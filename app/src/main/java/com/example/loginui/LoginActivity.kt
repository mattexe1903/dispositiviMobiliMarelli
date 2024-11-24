package com.example.loginui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import com.example.loginui.databinding.LoginActivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginActivityBinding
    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        checkIfUserLoggedIn()
        secretPassword()
        checkLoginCredential()
        //createAnAccount()
    }


    //function

    private fun checkIfUserLoggedIn(){
        val currentUser : FirebaseUser? = firebaseAuth.currentUser
        if(currentUser!=null){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun secretPassword(){
        val passwordEditText: EditText = findViewById(R.id.edPassword)
        passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        passwordEditText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (passwordEditText.inputType != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    passwordEditText.setSelection(passwordEditText.text.length)
                    Handler(Looper.getMainLooper()).postDelayed({
                        passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        passwordEditText.setSelection(passwordEditText.text.length)
                    }, 3000)
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }
    }

    private fun checkLoginCredential(){
        binding.loginButton.setOnClickListener{
            val email = binding.edUserName.text.toString().trim()
            val password = binding.edPassword.text.toString().trim()

            if(email.isNotEmpty() && password.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                    if(it.isSuccessful){
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /*private fun createAnAccount(){
        binding.signupTxt.setOnClickListener{
            val intent = Intent(this, RegistrationNewPersonalTrainerActivity::class.java)
            startActivity(intent)
        }
    }*/
}