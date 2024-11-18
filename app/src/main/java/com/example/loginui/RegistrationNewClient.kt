package com.example.loginui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.text.toLowerCase
import com.example.loginui.databinding.ActivityRegistrationBinding
import com.example.loginui.databinding.RegistrationNewClientBinding
import com.example.loginui.models.ClientModel
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

        registerNewClient()
    }

    //function
    private fun registerNewClient(){
        binding.registerNewClient.setOnClickListener{
            val name = binding.editClientName.text.toString().trim()
            val surname = binding.editClientSurname.text.toString().trim()

            if(name.isNotBlank() && surname.isNotBlank()){
                val clientModel = ClientModel("0", name, surname, 0, "s")
                db.insertClient(clientModel)
                Toast.makeText(this, "New client registered!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}