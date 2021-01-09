package com.jpz.workoutnotebook.fragments.editactivity

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
import com.jpz.workoutnotebook.fragments.BaseProfileFragment
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.utils.MyUtils
import org.koin.android.ext.android.inject
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructPermissionsRequest


@RuntimePermissions
class EditProfileFragment : BaseProfileFragment() {

    companion object {
        private val TAG = EditProfileFragment::class.java.simpleName
        private const val START_DELAY = 500L
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

        getUserDataToObject()

        binding.baseProfileFragmentPhoto.setOnClickListener {
            permissionsRequester.launch()
        }

        myUtils.scaleViewAnimation(binding.includedLayout.fabSave, START_DELAY)

        binding.includedLayout.fabSave.setOnClickListener {
            saveUpdatedData()
        }
    }

    //--------------------------------------------------------------------------------------
    // Get the user data to User Object from Firestore

    private fun getUserDataToObject() {
        userViewModel.getUser()?.addOnSuccessListener { documentSnapshot ->
            user = documentSnapshot.toObject(User::class.java)
            user?.let { binding.user = user }
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
        if (uriPictureSelected != null) {
            val referenceToStorage = userViewModel.storageRef()

            val uploadTask = referenceToStorage?.putFile(uriPictureSelected!!)

            // Register observers to listen for when the download is done or if it fails
            uploadTask?.addOnFailureListener { exception ->
                val errorCode = (exception as StorageException).errorCode
                val errorMessage = exception.message
                Log.e(TAG, "Unsuccessful upload: $errorCode $errorMessage")
            }
                ?.addOnSuccessListener { taskSnapshot ->
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
        user?.let { it ->
            Log.d(TAG, "user = $it")
            userViewModel.updateUser(it).addOnSuccessListener {
                activity?.setResult(Activity.RESULT_OK)
                activity?.finish()
            }.addOnFailureListener { e ->
                Log.e("updateUser", "Error updating document", e)
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
                .into(binding.baseProfileFragmentPhoto)
        }
    }

    //--------------------------------------------------------------------------------------
    // Permissions

    private fun onReadExternalStorageDenied() {
        myUtils.showSnackBar(
            binding.baseProfileFragmentCoordinatorLayout, R.string.permission_denied
        )
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
        myUtils.showSnackBar(binding.baseProfileFragmentCoordinatorLayout, R.string.never_ask_again)
    }
}