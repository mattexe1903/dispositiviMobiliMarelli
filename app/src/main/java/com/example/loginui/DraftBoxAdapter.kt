package com.example.loginui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ToggleButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginui.WorkoutSummaryAdapter.TrainingViewHolder
import com.example.loginui.models.TrainingModel
import com.example.loginui.models.RPRModel
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DraftBoxAdapter (private var trainings: List<TrainingModel>, context: Context ) :
    RecyclerView.Adapter<DraftBoxAdapter.DraftViewHolder>() {

    class DraftViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val clientName: TextView = itemView.findViewById(R.id.selectedDraftClientName)
        val draftId: TextView = itemView.findViewById(R.id.draftId)
        val ptName: TextView = itemView.findViewById(R.id.ptName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DraftViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.draft_box, parent, false)
        return DraftViewHolder(view)
    }

    override fun onBindViewHolder(holder: DraftViewHolder, position: Int) {
        val training = trainings[position]
        val db = WorkoutDatabaseHelper(holder.itemView.context)
        val client = db.getClientById(training.clientId)

        holder.clientName.text = client
    }

    override fun getItemCount(): Int = trainings.size

    fun refreshData(newTrainings: List<TrainingModel>){
        trainings = newTrainings
        notifyDataSetChanged()
    }
}