package com.example.loginui.models

data class ExerciseModel(
    val id:Int,
    val name:String,
    val category:String,
    val physicalGoal:String,
    val type:String
)

//TODO category, type, physical goal potrebbero essere dei tipi enumerativi (?)