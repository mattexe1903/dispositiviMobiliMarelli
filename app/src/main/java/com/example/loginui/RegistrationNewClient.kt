package com.example.loginui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.loginui.databinding.ActivityRegistrationBinding
import com.example.loginui.databinding.RegistrationNewClientBinding
import com.google.firebase.auth.FirebaseAuth

class RegistrationNewClient : AppCompatActivity()  {

    private lateinit var binding: RegistrationNewClientBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: WorkoutDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = RegistrationNewClientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = WorkoutDatabaseHelper(this)
        firebaseAuth = FirebaseAuth.getInstance()

    }

    //function
    private fun registerNewClient(){

    }
}