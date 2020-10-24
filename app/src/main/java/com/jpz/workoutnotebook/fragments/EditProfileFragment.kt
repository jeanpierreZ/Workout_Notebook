package com.jpz.workoutnotebook.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.storage.StorageException
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.utils.RequestCodes.Companion.RC_CHOOSE_PHOTO
import kotlinx.android.synthetic.main.fragment_base_profile.*
import org.koin.android.ext.android.inject
import permissions.dispatcher.*


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = userAuth.getCurrentUser()?.uid
        Log.d(TAG, "uid = $userId")

        getUserDataToObject(userId)

        baseProfileFragmentPhoto.setOnClickListener {
            addPhotoWithPermissionCheck()
        }

        baseProfileFragmentFABSave.setOnClickListener {
            saveUpdatedData()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == RC_CHOOSE_PHOTO) {
            // Uri of picture selected by user
            uriPictureSelected = data?.data
            // Display the user photo with data
            uriPictureSelected?.let { displayNewPhoto(it) }
            newPhotoAdded = true
        }
    }

    //--------------------------------------------------------------------------------------
    // Display the user data from Firebase

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

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun addPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, RC_CHOOSE_PHOTO)
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
                it, user?.nickName, user?.name, user?.firstName, user?.age,
                user?.photoProfile, user?.sports, user?.iFollow, user?.followers
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun showRationaleForReadExternalStorage(request: PermissionRequest) {
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

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun onReadExternalStorageDenied() {
        myUtils.showSnackBar(baseProfileFragmentCoordinatorLayout, R.string.permission_denied)
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun onPermissionNeverAskAgain() {
        myUtils.showSnackBar(baseProfileFragmentCoordinatorLayout, R.string.never_ask_again)
    }
}