package com.example.superpuperchat.profile_screen

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

    private val auth: FirebaseAuth = Firebase.auth
    private val user = auth.currentUser
    private val usersDatabase: DatabaseReference = Firebase.database.reference
    private val storageRef = Firebase.storage.reference

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
        setUserInfo(user)

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
            userName?.setText(user?.displayName)
        } else {
            updateUserName(userName?.text.toString())
        }
        userName?.isEnabled = false
        editNameIcon?.visibility = View.VISIBLE
        completeIcon?.visibility = View.GONE
    }

    private fun setUserInfo(user: FirebaseUser?) {
        userName?.setText(user?.displayName)

        Picasso.with(context)
            .load(user?.photoUrl)
            .placeholder(R.drawable.man)
            .into(profilePhoto)
    }

    private fun updateUserName(userName: String? =  null) {
        usersDatabase.child("users").child(user!!.uid).child("name").setValue(userName)
        val nameUpdate = userProfileChangeRequest {
            displayName = userName ?: displayName
        }
        user.updateProfile(nameUpdate)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Имя изменено", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, task.exception.toString(), Toast.LENGTH_SHORT).show()
                }
                this.userName?.setText(user.displayName)
            }
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
                    uploadFile(it)
                }
            }
        }
    }

    private fun updatePhoto(image: Uri?) {
        usersDatabase.child("users").child(user!!.uid).child("uri").setValue(image.toString())
        val profileUpdates = userProfileChangeRequest {
            photoUri = image ?: photoUri
        }
        auth.currentUser?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Фото обновлено", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun generateId(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..8)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun uploadFile(imageUri: Uri) {
        val ref = storageRef.child("images/${generateId()}")
        val uploadTask = ref.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updatePhoto(task.result)
            }
        }
    }

}