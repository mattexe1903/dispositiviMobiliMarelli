package com.example.loginui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ToggleButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginui.models.TrainingModel
import com.example.loginui.models.RPRModel

class WorkoutSummaryAdapter(private var trainings: List<TrainingModel>,context: Context) :
    RecyclerView.Adapter<WorkoutSummaryAdapter.TrainingViewHolder>() {

    class TrainingViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val clientName: TextView = itemView.findViewById(R.id.clientName)
        val toggleButton: ToggleButton = itemView.findViewById(R.id.toggleButton)
        val cardView: CardView = itemView.findViewById(R.id.cardRprSummary)
        val workoutNumber: TextView = itemView.findViewById(R.id.workoutNumber)
        val daySinceLastWorkout: TextView = itemView.findViewById(R.id.daySinceLastWorkout)
        val energy: TextView = itemView.findViewById(R.id.energy)
        val sleep: TextView = itemView.findViewById(R.id.sleep)
        val doms: TextView = itemView.findViewById(R.id.doms)
        val mood: TextView = itemView.findViewById(R.id.mood)
        val borg: TextView = itemView.findViewById(R.id.borg)
        val trainingLoad: TextView = itemView.findViewById(R.id.trainingLoad)
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
        holder.clientName.text = client
        rpr?.mood?.let { holder.mood.text = "mood: $it" }
        rpr?.sleep?.let { holder.sleep.text = "sleep: $it" }
        rpr?.energy?.let { holder.energy.text = "energy: $it" }
        rpr?.doms?.let { holder.doms.text = "doms: $it" }

        holder.toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                holder.cardView.visibility = View.VISIBLE
            } else {
                holder.cardView.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = trainings.size

    fun refreshData(newTrainings: List<TrainingModel>){
        trainings = newTrainings
        notifyDataSetChanged()
    }
}