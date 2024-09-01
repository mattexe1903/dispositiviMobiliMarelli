package com.example.loginui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.loginui.databinding.ActivityRegistrationBinding
import com.example.loginui.models.PersonalTrainerModel
import com.google.firebase.auth.FirebaseAuth

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: WorkoutDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = WorkoutDatabaseHelper(this)

        //firebaseAuth = FirebaseAuth.getInstance()

        registerAccount()

    }

    private fun registerAccount(){
        binding.signupBt.setOnClickListener{
            val email = binding.edEmail.text.toString()
            val password = binding.edPasswordSU.text.toString()
            if(email.isNotEmpty() && password.isNotEmpty()){
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                    if(it.isSuccessful){
                        val user = firebaseAuth.currentUser
                        val uid = user?.uid
                        Toast.makeText(this, "User ID: $uid", Toast.LENGTH_SHORT).show()
                        saveOperatorInfo(uid.toString())
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

    private fun saveOperatorInfo(uid:String){
        val name = binding.edName.text.toString()
        val surname = binding.edSurname.text.toString()
        val pt = PersonalTrainerModel(uid, name, surname)
        db.insertOperator(pt)
    }
}