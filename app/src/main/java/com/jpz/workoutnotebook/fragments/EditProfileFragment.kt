package com.jpz.workoutnotebook.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.storage.StorageException
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.utils.MyUtils
import kotlinx.android.synthetic.main.fragment_base_profile.*
import org.koin.android.ext.android.inject
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructPermissionsRequest


@RuntimePermissions
class EditProfileFragment : BaseProfileFragment() {

    companion object {
        private val TAG = EditProfileFragment::class.java.simpleName
    }

    private val myUtils: MyUtils by inject()

    private var user: User? = null

    // Uri used to locate the device picture
    private var uriPictureSelected: Uri? = null

    // Boolean used if the user has chosen a photo
    private var newPhotoAdded: Boolean = false

    private var startActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    // To request permission
    private lateinit var permissionsRequester: PermissionsRequester

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // constructPermissionsRequest must be invoked every time an activity is created
        permissionsRequester = constructPermissionsRequest(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            onShowRationale = ::showRationaleForReadExternalStorage,
            onPermissionDenied = ::onReadExternalStorageDenied,
            onNeverAskAgain = ::onPermissionNeverAskAgain
        ) {
            addPhoto()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Used to handle intent from addPhoto()
        startActivityForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // Uri of picture selected by user
                    uriPictureSelected = result.data?.data
                    Log.d(TAG, "uriPictureSelected = $uriPictureSelected")
                    // Display the user photo with data
                    uriPictureSelected?.let { displayNewPhoto(it) }
                    newPhotoAdded = true
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = userAuth.getCurrentUser()?.uid
        Log.d(TAG, "uid = $userId")

        getUserDataToObject(userId)

        baseProfileFragmentPhoto.setOnClickListener {
            permissionsRequester.launch()
        }

        baseProfileFragmentFABSave.setOnClickListener {
            saveUpdatedData()
        }
    }

    //--------------------------------------------------------------------------------------
    // Get the user data to User Object from Firebase

    private fun getUserDataToObject(userId: String?) {
        userId?.let {
            userViewModel.getUser(it)?.addOnSuccessListener { documentSnapshot ->
                user = documentSnapshot.toObject(User::class.java)
                user?.let { binding.user = user }
            }
        }
    }

    //--------------------------------------------------------------------------------------
    // Update / save data

    private fun addPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult.launch(intent)
    }

    private fun uploadPhotoInFirebaseAndUpdateUser() {
        // Upload the picture local file chosen by the user
        if (userId != null && uriPictureSelected != null) {

            val referenceToStorage = userStoragePhoto.storageRef(userId!!)

            val uploadTask = referenceToStorage.putFile(uriPictureSelected!!)

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener { exception ->
                val errorCode = (exception as StorageException).errorCode
                val errorMessage = exception.message
                Log.e(TAG, "Unsuccessful upload: $errorCode $errorMessage")
            }
                .addOnSuccessListener { taskSnapshot ->
                    referenceToStorage.downloadUrl.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Download uri from Firebase Storage
                            val downloadUri = task.result
                            // Convert uri to url and pass it as photoProfile
                            user?.photoProfile = downloadUri?.toString()
                            Log.d(TAG, "photoProfile: $user?.photoProfile")
                            // Then update User
                            updateUser()
                        }
                    }
                    Log.i(TAG, "Successful upload: ${taskSnapshot.totalByteCount}")
                }
        }
    }

    private fun updateUser() {
        val user = userId?.let { it ->
            User(
                it, user?.nickName, user?.name, user?.firstName,
                user?.age, user?.photoProfile, user?.sports
            )
        }
        if (user != null) {
            Log.d(TAG, "user = $user")
            userViewModel.updateUser(user)?.addOnSuccessListener {
                activity?.setResult(Activity.RESULT_OK)
                activity?.finish()
            }?.addOnFailureListener {
                activity?.setResult(Activity.RESULT_CANCELED)
                activity?.finish()
            }
        }
    }

    private fun saveUpdatedData() {
        if (newPhotoAdded) {
            uploadPhotoInFirebaseAndUpdateUser()
        } else {
            updateUser()
        }
    }

    //--------------------------------------------------------------------------------------

    private fun displayNewPhoto(uri: Uri) {
        activity?.let {
            Glide.with(it)
                .load(uri)
                .circleCrop()
                .into(baseProfileFragmentPhoto)
        }
    }

    //--------------------------------------------------------------------------------------
    // Permissions

    private fun onReadExternalStorageDenied() {
        myUtils.showSnackBar(baseProfileFragmentCoordinatorLayout, R.string.permission_denied)
    }

    private fun showRationaleForReadExternalStorage(request: PermissionRequest) {
        AlertDialog.Builder(activity)
            .setTitle(R.string.permission_needed)
            .setMessage(R.string.rationale_permission_read_external_storage)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                request.proceed()
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                request.cancel()
            }
            .show()
    }

    private fun onPermissionNeverAskAgain() {
        myUtils.showSnackBar(baseProfileFragmentCoordinatorLayout, R.string.never_ask_again)
    }
}