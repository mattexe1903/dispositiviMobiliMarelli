package com.example.loginui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginui.models.TrainingModel

class TrainingAdapter(private var trainings: List<TrainingModel>, context: Context) :
    RecyclerView.Adapter<TrainingAdapter.TrainingViewHolder>() {

    class TrainingViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val clientName : TextView = itemView.findViewById(R.id.clientName)
        val duration : TextView = itemView.findViewById(R.id.duration)
        val category : TextView = itemView.findViewById(R.id.category)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrainingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.workout_box, parent, false)
        return TrainingViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val training = trainings[position]
        val db = WorkoutDatabaseHelper(holder.itemView.context)
        val client = db.getClientById(training.clientId)
        holder.clientName.text = client
        holder.duration.text = training.duration

        //TODO from the training id search the category of the training
        //holder.category.text
    }

    override fun getItemCount(): Int = trainings.size

    fun refreshData(newTrainings: List<TrainingModel>){
        trainings = newTrainings
        notifyDataSetChanged()
    }
}