package com.example.loginui.models

data class TrainingModel(
    val id: Int,
    val date: String,
    val duration: String,
    val clientId: String,
    val personalTrainerId: String,
    val workoutNumber: Int
)
