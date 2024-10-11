package com.example.loginui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loginui.databinding.ActivityNewWorkoutBinding
import com.example.loginui.manager.AuthManager
import com.example.loginui.models.RPRModel
import com.example.loginui.models.TrainingDetailsModel
import com.example.loginui.models.TrainingModel

class NewWorkoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewWorkoutBinding
    private lateinit var db: WorkoutDatabaseHelper
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = WorkoutDatabaseHelper(this)
        authManager = AuthManager()

        addNewBoxInContainer()
        rprSection()
        saveWorkout()
    }


    //function

    private fun calculateAndDisplayAverage(mood: EditText, energy: EditText, doms: EditText, sleep: EditText, resultTextView: TextView) {
        val value1 = mood.text.toString().toDoubleOrNull() ?: 0.0
        val value2 = energy.text.toString().toDoubleOrNull() ?: 0.0
        val value3 = doms.text.toString().toDoubleOrNull() ?: 0.0
        val value4 = sleep.text.toString().toDoubleOrNull() ?: 0.0
        val average = (value1 + value2 + value3 + value4) / 4
        resultTextView.text = "$average"
    }

    private fun addNewBoxInContainer(){
        binding.buttonAddExercise.setOnClickListener {
            val newBox = LayoutInflater.from(this).inflate(R.layout.exercise_box, binding.container, false)
            val editExercise = newBox.findViewById<AutoCompleteTextView>(R.id.editExerciseName)

            val exercises = db.getExercisesName("")
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, exercises)
            editExercise.setAdapter(adapter)

            binding.container.addView(newBox)
        }
    }

    private fun rprSection(){
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateAndDisplayAverage(binding.edMoodValue, binding.edEnergyValue, binding.edDomsValue, binding.edSleepValue, binding.indexValue)
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        binding.edMoodValue.addTextChangedListener(textWatcher)
        binding.edEnergyValue.addTextChangedListener(textWatcher)
        binding.edDomsValue.addTextChangedListener(textWatcher)
        binding.edSleepValue.addTextChangedListener(textWatcher)
    }

    //TODO data and duration
    private fun saveWorkout(){
        binding.saveButton.setOnClickListener{
            val clientName = binding.editName.text.toString()
            val clientSurname = binding.editSurname.text.toString()
            val userId = db.getUserIdByNameAndSurname(clientName, clientSurname).toString()
            val containerBox = binding.container
            val allValues = mutableListOf<TrainingDetailsModel>()
            val ptId = authManager.getCurrentUserUid()

            if(userId!= "null"){
                //TODO adjust with autoincremental id
                val trainingId = db.countTrainingRows()+1
                val workoutNumber = db.getWorkoutNumberByClientId(userId)
                val trainingModel = TrainingModel(trainingId, "data", "duration", userId, ptId.toString(), workoutNumber)
                db.insertTraining(trainingModel)
                
                val mood = binding.edMoodValue.text.toString()
                val energy = binding.edEnergyValue.text.toString()
                val doms = binding.edDomsValue.text.toString()
                val sleep = binding.edSleepValue.text.toString()
                val index = binding.indexValue.text.toString()
                val borg = binding.borgValue.text.toString()
                val rprModel = RPRModel(0, userId, mood, sleep, energy, doms, index, borg, trainingId)
                db.insertRPR(rprModel)


                for (i in 0 until containerBox.childCount) {
                    val box = containerBox.getChildAt(i)
                    val exerciseName = box.findViewById<AutoCompleteTextView>(R.id.editExerciseName)
                    val exerciseId = db.getExerciseIdFromName(exerciseName.text.toString())
                    val reps = box.findViewById<EditText>(R.id.editReps)
                    val sets = box.findViewById<EditText>(R.id.editSerie)
                    val weight = box.findViewById<EditText>(R.id.editWeight)
                    val note = box.findViewById<EditText>(R.id.editNote)
                    val executionTime = box.findViewById<EditText>(R.id.editExecutionTime)
                    val seekBar: SeekBar = box.findViewById(R.id.exerciseBorgValue)
                    val borg: Int = seekBar.progress + 6

                    //TODO check if this control-if is necessary for the correct functionality
                    if (exerciseId != null && reps != null && sets != null && weight != null && executionTime != null) {
                        allValues.add(
                            TrainingDetailsModel(
                                0,
                                reps.text.toString(),
                                sets.text.toString(),
                                weight.text.toString(),
                                trainingId,
                                exerciseId,
                                note.text.toString(),
                                executionTime.text.toString(),
                                borg
                            )
                        )
                    }else{
                        Toast.makeText(this, "problem null", Toast.LENGTH_SHORT).show()
                    }
                }
                for (item in allValues) {
                    db.insertTrainingDetails(item)
                }
                finish()
                Toast.makeText(this, "workout saved", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "user not found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}