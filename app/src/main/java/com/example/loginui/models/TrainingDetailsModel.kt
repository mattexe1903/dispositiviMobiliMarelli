package com.example.loginui.models

data class TrainingDetailsModel(
    val id: Int,
    val reps: String,
    val sets: String,
    val weight: String,
    val trainingId: Int,
    val exerciseId: Int?,
    val note: String,
    val executionTime: String,
    val borg: Int
)