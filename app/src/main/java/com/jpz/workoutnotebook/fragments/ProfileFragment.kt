package com.jpz.workoutnotebook.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.DocumentChange
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.models.User
import kotlinx.android.synthetic.main.fragment_base_profile.*


class ProfileFragment : BaseProfileFragment() {

    companion object {
        private val TAG = ProfileFragment::class.java.simpleName
    }

    //--------------------------------------------------------------------------------------
    // For disconnect menu

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_disconnect, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.menu_disconnect) {
            disconnectCurrentUser()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    //--------------------------------------------------------------------------------------

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Disable EditText and counter
        baseProfileFragmentNickname.editText?.isEnabled = false
        baseProfileFragmentNickname.isCounterEnabled = false
        baseProfileFragmentName.editText?.isEnabled = false
        baseProfileFragmentName.isCounterEnabled = false
        baseProfileFragmentFirstName.editText?.isEnabled = false
        baseProfileFragmentFirstName.isCounterEnabled = false
        baseProfileFragmentAge.editText?.isEnabled = false
        baseProfileFragmentAge.isCounterEnabled = false
        baseProfileFragmentSports.editText?.isEnabled = false
        baseProfileFragmentSports.isCounterEnabled = false

        // Disable FloatingActionButton
        baseProfileFragmentFABSave.isEnabled = false
        baseProfileFragmentFABSave.visibility = View.GONE

        val userId = userAuth.getCurrentUser()?.uid
        Log.d(TAG, "uid = $userId")

        userId?.let { getCurrentUserDataInRealTime(it) }
    }

    //--------------------------------------------------------------------------------------
    // Listener of current user data in real time from Firebase

    private fun getCurrentUserDataInRealTime(userId: String) {
        userViewModel.getCurrentUser(userId)?.addSnapshotListener { snapshot, e ->

            if (e != null) {
                Log.w(TAG, "listen:error", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                for (dc in snapshot.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED || dc.type == DocumentChange.Type.MODIFIED) {
                        val user: User? = dc.document.toObject(User::class.java)
                        user?.let {
                            Log.d(TAG, "user = $user")
                            // Display user data with binding
                            binding.user = user
                        }
                    }
                }
            }
        }
    }

    //--------------------------------------------------------------------------------------

    private fun disconnectCurrentUser() {
        // Create an alert dialog to prevent the user
        activity?.let { activity ->
            AlertDialog.Builder(activity)
                .setMessage(R.string.disconnect)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // Disconnect the user
                    userAuth.signOut(activity)
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                }
                .show()
        }
    }
}