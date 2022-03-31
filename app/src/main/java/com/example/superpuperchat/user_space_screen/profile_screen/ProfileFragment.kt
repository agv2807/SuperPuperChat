package com.example.superpuperchat.user_space_screen.profile_screen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.superpuperchat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {
    private val REQUEST_CODE = 100

    private val profileViewModel = ProfileViewModel()

    private var profilePhoto: CircleImageView? = null
    private var userName: EditText? = null
    private var editNameIcon: ImageView? = null
    private var completeIcon: ImageView? = null
    private var logOutIc: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        setupViews(view)

        profileViewModel.profileInfo.observe(requireActivity()) {
            setUserInfo(it)
        }

        profileViewModel.dataToast.observe(requireActivity()) {
            if (!it.isNullOrEmpty())
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun setupViews(view: View) {
        profilePhoto = view.findViewById(R.id.profile_photo)
        userName = view.findViewById(R.id.user_name)
        editNameIcon = view.findViewById(R.id.edit_icon)
        completeIcon = view.findViewById(R.id.complete_icon)
        logOutIc = view.findViewById(R.id.log_out_ic)

        editNameIcon?.setOnClickListener {
            editUserName()
        }

        completeIcon?.setOnClickListener {
            completeEditName()
        }

        profilePhoto?.setOnClickListener {
            openGallery()
        }

        logOutIc?.setOnClickListener {
            openAlertDialog()
        }
    }

    private fun setUserInfo(user: FirebaseUser?) {
        userName?.setText(user?.displayName)

        Picasso.with(context)
            .load(user?.photoUrl)
            .placeholder(R.drawable.man)
            .into(profilePhoto)
    }

    private fun openAlertDialog() {
        val dialogFragment = MyDialogFragment()
        val manager = activity?.supportFragmentManager!!
        dialogFragment.show(manager, "myDialog")
    }

    private fun editUserName() {
        userName?.isEnabled = true
        editNameIcon?.visibility = View.GONE
        completeIcon?.visibility = View.VISIBLE
        userName?.requestFocus()
    }

    private fun completeEditName() {
        if (userName!!.text.isEmpty()){
            userName?.setText(profileViewModel.profileInfo.value?.displayName)
        } else {
            profileViewModel.updateUserName(userName?.text.toString())
        }
        userName?.isEnabled = false
        editNameIcon?.visibility = View.VISIBLE
        completeIcon?.visibility = View.GONE
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            if (data?.data != null) {
                Picasso.with(context)
                    .load(data.data)
                    .placeholder(R.drawable.man)
                    .into(profilePhoto)
                data.data?.let {
                    profileViewModel.uploadFile(it)
                }
            }
        }
    }

    override fun onPause() {
        profileViewModel.dataToast.value = null
        super.onPause()
    }
}