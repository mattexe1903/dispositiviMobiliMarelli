package com.example.loginui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loginui.databinding.WorkoutPerformedActivityBinding
import com.example.loginui.manager.NumberPickerManager

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
            binding.textViewCountdownWP.text = training.duration

            binding.edDomsValueWP.setText(rpr.doms)
            binding.edSleepValueWP.setText(rpr.sleep)
            binding.edEnergyValueWP.setText(rpr.energy)
            binding.edMoodValueWP.setText(rpr.mood)
            binding.indexValueWP.text = rpr.index

            val exercises = db.getTrainingDetailsByTrainingId(trainingId.toString())
            exercises.forEach { exercise ->
                val newBox = LayoutInflater.from(this).inflate(R.layout.exercise_box, binding.containerWP, false)
                val editExercise = newBox.findViewById<AutoCompleteTextView>(R.id.editExerciseName)

                val numberPickerManager = NumberPickerManager()
                val reps = newBox.findViewById<NumberPicker>(R.id.repsPicker)
                numberPickerManager.configureRepsPicker(reps)
                val sets = newBox.findViewById<NumberPicker>(R.id.seriesPicker)
                numberPickerManager.configureSetsPicker(sets)
                val weight = newBox.findViewById<NumberPicker>(R.id.weightPicker)
                numberPickerManager.configureWeightPicker(weight)
                val note = newBox.findViewById<EditText>(R.id.editNote)
                val seekBar = newBox.findViewById<SeekBar>(R.id.exerciseBorgValue)
                val timerText = newBox.findViewById<TextView>(R.id.timerTextView)

                val repsValue = exercise.reps.toIntOrNull() ?: 0
                val setsValue = exercise.sets.toIntOrNull() ?: 0
                val weightValue = exercise.weight.toIntOrNull() ?: 0

                editExercise.setText(exercise.exerciseId?.let { db.getExerciseNameFromId(it) })
                reps.value = repsValue
                sets.value = setsValue
                weight.value = weightValue
                note.setText(exercise.note)
                seekBar.progress = exercise.borg - 6
                timerText.text = formatTime(exercise.executionTime.toLong()*1000)

                reps.isEnabled = false
                sets.isEnabled = false
                weight.isEnabled = false

                binding.containerWP.addView(newBox, 0)
            }
        } else {
            Toast.makeText(this, "Error: draft not found.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatTime(millis: Long): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}