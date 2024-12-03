package com.example.loginui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loginui.databinding.WorkoutPerformedActivityBinding

class WorkoutPerformedActivity: AppCompatActivity(){
    private lateinit var binding: WorkoutPerformedActivityBinding
    private lateinit var db: WorkoutDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WorkoutPerformedActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = WorkoutDatabaseHelper(this)

        val trainingId = intent.getIntExtra("TRAINING_ID", -1)
        loadDraftData(trainingId)
    }

    private fun loadDraftData(trainingId: Int) {
        val training = db.getTrainingById(trainingId)
        val rpr = db.getRprByTrainingId(trainingId.toString())
        val clientNameSurname = training?.clientId?.let { db.getClientById(it) }

        if (training != null) {
            binding.userName.text = clientNameSurname
            binding.editDateWP.setText(training.date)
            binding.textViewCountdownWP.text = parseDuration(training.duration).toString()

            binding.edDomsValueWP.setText(rpr.doms)
            binding.edSleepValueWP.setText(rpr.sleep)
            binding.edEnergyValueWP.setText(rpr.energy)
            binding.edMoodValueWP.setText(rpr.mood)
            binding.indexValueWP.text = rpr.index

            val exercises = db.getTrainingDetailsByTrainingId(trainingId.toString())
            exercises.forEach { exercise ->
                val newBox = LayoutInflater.from(this).inflate(R.layout.exercise_box, binding.containerWP, false)
                val editExercise = newBox.findViewById<AutoCompleteTextView>(R.id.editExerciseName)

                val exerciseList = db.getExercisesName("")
                val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, exerciseList)
                editExercise.setAdapter(adapter)

                val reps = newBox.findViewById<NumberPicker>(R.id.repsPicker)
                val sets = newBox.findViewById<NumberPicker>(R.id.seriesPicker)
                val weight = newBox.findViewById<NumberPicker>(R.id.weightPicker)
                val note = newBox.findViewById<EditText>(R.id.editNote)
                val seekBar = newBox.findViewById<SeekBar>(R.id.exerciseBorgValue)

                editExercise.setText(exercise.exerciseId?.let { db.getExerciseNameFromId(it) })
                reps.value = exercise.reps.toInt()
                sets.value = exercise.sets.toInt()
                weight.value = exercise.weight.toInt()
                note.setText(exercise.note)
                seekBar.progress = exercise.borg - 6

                binding.containerWP.addView(newBox, 0)
            }
        } else {
            Toast.makeText(this, "Error: draft not found.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun parseDuration(duration: String): Long {
        val parts = duration.split(":")
        val minutes = parts[0].toLong()
        val seconds = parts[1].toLong()
        return (minutes * 60 + seconds)
    }
}