package com.example.loginui.manager

import com.google.firebase.auth.FirebaseAuth

class AuthManager {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUserUid(): String?{
        return mAuth.currentUser?.uid
    }
}