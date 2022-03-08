package com.example.superpuperchat.sign_screen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.superpuperchat.R
import com.example.superpuperchat.models.User
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

class SignUpFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val usersDatabase: DatabaseReference = Firebase.database.reference
    private val storageRef = Firebase.storage.reference

    private val REQUEST_CODE = 100
    private var imageUri: Uri? = null

    private lateinit var emailEditText: EditText
    private lateinit var userNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registrationButton: Button
    private lateinit var backTextView: TextView
    private lateinit var addPhotoButton: ImageView
    private lateinit var addedImage: CircleImageView
    private lateinit var loader: ProgressBar
    private lateinit var mainCont: LinearLayout

    private val mainActivity: MainActivity
        get() = activity as MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register_screen, container, false)

        setupViews(view)

        auth = Firebase.auth

        return view
    }

    private fun setupViews(view: View) {
        emailEditText = view.findViewById(R.id.email_edittext)
        userNameEditText = view.findViewById(R.id.enter_name_registration)
        passwordEditText = view.findViewById(R.id.enter_password_edittext)
        confirmPasswordEditText = view.findViewById(R.id.confirm_password_edittext)
        registrationButton = view.findViewById(R.id.registration_button)
        backTextView = view.findViewById(R.id.back)
        addPhotoButton = view.findViewById(R.id.add_photo)
        addedImage = view.findViewById(R.id.added_image)
        loader = view.findViewById(R.id.loader)
        mainCont = view.findViewById(R.id.main_container)

        registrationButton.setOnClickListener {
            signUp(emailEditText.text.toString(), passwordEditText.text.toString(), userNameEditText.text.toString())
        }

        backTextView.setOnClickListener {
            mainActivity.routeToSignIn()
        }

        addPhotoButton.setOnClickListener {
            openGallery()
        }
    }

    private fun signUp(email: String, password: String, userName: String) {
        if (email.isEmpty() || password.isEmpty() || userName.isEmpty()) {
            Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
        } else {
            loader.visibility = View.VISIBLE
            mainCont.visibility = View.GONE

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(mainActivity) { task ->
                    if (task.isSuccessful) {
                        if (imageUri == null) {
                            updateUserInfo(userNameEditText.text.toString())
                        } else {
                            uploadFile(imageUri!!)
                        }
                    } else {
                        Toast.makeText(activity, task.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
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
                imageUri = data.data!!
                addedImage.visibility = View.VISIBLE
                Picasso.with(context)
                    .load(imageUri)
                    .placeholder(R.drawable.man)
                    .into(addedImage)
            }
        }
    }

    private fun generateId(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..8)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun uploadFile(image: Uri) {
        val ref = storageRef.child("images/${generateId()}")
        val uploadTask = ref.putFile(image)

        uploadTask.continueWithTask { task ->
            if (task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                updateUserInfo(userNameEditText.text.toString(), downloadUri)
            }
        }
    }

    private fun updateUserInfo(name: String? = null, image: Uri? = null) {
        val profileUpdates = userProfileChangeRequest {
            displayName = name ?: displayName
            photoUri = image ?: photoUri
        }
        auth.currentUser?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    createNewUser(user)
                    mainActivity.routeChat()
                }
            }
    }

    private fun createNewUser(user: FirebaseUser?) {
        val newUser = User(user?.uid ?: "", user?.displayName.toString(), user?.photoUrl.toString())
        usersDatabase.child("users").child(user?.uid ?: "").setValue(newUser)
    }
}