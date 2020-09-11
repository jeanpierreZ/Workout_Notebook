package com.jpz.workoutnotebook.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.firebase.ui.auth.AuthUI
import com.google.android.material.tabs.TabLayoutMediator
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.adapters.ViewPagerAdapter
import com.jpz.workoutnotebook.fragments.ProfileFragment
import com.jpz.workoutnotebook.utils.MyUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val EDIT = "EDIT"
        private const val RC_EDIT_PROFILE: Int = 200
    }

    private var fabSelected = 0
    private var profileFAB = 4
    private val myUtils = MyUtils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureToolbar()
        configureViewPagerAdapter()
        configureTabLayout()
        animateFAB()
        mainActivityFABEditProfile.setOnClickListener {
            editProfile()
        }
        mainActivityFABDisconnect.setOnClickListener {
            disconnectCurrentUser()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_EDIT_PROFILE) {
            when (resultCode) {
                RESULT_OK -> myUtils.showSnackBar(
                    mainActivityCoordinatorLayout, R.string.user_data_updated
                )
                RESULT_CANCELED -> myUtils.showSnackBar(
                    mainActivityCoordinatorLayout, R.string.user_data_recovery_error
                )
            }
        }
    }

    //--------------------------------------------------------------------------------------
    // UI

    private fun configureToolbar() {
        // Get the toolbar view inside the activity layout
        setSupportActionBar(toolbar)
    }

    private fun configureViewPagerAdapter() {
        val viewPagerAdapter = ViewPagerAdapter(this)
        mainActivityViewPager.adapter = viewPagerAdapter
    }

    private fun configureTabLayout() {
        TabLayoutMediator(mainActivityTabLayout, mainActivityViewPager) { tab, position ->
            tab.text = "OBJECT ${(position + 1)}"
        }.attach()
    }

    //--------------------------------------------------------------------------------------

    // FAB disappear and appear when a page is scrolled or selected
    private fun animateFAB() {
        mainActivityViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                fabSelected = position
                if (profileFAB == fabSelected) {
                    mainActivityFABEditProfile.show()
                    mainActivityFABDisconnect.show()
                } else {
                    mainActivityFABEditProfile.hide()
                    mainActivityFABDisconnect.hide()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager2.SCROLL_STATE_IDLE && profileFAB == fabSelected) {
                    mainActivityFABEditProfile.show()
                    mainActivityFABDisconnect.show()
                }
                if (state == ViewPager2.SCROLL_STATE_DRAGGING && profileFAB == fabSelected) {
                    mainActivityFABEditProfile.hide()
                    mainActivityFABDisconnect.hide()
                }
            }
        })
    }

    //--------------------------------------------------------------------------------------

    private fun editProfile() {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra(EDIT, ProfileFragment::class.java.name)
        startActivityForResult(intent, RC_EDIT_PROFILE)
    }

    private fun disconnectCurrentUser() {
        // Create an alert dialog to prevent the user
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.disconnect)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                AuthUI.getInstance().signOut(this).addOnSuccessListener {
                    val intent = Intent(this, ConnectionActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
            }
        builder.show()
    }
}