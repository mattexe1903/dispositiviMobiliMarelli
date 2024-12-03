package com.example.loginui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginui.models.TrainingModel

class DraftBoxAdapter (private var trainings: List<TrainingModel>, context: Context ) :
    RecyclerView.Adapter<DraftBoxAdapter.DraftViewHolder>() {

    class DraftViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val clientName: TextView = itemView.findViewById(R.id.selectedDraftClientName)
        val draftId: TextView = itemView.findViewById(R.id.draftId)
        val ptName: TextView = itemView.findViewById(R.id.ptName)
        val cardView: CardView = itemView.findViewById(R.id.draftCardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DraftViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.draft_box, parent, false)
        return DraftViewHolder(view)
    }

    override fun onBindViewHolder(holder: DraftViewHolder, position: Int) {
        val training = trainings[position]
        val db = WorkoutDatabaseHelper(holder.itemView.context)
        val client = db.getClientById(training.clientId)
        val trainingId = training.id
        //TODO pt name

        holder.clientName.text = client
        holder.draftId.text = trainingId.toString()

        holder.cardView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, NewWorkoutActivity::class.java)
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