package com.example.superpuperchat.sign_screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.superpuperchat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInFragment : Fragment() {

    private var auth: FirebaseAuth? = null

    private var emailEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var signInButton: Button? = null
    private var routeSignUpTextView: TextView? = null
    private var loader: ProgressBar? = null
    private var mainCont: LinearLayout? = null

    private val mainActivity: MainActivity
        get() = activity as MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_in_screen, container, false)

        setupViews(view)

        auth = Firebase.auth

        return view
    }

    private fun setupViews(view: View) {
        emailEditText = view.findViewById(R.id.login_email)
        passwordEditText = view.findViewById(R.id.login_password)
        signInButton = view.findViewById(R.id.signin_button)
        routeSignUpTextView = view.findViewById(R.id.open_sign_up)
        loader = view.findViewById(R.id.loader)
        mainCont = view.findViewById(R.id.main_container)

        signInButton?.setOnClickListener {
            signIn(emailEditText?.text.toString(), passwordEditText?.text.toString())
        }

        routeSignUpTextView?.setOnClickListener {
            mainActivity.routeToSignUp()
        }
    }

    private fun signIn(email: String, password: String) {
        loader?.visibility = View.VISIBLE
        mainCont?.visibility = View.GONE

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
            loader?.visibility = View.GONE
            mainCont?.visibility = View.VISIBLE
        } else {
            auth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        mainActivity.routeChat()
                    } else {
                        Toast.makeText(activity, task.exception.toString(), Toast.LENGTH_SHORT)
                            .show()

                        loader?.visibility = View.GONE
                        mainCont?.visibility = View.VISIBLE
                    }
                }
        }
    }

}