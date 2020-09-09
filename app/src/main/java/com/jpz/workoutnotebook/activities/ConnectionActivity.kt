package com.jpz.workoutnotebook.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.injections.Injection
import com.jpz.workoutnotebook.injections.ViewModelFactory
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.utils.FirebaseUtils
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_connection.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ConnectionActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN: Int = 100
        private val TAG = ConnectionActivity::class.java.simpleName
    }

    private val firebaseUtils = FirebaseUtils()
    private val myUtils = MyUtils()

    private lateinit var userViewModel: UserViewModel

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
        configureViewModel()

        GlobalScope.launch {
            delay(2000L)
            if (firebaseUtils.isCurrentUserLogged()) {
                Log.i(TAG, "user logged = " + firebaseUtils.isCurrentUserLogged())
                myUtils.startMainActivity(this@ConnectionActivity)
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
                myUtils.startMainActivity(this)
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
                .setLogo(R.drawable.ic_launcher_background)
                .build(),
            RC_SIGN_IN
        )
    }

    //--------------------------------------------------------------------------------------

    private fun configureToolbar() {
        // Get the toolbar view inside the activity layout
        setSupportActionBar(toolbar)
    }

    //--------------------------------------------------------------------------------------

        private fun configureViewModel() {
        val viewModelFactory: ViewModelFactory = Injection.provideViewModelFactory(application)
            // Use the ViewModelProvider to associate the ViewModel with Activity
            userViewModel = ViewModelProvider(this, viewModelFactory).get(UserViewModel::class.java)
    }

    // Create the current user in Room when he is identified
    private fun createUser() {
        if (firebaseUtils.getCurrentUser() != null) {
            val userId: String = firebaseUtils.getCurrentUser()!!.uid
            val nickName: String? = firebaseUtils.getCurrentUser()!!.displayName
            val urlPhoto: String? = firebaseUtils.getCurrentUser()!!.photoUrl.toString()

            // Set data. By default firstName, name, sports, iFollow, followers are null and age = 0.
            val user = User(userId, nickName, null, null, 0, urlPhoto, null, null, null)
            userViewModel.createUser(user)
            Log.w(TAG, "user = $user")
        }
    }

}