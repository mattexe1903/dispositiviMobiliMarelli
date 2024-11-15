package com.example.loginui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.loginui.databinding.NewWorkoutActivityBinding
import com.example.loginui.manager.AuthManager
import com.example.loginui.manager.NumberPickerManager
import com.example.loginui.models.RPRModel
import com.example.loginui.models.TrainingDetailsModel
import com.example.loginui.models.TrainingModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NewWorkoutActivity : AppCompatActivity() {

    private lateinit var binding: NewWorkoutActivityBinding
    private lateinit var db: WorkoutDatabaseHelper
    private lateinit var authManager: AuthManager
    private val handler = Handler(Looper.getMainLooper())
    private var workoutDuration: Long=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NewWorkoutActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = WorkoutDatabaseHelper(this)
        authManager = AuthManager()

        setDefaultDate()

        countdownSection()
        addNewBoxInContainer()
        rprSection()
        saveWorkout()
    }


    //FUNCTION

    private fun setDefaultDate(){
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        val formattedDate = String.format("%02d/%02d/%d", day, month, year)
        binding.editDate.setText(formattedDate)
    }

    private fun calculateAndDisplayAverage(mood: EditText, energy: EditText, doms: EditText, sleep: EditText, resultTextView: TextView) {
        val moodValue = mood.text.toString().toDoubleOrNull() ?: 0.0
        val energyValue = energy.text.toString().toDoubleOrNull() ?: 0.0
        val domsValue = doms.text.toString().toDoubleOrNull() ?: 0.0
        val sleepValue = sleep.text.toString().toDoubleOrNull() ?: 0.0
        val average = ((moodValue + energyValue + (11-domsValue) + sleepValue) / 4)*2
        resultTextView.text = "$average"
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun addNewBoxInContainer() {
        binding.buttonAddExercise.setOnClickListener {
            val newBox = LayoutInflater.from(this).inflate(R.layout.exercise_box, binding.container, false)

            val editExercise = newBox.findViewById<AutoCompleteTextView>(R.id.editExerciseName)
            val exercises = db.getExercisesName("")
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, exercises)
            editExercise.setAdapter(adapter)

            val switchTimeOption = newBox.findViewById<Switch>(R.id.switchTimeOption)
            val timerOption = newBox.findViewById<ConstraintLayout>(R.id.timerOption)
            val manualOption = newBox.findViewById<ConstraintLayout>(R.id.manualOption)

            switchTimeOption.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    timerOption.visibility = View.GONE
                    manualOption.visibility = View.VISIBLE
                } else {
                    timerOption.visibility = View.VISIBLE
                    manualOption.visibility = View.GONE
                }
            }

            val timerTextView = newBox.findViewById<TextView>(R.id.timerTextView)
            val startButton = newBox.findViewById<Button>(R.id.start)
            val stopButton = newBox.findViewById<Button>(R.id.stop)
            val resetButton = newBox.findViewById<Button>(R.id.reset)

            stopButton.visibility = View.GONE
            resetButton.visibility = View.GONE

            val boxTimer = Timer(handler) { elapsedTime ->
                timerTextView.text = formatDuration(elapsedTime)
            }

            startButton.setOnClickListener {
                boxTimer.start()
                stopButton.visibility = View.VISIBLE
                resetButton.visibility = View.GONE
                startButton.visibility = View.GONE
            }

            stopButton.setOnClickListener {
                boxTimer.stop()
                startButton.visibility = View.VISIBLE
                stopButton.visibility = View.GONE
                resetButton.visibility = View.VISIBLE
            }

            resetButton.setOnClickListener {
                boxTimer.reset()
                stopButton.visibility = View.GONE
                resetButton.visibility = View.GONE
                startButton.visibility = View.VISIBLE
            }

            val numberPickerManager = NumberPickerManager()
            val reps = newBox.findViewById<NumberPicker>(R.id.repsPicker)
            numberPickerManager.configureRepsPicker(reps)
            val sets = newBox.findViewById<NumberPicker>(R.id.seriesPicker)
            numberPickerManager.configureSetsPicker(sets)
            val weight = newBox.findViewById<NumberPicker>(R.id.weightPicker)
            numberPickerManager.configureWeightPicker(weight)

            binding.container.addView(newBox, 0)
        }
    }

    private fun formatDuration(millis: Long): String {
        val minutes = (millis / (1000 * 60)) % 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun parseDuration(duration: String): Long {
        val parts = duration.split(":")
        val minutes = parts[0].toLong()
        val seconds = parts[1].toLong()
        return (minutes * 60 + seconds)
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

                var overallBorg = 0
                var exerciseCount =0

                if(userId != "null"){
                    //TODO adjust with autoincremental id
                    val trainingId = db.countTrainingRows()+1
                    val workoutNumber = db.getWorkoutNumberByClientId(userId)
                    val trainingModel = TrainingModel(trainingId, formattedDate, formatTime(workoutDuration), userId, ptId.toString(), workoutNumber)
                    db.insertTraining(trainingModel)

                    val mood = binding.edMoodValue.text.toString()
                    val energy = binding.edEnergyValue.text.toString()
                    val doms = binding.edDomsValue.text.toString()
                    val sleep = binding.edSleepValue.text.toString()
                    val index = binding.indexValue.text.toString()

                    for (i in 0 until containerBox.childCount) {
                        val box = containerBox.getChildAt(i)

                        val exerciseName = box.findViewById<AutoCompleteTextView>(R.id.editExerciseName)
                        val exerciseId = db.getExerciseIdFromName(exerciseName.text.toString())
                        val reps = box.findViewById<NumberPicker>(R.id.repsPicker)
                        val sets = box.findViewById<NumberPicker>(R.id.seriesPicker)
                        val weight = box.findViewById<NumberPicker>(R.id.weightPicker)
                        val note = box.findViewById<EditText>(R.id.editNote)
                        val seekBar: SeekBar = box.findViewById(R.id.exerciseBorgValue)
                        val borg: Int = seekBar.progress + 6
                        overallBorg += borg
                        exerciseCount++

                        val switchedTimeOption = box.findViewById<Switch>(R.id.switchTimeOption)
                        val executionTime: String = if(switchedTimeOption.isChecked){
                            box.findViewById<EditText>(R.id.editManualDuration).text.toString()
                        }else{
                            box.findViewById<TextView>(R.id.timerTextView).text.toString()
                        }
                        val durationInSeconds = parseDuration(executionTime)

                        //TODO check if this control-if is necessary for the correct functionality
                        if (exerciseId != null && reps != null && sets != null && weight != null && executionTime != null) {
                            allValues.add(
                                TrainingDetailsModel(
                                    0,
                                    reps.value.toString(),
                                    sets.value.toString(),
                                    weight.value.toString(),
                                    trainingId,
                                    exerciseId,
                                    note.text.toString(),
                                    durationInSeconds.toString(),
                                    borg
                                )
                            )
                        }else{
                            Toast.makeText(this, "problem null", Toast.LENGTH_SHORT).show()
                        }
                    }

                    val avarageBorg = if(exerciseCount>0) overallBorg/exerciseCount else 0
                    val rprModel = RPRModel(0, userId, mood, sleep, energy, doms, index, avarageBorg.toString(), trainingId)
                    db.insertRPR(rprModel)

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

    private fun countdownSection(){
        val textViewCountdown = binding.textViewCountdown
        val spinnerTimeSelection = binding.spinnerTimeSelection
        val buttonStart = binding.buttonStart
        var selectedTimeInMillis: Long = 0
        var countdownTimer: CountDownTimer? = null
        var isCountdownActive = false

        // Spinner configuration with the countdown_times
        ArrayAdapter.createFromResource(
            this,
            R.array.countdown_times,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerTimeSelection.adapter = adapter
        }

        // Set the listener for the spinner selection change
        spinnerTimeSelection.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Set the time in milliseconds based on selection
                selectedTimeInMillis = when (position) {
                    0 -> 60 * 60 * 1000L
                    1 -> 45 * 60 * 1000L
                    2 -> 30 * 60 * 1000L
                    else -> 0
                }
                textViewCountdown.text = formatTime(selectedTimeInMillis)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No action necessary
            }
        }

        // start the countdown when start button is pressed
        buttonStart.setOnClickListener {
            if(isCountdownActive) {
                countdownTimer?.cancel()
            }
            workoutDuration = selectedTimeInMillis

            countdownTimer = object : CountDownTimer(selectedTimeInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    textViewCountdown.text = formatTime(millisUntilFinished)
                }

                override fun onFinish() {
                    textViewCountdown.text = "Time expired!"
                    isCountdownActive= false
                }
            }.start()

            isCountdownActive = true
        }
    }

    private fun formatTime(millis: Long): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

}