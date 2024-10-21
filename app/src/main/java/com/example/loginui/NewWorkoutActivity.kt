package com.example.loginui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NewWorkoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewWorkoutBinding
    private lateinit var db: WorkoutDatabaseHelper
    private lateinit var authManager: AuthManager
    private val handler = Handler(Looper.getMainLooper())


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

    private fun addNewBoxInContainer() {
        binding.buttonAddExercise.setOnClickListener {
            val newBox = LayoutInflater.from(this).inflate(R.layout.new_exercise_box, binding.container, false)

            val editExercise = newBox.findViewById<AutoCompleteTextView>(R.id.editExerciseName)
            val exercises = db.getExercisesName("")
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, exercises)
            editExercise.setAdapter(adapter)

            val timerTextView = newBox.findViewById<TextView>(R.id.timerTextView)
            val startButton = newBox.findViewById<Button>(R.id.start)
            val stopButton = newBox.findViewById<Button>(R.id.stop)
            val intermediateButton = newBox.findViewById<Button>(R.id.intermedio)
            val resetButton = newBox.findViewById<Button>(R.id.reset)

            stopButton.visibility = View.GONE
            intermediateButton.visibility = View.GONE
            resetButton.visibility = View.GONE

            val boxTimer = Timer(handler) { elapsedTime ->
                timerTextView.text = formatDuration(elapsedTime)
            }

            startButton.setOnClickListener {
                boxTimer.start()
                stopButton.visibility = View.VISIBLE
                intermediateButton.visibility = View.VISIBLE
                resetButton.visibility = View.GONE
                startButton.visibility = View.GONE
            }

            stopButton.setOnClickListener {
                boxTimer.stop()
                startButton.visibility = View.VISIBLE
                stopButton.visibility = View.GONE
                resetButton.visibility = View.VISIBLE
                intermediateButton.visibility = View.GONE
            }

            intermediateButton.setOnClickListener {
                boxTimer.intermediate()
            }

            resetButton.setOnClickListener {
                boxTimer.reset()
                stopButton.visibility = View.GONE
                intermediateButton.visibility = View.GONE
                resetButton.visibility = View.GONE
                startButton.visibility = View.VISIBLE
            }

            binding.container.addView(newBox)
        }
    }


    private fun formatDuration(millis: Long): String {
        val hours = millis / (1000 * 60 * 60)
        val minutes = (millis / (1000 * 60)) % 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
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

    //TODO duration with timer
    private fun saveWorkout(){
        binding.saveButton.setOnClickListener{
            val clientName = binding.editName.text.toString()
            val clientSurname = binding.editSurname.text.toString()
            val editDate: EditText = findViewById(R.id.editDate)
            val dateText: String = editDate.text.toString()
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            try {
                val date: Date = formatter.parse(dateText)
                val formattedDate = formatter.format(date)
                val userId = db.getUserIdByNameAndSurname(clientName, clientSurname).toString()
                val containerBox = binding.container
                val allValues = mutableListOf<TrainingDetailsModel>()
                val ptId = authManager.getCurrentUserUid()

                if(userId != "null"){
                    //TODO adjust with autoincremental id
                    val trainingId = db.countTrainingRows()+1
                    val workoutNumber = db.getWorkoutNumberByClientId(userId)
                    val trainingModel = TrainingModel(trainingId, formattedDate, "duration", userId, ptId.toString(), workoutNumber)
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
            } catch (e: ParseException) {
                Toast.makeText(this, "date not valid", Toast.LENGTH_SHORT).show()
            }
        }
    }
}