package com.jpz.workoutnotebook.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.utils.FirebaseUtils
import com.jpz.workoutnotebook.utils.MyUtils
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN: Int = 100
        private val TAG = SplashActivity::class.java.simpleName
    }

    private val firebaseUtils = FirebaseUtils()
    private val myUtils = MyUtils()

    // Authentication providers
    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.FacebookBuilder().build()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        GlobalScope.launch {
            delay(3000L)
            if (firebaseUtils.isCurrentUserLogged()) {
                Log.i(TAG, "user logged = " + firebaseUtils.isCurrentUserLogged())
                myUtils.startMainActivity(this@SplashActivity)
                finish()
            } else {
                startSignInActivity()
            }
        }
    }

    //--------------------------------------------------------------------------------------

    @SuppressLint("SwitchIntDef")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                myUtils.showSnackBar(
                    splashActivityCoordinatorLayout,
                    R.string.authentication_succeed
                )
                myUtils.startMainActivity(this)
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                if (response == null) {
                    myUtils.showSnackBar(
                        splashActivityCoordinatorLayout,
                        R.string.sign_in_cancelled
                    )
                } else {
                    when (response.error?.errorCode) {
                        ErrorCodes.NO_NETWORK -> myUtils.showSnackBar(
                            splashActivityCoordinatorLayout,
                            R.string.error_no_internet
                        )
                        ErrorCodes.UNKNOWN_ERROR -> myUtils.showSnackBar(
                            splashActivityCoordinatorLayout,
                            R.string.error_unknown_error
                        )
                    }
                }
            }
        }
    }

    //--------------------------------------------------------------------------------------

    // Method to launch Sign-In Activity
    private fun startSignInActivity() {
        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.AppTheme)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false, true)
                .setLogo(R.drawable.ic_launcher_background)
                .build(),
            RC_SIGN_IN
        )
    }

}