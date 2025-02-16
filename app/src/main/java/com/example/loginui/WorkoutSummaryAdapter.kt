package com.example.loginui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginui.models.TrainingModel
import com.example.loginui.models.RPRModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WorkoutSummaryAdapter(private var trainings: List<TrainingModel>,context: Context) :
    RecyclerView.Adapter<WorkoutSummaryAdapter.TrainingViewHolder>() {

    class TrainingViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val clientName: TextView = itemView.findViewById(R.id.clientName)
        val toggleButton: ToggleButton = itemView.findViewById(R.id.toggleButton)
        val cardView: CardView = itemView.findViewById(R.id.cardRprSummary)
        val workoutNumber: TextView = itemView.findViewById(R.id.workoutNumber)
        val daySinceLastWorkout: TextView = itemView.findViewById(R.id.daySinceLastWorkout)
        val index: TextView = itemView.findViewById(R.id.indexValue)
        val energy: TextView = itemView.findViewById(R.id.energy)
        val sleep: TextView = itemView.findViewById(R.id.sleep)
        val doms: TextView = itemView.findViewById(R.id.doms)
        val mood: TextView = itemView.findViewById(R.id.mood)
        val borg: TextView = itemView.findViewById(R.id.borg)
        val trainingLoad: TextView = itemView.findViewById(R.id.trainingLoad)
        val info: ImageView = itemView.findViewById(R.id.infoWorkoutPerformed)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.workout_summary, parent, false)
        return TrainingViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val training = trainings[position]
        val db = WorkoutDatabaseHelper(holder.itemView.context)
        val client = db.getClientById(training.clientId)
        val rpr = db.getRprByTrainingId(training.id.toString())
        val borg = rpr.borg.toIntOrNull() ?: 0
        val activeTime = db.getActiveTimeByTrainingId(training.id) ?: 0
        val activeTimeInMinutes = activeTime / 60
        val trainingLoad = borg * activeTimeInMinutes
        val trainingId = training.id

        holder.clientName.text = client
        training.workoutNumber.let { holder.workoutNumber.text = "$it ° w" }

        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        if (position > 0) {
            val previousTraining = trainings[position - 1]
            val currentDate: Date = formatter.parse(training.date)
            val previousDate: Date = formatter.parse(previousTraining.date)
            val diff: Long = currentDate.time - previousDate.time
            val daysSinceLastWorkout = diff / (1000 * 60 * 60 * 24)
            holder.daySinceLastWorkout.text = "$daysSinceLastWorkout days"
        } else {
            holder.daySinceLastWorkout.text = ""
        }

        rpr.index.let { holder.index.text = "index: $it"}
        rpr.mood.let { holder.mood.text = "mood: $it" }
        rpr.sleep.let { holder.sleep.text = "sleep: $it" }
        rpr.energy.let { holder.energy.text = "energy: $it" }
        rpr.doms.let { holder.doms.text = "doms: $it" }
        borg.let{holder.borg.text = "borg: $it"}
        trainingLoad.let {holder.trainingLoad.text = "training load: $it"}

        holder.toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                holder.cardView.visibility = View.VISIBLE
            } else {
                holder.cardView.visibility = View.GONE
            }
        }

        holder.info.setOnClickListener{
            val context = holder.itemView.context
            val intent = Intent(context, WorkoutPerformedActivity::class.java)
            intent.putExtra("TRAINING_ID", trainingId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = trainings.size

    fun refreshData(newTrainings: List<TrainingModel>){
        trainings = newTrainings
        notifyDataSetChanged()
    }
}