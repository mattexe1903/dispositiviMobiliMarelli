package com.example.loginui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loginui.databinding.ManageExerciseActivityBinding
import com.example.loginui.models.ExerciseModel
import com.google.firebase.auth.FirebaseAuth

class ManageExerciseListActivity : AppCompatActivity() {

    private lateinit var binding: ManageExerciseActivityBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: WorkoutDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ManageExerciseActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = WorkoutDatabaseHelper(this)
        firebaseAuth = FirebaseAuth.getInstance()

        registerNewExercise()
    }

    //function
    private fun registerNewExercise(){
        binding.registerNewExercise.setOnClickListener{
            val exerciseName = binding.editNewExerciseName.text.toString().trim()
            val exerciseCategory = binding.editExerciseCategory.text.toString().trim()
            val exercisePhysicalGoal = binding.editExercisePhysicalGoal.text.toString().trim()
            val exerciseType = binding.editExerciseType.text.toString().trim()

            if(exerciseName.isNotBlank() && exerciseCategory.isNotBlank() && exerciseType.isNotBlank()){
                val exerciseModel = ExerciseModel(0, exerciseName, exerciseCategory, exercisePhysicalGoal, exerciseType)
                db.insertExercise(exerciseModel)
                Toast.makeText(this, "New exercise registered!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

}