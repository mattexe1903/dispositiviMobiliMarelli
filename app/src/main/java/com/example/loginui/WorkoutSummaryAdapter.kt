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
        //TODO assign the appropriate attribute into the right area
        //val rprs = db.getRprByTrainingId(training.id.toString())
        //val rpr = rprs[position]
        holder.clientName.text = client
        holder.workoutNumber.text = ""
        //holder.energy.text = "energy: ${rpr.energy}"

        

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