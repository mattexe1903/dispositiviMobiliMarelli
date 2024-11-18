package com.example.loginui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loginui.databinding.ActivityHomeBinding
import com.example.loginui.databinding.RegistrationNewClientBinding
import com.example.loginui.manager.AuthManager
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var db: WorkoutDatabaseHelper
    private lateinit var authManager: AuthManager
    private lateinit var workoutSummaryAdapter: WorkoutSummaryAdapter
    private var selectedUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = WorkoutDatabaseHelper(this)
        authManager = AuthManager()

        workoutSummaryAdapter = WorkoutSummaryAdapter(emptyList(), this)
        binding.trainingsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.trainingsRecyclerView.adapter = workoutSummaryAdapter

        searchUser()
        setupUI()
        setupListeners()

        val imgSetting: ImageView = findViewById(R.id.imgSetting)

        imgSetting.setOnClickListener {
            val popupMenu = PopupMenu(this, imgSetting)
            val inflater: MenuInflater = menuInflater
            inflater.inflate(R.menu.setting_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.action_profile -> {
                        true
                    }
                    R.id.registration_new_clients -> {
                        val intent = Intent(this, RegistrationNewClient::class.java)
                        startActivity(intent)
                        true
                    }
                    R.id.action_logout -> {
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }


    //function
    private fun setupUI(){
        val uid = authManager.getCurrentUserUid()
        val name = db.getOpNameByUid(uid.toString())
        binding.hiText.text = "Hi $name !"
    }

    private fun setupListeners(){
        binding.imgNewWrk.setOnClickListener{
            val intent = Intent(this, NewWorkoutActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun searchUser() {
        val editExercise = this.findViewById<AutoCompleteTextView>(R.id.searchUserName)
        val users = db.getUsersName("")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, users)
        editExercise.setAdapter(adapter)

        editExercise.setOnItemClickListener { parent, view, position, id ->
            val selectedUserName = parent.getItemAtPosition(position) as String
            val parts = selectedUserName.split(" ")
            val name = if (parts.isNotEmpty()) parts[0] else ""
            val surname = if (parts.size > 1) parts[1] else ""
            val clientId = db.getUserIdByNameAndSurname(name, surname)
            selectedUserId = clientId.toString()

            workoutSummaryAdapter.refreshData(db.getTrainingByClientId(selectedUserId.toString()))
        }

        editExercise.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val clientName = s.toString()
                if (clientName.isEmpty()) {
                    workoutSummaryAdapter.refreshData(emptyList())
                } else {
                    val parts = clientName.split(" ")
                    val name = if (parts.isNotEmpty()) parts[0] else ""
                    val surname = if (parts.size > 1) parts[1] else ""
                    val clientId = db.getUserIdByNameAndSurname(name, surname)
                    selectedUserId = clientId.toString()
                    workoutSummaryAdapter.refreshData(db.getTrainingByClientId(selectedUserId.toString()))
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }




}