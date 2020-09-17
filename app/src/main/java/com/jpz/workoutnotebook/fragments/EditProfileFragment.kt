package com.jpz.workoutnotebook.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.storage.StorageException
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.api.UserAuth
import com.jpz.workoutnotebook.api.UserStoragePhoto
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import permissions.dispatcher.*


@RuntimePermissions
class EditProfileFragment : Fragment() {

    companion object {
        private val TAG = EditProfileFragment::class.java.simpleName
        const val RC_CHOOSE_PHOTO = 300
    }

    private val userAuth: UserAuth by inject()
    private val userViewModel: UserViewModel by viewModel()
    private val userStoragePhoto: UserStoragePhoto by inject()
    private val myUtils: MyUtils by inject()

    // Uri used to locate the device picture
    private var uriPictureSelected: Uri? = null

    // Used if the user has chosen a photo
    private var newPhotoAdded: Boolean = false

    private var userId: String? = null
    private var nickName: String? = null
    private var name: String? = null
    private var firstName: String? = null
    private var age: Int? = null
    private var photo: Int? = 0
    private var sports: String? = null
    private var iFollow: ArrayList<String>? = null
    private var followers: ArrayList<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = userAuth.getCurrentUser()?.uid

        displayUserData(userId)
        onChangeData()
        editProfileFragmentFABSave.setOnClickListener {
            saveUpdatedData()
        }
        editProfileFragmentFABCancel.setOnClickListener {
            activity?.finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == RC_CHOOSE_PHOTO) {
            // Uri of picture selected by user
            uriPictureSelected = data?.data
            // Display the user photo with data
            view?.let {
                Glide.with(it)
                    .load(uriPictureSelected)
                    .circleCrop()
                    .into(editProfileFragmentPhoto)
            }
            newPhotoAdded = true
        }
    }

    //--------------------------------------------------------------------------------------
    // Display the user data from Firebase

    private fun displayUserData(userId: String?) {
        if (userId != null) {
            userViewModel.getUser(userId)?.addOnSuccessListener { documentSnapshot ->
                val user: User? = documentSnapshot.toObject(User::class.java)

                user?.let {
                    // Retrieve data user
                    photo = user.photo
                    iFollow = user.iFollow
                    followers = user.followers

                    view?.let {
                        if (activity != null) {
                            // Download the photo if it exists...
                            if (user.photo != 0) {
                                // Download the photo from Firebase Storage
                                userStoragePhoto.storageRef(userId).downloadUrl.addOnSuccessListener { uri ->
                                    myUtils.displayUserPhoto(
                                        activity!!, uri, editProfileFragmentPhoto
                                    )
                                }
                                // ...else display an icon for the photo
                            } else {
                                myUtils.displayGenericPhoto(activity!!, editProfileFragmentPhoto)
                            }
                        }
                        myUtils.displayUserData(
                            user,
                            editProfileFragmentNickname,
                            editProfileFragmentName,
                            editProfileFragmentFirstName,
                            editProfileFragmentAge,
                            editProfileFragmentSports
                        )
                    }
                }
            }
        }
    }

    //--------------------------------------------------------------------------------------
    // Update / save data

    private fun onChangeData() {
        editProfileFragmentPhoto.setOnClickListener {
            addPhotoWithPermissionCheck()
        }
        editProfileFragmentNickname.editText?.doOnTextChanged { text, _, _, _ ->
            nickName = text.toString()
        }
        editProfileFragmentName.editText?.doOnTextChanged { text, _, _, _ ->
            name = text.toString()
        }
        editProfileFragmentFirstName.editText?.doOnTextChanged { text, _, _, _ ->
            firstName = text.toString()
        }
        editProfileFragmentAge.editText?.doOnTextChanged { text, _, _, _ ->
            age = text.toString().toIntOrNull()
        }
        editProfileFragmentSports.editText?.doOnTextChanged { text, _, _, _ ->
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
                // If a new photo is added, set boolean photo to true then update user data
                photo = photo?.plus(1)
                updateUser()
            }
        }
    }

    private fun updateUser() {
        val user = userId?.let { it ->
            User(it, nickName, name, firstName, age, photo, sports, iFollow, followers)
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
        myUtils.showSnackBar(editProfileFragmentCoordinatorLayout, R.string.permission_denied)
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun onPermissionNeverAskAgain() {
        myUtils.showSnackBar(editProfileFragmentCoordinatorLayout, R.string.never_ask_again)
    }
}