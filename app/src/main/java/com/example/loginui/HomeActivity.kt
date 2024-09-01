package com.example.loginui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginui.databinding.ActivityHomeBinding
import com.example.loginui.manager.AuthManager
import com.example.loginui.models.TrainingModel

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var db: WorkoutDatabaseHelper
    private lateinit var authManager: AuthManager
    private lateinit var trainingsAdapter: TrainingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = WorkoutDatabaseHelper(this)
        authManager = AuthManager()
        trainingsAdapter = TrainingAdapter(db.getLastFiveTraining(), this)

        binding.trainingsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.trainingsRecyclerView.adapter = trainingsAdapter

        setupUI()
        setupListeners()
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
        trainingsAdapter.refreshData(db.getLastFiveTraining())
    }
}