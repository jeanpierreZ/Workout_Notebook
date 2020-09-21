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
import androidx.core.widget.doOnTextChanged
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

    // Uri used to locate the device picture
    private var uriPictureSelected: Uri? = null

    // Boolean used if the user has chosen a photo
    private var newPhotoAdded: Boolean = false

    private var nickName: String? = null
    private var name: String? = null
    private var firstName: String? = null
    private var age: Int? = null
    private var photo: Int? = 0
    private var sports: String? = null
    private var iFollow: ArrayList<String>? = null
    private var followers: ArrayList<String>? = null
    private var exercisesList: ArrayList<String>? = null
    private var workoutsList: ArrayList<String>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = userAuth.getCurrentUser()?.uid
        Log.d(TAG, "uid = $userId")

        getUserData(userId)
        onChangeData()

        baseProfileFragmentFABSave.setOnClickListener {
            saveUpdatedData()
        }
        baseProfileFragmentFABCancel.setOnClickListener {
            activity?.finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == RC_CHOOSE_PHOTO) {
            // Uri of picture selected by user
            uriPictureSelected = data?.data
            // Display the user photo with data
            uriPictureSelected?.let { displayUserPhoto(it) }
            newPhotoAdded = true
        }
    }

    //--------------------------------------------------------------------------------------
    // Display the user data from Firebase

    private fun getUserData(userId: String?) {
        if (userId != null) {
            userViewModel.getUser(userId)?.addOnSuccessListener { documentSnapshot ->
                val user: User? = documentSnapshot.toObject(User::class.java)

                user?.let {
                    // Retrieve user data
                    photo = user.photo
                    Log.d(TAG, "user.photo = $photo")
                    iFollow = user.iFollow
                    followers = user.followers
                    exercisesList = user.exercisesList
                    workoutsList = user.workoutsList

                    view?.let {
                        if (activity != null) {
                            // Download the photo if it exists...
                            if (user.photo != null && user.photo != 0) {
                                // Download the photo from Firebase Storage
                                userStoragePhoto.storageRef(userId).downloadUrl.addOnSuccessListener { uri ->
                                    displayUserPhoto(uri)
                                }
                                // ...else display an icon for the photo
                            } else {
                                displayGenericPhoto()
                            }
                        }
                        displayUserData(user)
                    }
                }
            }
        }
    }

    //--------------------------------------------------------------------------------------
    // Update / save data

    private fun onChangeData() {

        baseProfileFragmentPhoto.setOnClickListener {
            addPhotoWithPermissionCheck()
        }
        baseProfileFragmentNickname.editText?.doOnTextChanged { text, _, _, _ ->
            nickName = text.toString()
        }
        baseProfileFragmentName.editText?.doOnTextChanged { text, _, _, _ ->
            name = text.toString()
        }
        baseProfileFragmentFirstName.editText?.doOnTextChanged { text, _, _, _ ->
            firstName = text.toString()
        }
        baseProfileFragmentAge.editText?.doOnTextChanged { text, _, _, _ ->
            age = text.toString().toIntOrNull()
        }
        baseProfileFragmentSports.editText?.doOnTextChanged { text, _, _, _ ->
            sports = text.toString()
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun addPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, RC_CHOOSE_PHOTO)
    }

    private fun uploadPhotoInFirebaseAndUpdateUser() {
        // Upload the picture local file chosen by the user
        if (userId != null && uriPictureSelected != null) {
            val uploadTask = userStoragePhoto.storageRef(userId!!).putFile(uriPictureSelected!!)
            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener { exception ->
                val errorCode = (exception as StorageException).errorCode
                val errorMessage = exception.message
                Log.e(TAG, "Unsuccessful upload: $errorCode $errorMessage")
            }.addOnSuccessListener { taskSnapshot ->
                Log.i(TAG, "Successful upload: ${taskSnapshot.totalByteCount}")
                // If a new photo is added, increment photo number value then update user data
                photo = if (photo == null) {
                    1
                } else {
                    photo?.plus(1)
                }
                Log.d(TAG, "new photo data = $photo")
                updateUser()
            }
        }
    }

    private fun updateUser() {
        val user = userId?.let { it ->
            User(
                it, nickName, name, firstName, age, photo, sports,
                iFollow, followers, exercisesList, workoutsList
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

    // Update data
    private fun saveUpdatedData() {
        if (newPhotoAdded) {
            uploadPhotoInFirebaseAndUpdateUser()
        } else {
            updateUser()
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