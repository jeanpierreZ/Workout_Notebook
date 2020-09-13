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
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.utils.FirebaseUtils
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import permissions.dispatcher.*


@RuntimePermissions
class EditProfileFragment : Fragment() {

    companion object {
        private val TAG = EditProfileFragment::class.java.simpleName
        const val RC_CHOOSE_PHOTO = 300
    }

    private val userViewModel: UserViewModel by viewModel()
    private val myUtils = MyUtils()
    private var uriPictureSelected: Uri? = null

    private var userId: String? = null
    private var nickName: String? = null
    private var name: String? = null
    private var firstName: String? = null
    private var age: Int? = null
    private var photo: String? = null
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

        val firebaseUtils = FirebaseUtils()
        userId = firebaseUtils.getCurrentUser()?.uid

        displayUserData(userId)
        onChangeData()
        editProfileFragmentFABSave.setOnClickListener {
            saveUpdatedData(userId)
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
            // Create the user photo with data
            photo = uriPictureSelected.toString()
            view?.let {
                Glide.with(it)
                    .load(photo)
                    .circleCrop()
                    .into(editProfileFragmentPhoto)
            }
        }
    }

    //--------------------------------------------------------------------------------------
    // Display the user data from Firebase

    private fun displayUserData(userId: String?) {
        if (userId != null) {
            userViewModel.getUser(userId)?.addOnSuccessListener { documentSnapshot ->
                val user: User? = documentSnapshot.toObject(User::class.java)
                user?.let {
                    photo = user.photo
                    iFollow = user.iFollow
                    followers = user.followers
                    view?.let {
                        if (photo != null) {
                            Glide.with(it)
                                .load(photo)
                                .circleCrop()
                                .into(editProfileFragmentPhoto)
                        } else {
                            editProfileFragmentPhoto.background =
                                activity?.let { activity ->
                                    ContextCompat.getDrawable(
                                        activity, R.drawable.ic_baseline_person_pin_24
                                    )
                                }
                        }
                    }
                    editProfileFragmentNickname.editText?.setText(user.nickName)
                    editProfileFragmentName.editText?.setText(user.name)
                    editProfileFragmentFirstName.editText?.setText(user.firstName)
                    editProfileFragmentAge.editText?.setText(user.age.toString())
                    editProfileFragmentSports.editText?.setText(user.sports)
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

    private fun uploadPhotoInFirebase() {
        // Instance of FirebaseStorage with point to the root reference
        val storageRef = Firebase.storage.reference
        // Use variables to create child values
        // Points to "photos/userID"
        val photosRef = storageRef.child("photos")
        val fileName = userId
        val spaceRef = fileName?.let { photosRef.child(it) }
        // Upload the picture local file chosen by the user
        val uploadTask = uriPictureSelected?.let { spaceRef?.putFile(it) }
        uploadTask
            ?.addOnSuccessListener { taskSnapshot ->
                Log.d(TAG, "Upload Storage successful")
                taskSnapshot?.storage?.downloadUrl?.addOnCompleteListener {
                    // Set photo data with storage path
                    photo = uploadTask.result.toString()
                }
            }
            ?.addOnFailureListener { e ->
                Log.e(TAG, "Upload Storage failed = $e")
            }
    }

    private fun saveUpdatedData(userId: String?) {
        // Update data
        uploadPhotoInFirebase()
        // todo : wait for photo complete listener before update user ?
        val user = userId?.let { it1 ->
            User(it1, nickName, name, firstName, age, photo, sports, iFollow, followers)
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
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.permission_needed)
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