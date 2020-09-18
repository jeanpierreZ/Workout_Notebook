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
import com.jpz.workoutnotebook.api.UserAuth
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_connection.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class ConnectionActivity : AppCompatActivity() {

    companion object {
        private val TAG = ConnectionActivity::class.java.simpleName
        private const val RC_SIGN_IN: Int = 100
        private const val AUTH_USER_ID = "userId"
    }

    private val userAuth: UserAuth by inject()
    private val userViewModel: UserViewModel by viewModel()
    private val myUtils: MyUtils by inject()

    // Authentication providers
    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.FacebookBuilder().build()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection)

        configureToolbar()

        GlobalScope.launch {
            delay(2000L)
            if (userAuth.isCurrentUserLogged()) {
                Log.i(TAG, "user logged = " + userAuth.isCurrentUserLogged())
                startMainActivity()
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
                    connectionActivityCoordinatorLayout,
                    R.string.authentication_succeed
                )
                createUser()
                startMainActivity()
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                if (response == null) {
                    myUtils.showSnackBar(
                        connectionActivityCoordinatorLayout,
                        R.string.sign_in_cancelled
                    )
                } else {
                    when (response.error?.errorCode) {
                        ErrorCodes.NO_NETWORK -> myUtils.showSnackBar(
                            connectionActivityCoordinatorLayout,
                            R.string.error_no_internet
                        )
                        ErrorCodes.UNKNOWN_ERROR -> myUtils.showSnackBar(
                            connectionActivityCoordinatorLayout,
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
                .setTheme(R.style.AppThemeFirebaseAuth)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false, true)
                .build(),
            RC_SIGN_IN
        )
    }

    //--------------------------------------------------------------------------------------

    private fun configureToolbar() {
        // Get the toolbar view inside the activity layout
        setSupportActionBar(toolbar)
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    //--------------------------------------------------------------------------------------

    // Create the user in Firestore when he is identified
    private fun createUser() {
        if (userAuth.getCurrentUser() != null) {
            val userId: String = userAuth.getCurrentUser()!!.uid
            val data = hashMapOf(AUTH_USER_ID to userId)
            userViewModel.createUser(userId, data)
        }
    }
}