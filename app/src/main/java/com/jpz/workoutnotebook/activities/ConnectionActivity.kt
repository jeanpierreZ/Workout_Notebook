package com.jpz.workoutnotebook.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.databinding.ActivityConnectionBinding
import com.jpz.workoutnotebook.repositories.UserAuth
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.UserViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class ConnectionActivity : AppCompatActivity() {

    companion object {
        private val TAG = ConnectionActivity::class.java.simpleName
        private const val AUTH_USER_ID = "userId"
    }

    private lateinit var binding: ActivityConnectionBinding

    private val userAuth: UserAuth by inject()
    private val userViewModel: UserViewModel by viewModel()
    private val myUtils: MyUtils by inject()

    // Authentication providers
    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.FacebookBuilder().build()
    )

    private var startActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnectionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        configureToolbar()

        GlobalScope.launch {
            delay(MyUtils.DELAY)
            if (userAuth.isCurrentUserLogged()) {
                Log.i(TAG, "user logged = " + userAuth.isCurrentUserLogged())
                startMainActivity()
                finish()
            } else {
                startSignInActivity()
            }
        }

        @SuppressLint("SwitchIntDef")
        // Used to handle intent from startSignInActivity()
        startActivityForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result: ActivityResult ->
                val response = IdpResponse.fromResultIntent(result.data)
                if (result.resultCode == Activity.RESULT_OK) {
                    // Successfully signed in
                    myUtils.showSnackBar(
                        binding.connectionActivityCoordinatorLayout,
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
                            binding.connectionActivityCoordinatorLayout,
                            R.string.sign_in_cancelled
                        )
                    } else {
                        when (response.error?.errorCode) {
                            ErrorCodes.NO_NETWORK -> myUtils.showSnackBar(
                                binding.connectionActivityCoordinatorLayout,
                                R.string.error_no_internet
                            )
                            ErrorCodes.UNKNOWN_ERROR -> myUtils.showSnackBar(
                                binding.connectionActivityCoordinatorLayout,
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
        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setTheme(R.style.AppThemeFirebaseAuth)
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false, true)
            .build()
        startActivityForResult.launch(intent)
    }

    //--------------------------------------------------------------------------------------

    private fun configureToolbar() {
        // Get the toolbar view inside the activity layout
        setSupportActionBar(binding.includedLayout.toolbar)
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