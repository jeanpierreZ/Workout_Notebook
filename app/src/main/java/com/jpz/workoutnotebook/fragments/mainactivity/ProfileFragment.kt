package com.jpz.workoutnotebook.fragments.mainactivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.DocumentChange
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.ConnectionActivity
import com.jpz.workoutnotebook.fragments.BaseProfileFragment
import com.jpz.workoutnotebook.models.User


class ProfileFragment : BaseProfileFragment() {

    companion object {
        private val TAG = ProfileFragment::class.java.simpleName
    }

    //--------------------------------------------------------------------------------------
    // For disconnect menu

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
        binding.baseProfileFragmentNickname.editText?.isEnabled = false
        binding.baseProfileFragmentNickname.isCounterEnabled = false
        binding.baseProfileFragmentName.editText?.isEnabled = false
        binding.baseProfileFragmentName.isCounterEnabled = false
        binding.baseProfileFragmentFirstName.editText?.isEnabled = false
        binding.baseProfileFragmentFirstName.isCounterEnabled = false
        binding.baseProfileFragmentAge.editText?.isEnabled = false
        binding.baseProfileFragmentAge.isCounterEnabled = false
        binding.baseProfileFragmentSports.editText?.isEnabled = false
        binding.baseProfileFragmentSports.isCounterEnabled = false

        // Disable FloatingActionButton
        binding.includedLayout.fabSave.isEnabled = false
        binding.includedLayout.fabSave.visibility = View.GONE

        getCurrentUserDataInRealTime()
    }

    //--------------------------------------------------------------------------------------
    // Listener of current user data in real time from Firebase

    private fun getCurrentUserDataInRealTime() {
        userViewModel.getCurrentUserData().addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "listen:error", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                for (dc in snapshot.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED || dc.type == DocumentChange.Type.MODIFIED) {
                        val user: User = dc.document.toObject(User::class.java)
                        Log.d(TAG, "user = $user")
                        // Display user data with binding
                        binding?.user = user
                    }
                }
            }
        }
    }

    //--------------------------------------------------------------------------------------

    // Method to disconnect the user
    private fun signOut() {
        userViewModel.getInstanceOfAuthUI()
            .signOut(requireActivity())
            .addOnSuccessListener {
                val intent = Intent(activity, ConnectionActivity::class.java)
                startActivity(intent, null)
                activity?.finish()
            }
    }

    private fun disconnectCurrentUser() {
        // Create an alert dialog to prevent the user
        activity?.let { activity ->
            AlertDialog.Builder(activity)
                .setMessage(R.string.disconnect)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // Disconnect the user
                    signOut()
                }
                .setNegativeButton(android.R.string.cancel) { _, _ -> }
                .show()
        }
    }
}