package com.example.loginui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loginui.databinding.DraftActivityBinding

class DraftActivity : AppCompatActivity() {
    private lateinit var binding: DraftActivityBinding
    private lateinit var db: WorkoutDatabaseHelper
    private lateinit var draftBoxAdapter: DraftBoxAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DraftActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = WorkoutDatabaseHelper(this)

        draftBoxAdapter = DraftBoxAdapter(db.getDraftWorkout(), this)

        binding.draftRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.draftRecyclerView.adapter = draftBoxAdapter
    }
}