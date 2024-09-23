package com.example.loginui

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.loginui.models.RPRModel
import com.example.loginui.models.ClientModel
import com.example.loginui.models.PersonalTrainerModel
import com.example.loginui.models.TrainingDetailsModel
import com.example.loginui.models.TrainingModel

class WorkoutDatabaseHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "ptl.db"
        private const val DATABASE_VERSION = 1

        //table
        private const val TABLE_RPR = "rpr"
        private const val TABLE_CLIENT = "client"
        private const val TABLE_OPERATOR = "operator"
        private const val TABLE_EXERCISE ="exercise"
        private const val TABLE_TRAINING = "training"
        private const val TABLE_TRAININGDETAILS ="trainingDetails"

        //Column RPR table
        private const val COLUMN_RPR_ID = "id"
        private const val COLUMN_RPR_MOOD = "mood"
        private const val COLUMN_RPR_SLEEP = "sleep"
        private const val COLUMN_RPR_ENERGY = "energy"
        private const val COLUMN_RPR_DOMS = "doms"
        private const val COLUMN_RPR_AVG = "avg"
        private const val COLUMN_RPR_BORG = "borg"
        private const val COLUMN_RPR_CLIENT = "client"
        private const val COLUMN_RPR_TRAINING = "training"

        //Column client table
        private const val COLUMN_CLIENT_ID = "id"
        private const val COLUMN_CLIENT_NAME = "name"
        private const val COLUMN_CLIENT_SURNAME = "surname"

        //Column operator table
        private const val COLUMN_OP_UID = "uid"
        private const val COLUMN_OP_NAME = "name"

        //column exercise table
        private const val COLUMN_EX_ID= "id"
        const val COLUMN_EX_NAME = "name"
        private const val COLUMN_EX_CATEGORY = "category"
        private const val COLUMN_EX_PHYSICAL_GOAL= "physicalGoal"
        private const val COLUMN_EX_TYPE= "type"

        //column training table
        private const val COLUMN_TRAINING_ID = "id"
        private const val COLUMN_TRAINING_DATE = "date"
        private const val COLUMN_TRAINING_DURATION ="duration"
        private const val COLUMN_TRAINING_CLIENT = "client"
        private const val COLUMN_TRAINING_PT= "operator"

        //column training details table
        private const val COLUMN_TD_ID="id"
        private const val COLUMN_TD_REPS = "reps"
        private const val COLUMN_TD_SETS ="sets"
        private const val COLUMN_TD_WEIGHT= "weight"
        private const val COLUMN_TD_TRAINING="training"
        private const val COLUMN_TD_EXERCISE= "exercise"
        private const val COLUMN_TD_NOTE="note"
        private const val COLUMN_TD_EXTIME="executionTime"
        private const val COLUMN_TD_BORG="borg"

        //creation table
        private const val CREATETABLERPR = "CREATE TABLE $TABLE_RPR(" +
                "$COLUMN_RPR_ID INTEGER PRIMARY KEY, " +
                "$COLUMN_RPR_DOMS TEXT," +
                "$COLUMN_RPR_MOOD TEXT," +
                "$COLUMN_RPR_ENERGY TEXT," +
                "$COLUMN_RPR_SLEEP TEXT," +
                "$COLUMN_RPR_BORG TEXT," +
                "$COLUMN_RPR_AVG TEXT," +
                "$COLUMN_RPR_CLIENT TEXT, "+
                "$COLUMN_RPR_TRAINING TEXT)"

        private const val CREATETABLECLIENT = "CREATE TABLE $TABLE_CLIENT(" +
                "$COLUMN_CLIENT_ID INTEGER PRIMARY KEY," +
                "$COLUMN_CLIENT_NAME TEXT, " +
                "$COLUMN_CLIENT_SURNAME TEXT)"

        private const val CREATETABLEOPERATOR = "CREATE TABLE $TABLE_OPERATOR(" +
                "$COLUMN_OP_UID TEXT PRIMARY KEY,"+
                "$COLUMN_OP_NAME TEXT)"

        private const val CREATETABLEEXERCISE = "CREATE TABLE $TABLE_EXERCISE("+
                "$COLUMN_EX_ID INTEGER PRIMARY KEY, "+
                "$COLUMN_EX_NAME TEXT, "+
                "$COLUMN_EX_CATEGORY TEXT,"+
                "$COLUMN_EX_PHYSICAL_GOAL TEXT, "+
                "$COLUMN_EX_TYPE TEXT)"

        private const val CREATETABLETRAINING = "CREATE TABLE $TABLE_TRAINING("+
                "$COLUMN_TRAINING_ID INTEGER PRIMARY KEY, "+
                "$COLUMN_TRAINING_DATE TEXT,"+
                "$COLUMN_TRAINING_DURATION TEXT,"+
                "$COLUMN_TRAINING_CLIENT TEXT," +
                "$COLUMN_TRAINING_PT TEXT)"

        private const val CREATETABLETD = "CREATE TABLE $TABLE_TRAININGDETAILS("+
                "$COLUMN_TD_ID INTEGER PRIMARY KEY, "+
                "$COLUMN_TD_REPS TEXT, "+
                "$COLUMN_TD_SETS TEXT, "+
                "$COLUMN_TD_WEIGHT TEXT, "+
                "$COLUMN_TD_EXTIME TEXT, "+
                "$COLUMN_TD_EXERCISE TEXT, "+
                "$COLUMN_TD_TRAINING TEXT, "+
                "$COLUMN_TD_BORG TEXT, "+
                "$COLUMN_TD_NOTE TEXT)"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATETABLERPR)
        db?.execSQL(CREATETABLECLIENT)
        db?.execSQL(CREATETABLEOPERATOR)
        db?.execSQL(CREATETABLEEXERCISE)
        db?.execSQL(CREATETABLETRAINING)
        db?.execSQL(CREATETABLETD)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableClientQuery = "DROP TABLE IF EXISTS $TABLE_CLIENT"
        val dropTableRPRQuery = "DROP TABLE IF EXISTS $TABLE_RPR"
        val dropTableOpQuery = "DROP TABLE IF EXISTS $TABLE_OPERATOR"
        val dropTableExQuery = "DROP TABLE IF EXISTS $TABLE_EXERCISE"
        val dropTableTrainingQuery = "DROP TABLE IF EXISTS $TABLE_TRAINING"
        val dropTableTDQuery = "DROP TABLE IF EXISTS $TABLE_TRAININGDETAILS"
        db?.execSQL(dropTableClientQuery)
        db?.execSQL(dropTableRPRQuery)
        db?.execSQL(dropTableOpQuery)
        db?.execSQL(dropTableExQuery)
        db?.execSQL(dropTableTrainingQuery)
        db?.execSQL(dropTableTDQuery)
        onCreate(db)
    }

    fun insertRPR(rprModel: RPRModel){
        val db = writableDatabase
        val values = ContentValues().apply{
            put(COLUMN_RPR_DOMS, rprModel.doms)
            put(COLUMN_RPR_ENERGY, rprModel.energy)
            put(COLUMN_RPR_SLEEP, rprModel.sleep)
            put(COLUMN_RPR_MOOD, rprModel.mood)
            put(COLUMN_RPR_CLIENT, rprModel.clientID)
            put(COLUMN_RPR_BORG, rprModel.borg)
            put(COLUMN_RPR_AVG, rprModel.index)
            put(COLUMN_RPR_TRAINING, rprModel.training)
        }
        db.insert(TABLE_RPR, null, values)
        db.close()
    }

    fun insertClient(clientModel: ClientModel){
        val db = writableDatabase
        val values = ContentValues().apply{
            put(COLUMN_CLIENT_NAME, clientModel.name)
            put(COLUMN_CLIENT_SURNAME, clientModel.surname)
        }
        db.insert(TABLE_CLIENT, null, values)
        db.close()
    }

    fun insertTraining(training: TrainingModel){
        val db= writableDatabase
        val values = ContentValues().apply{
            put(COLUMN_TD_ID, training.id)
            put(COLUMN_TRAINING_DATE, training.date)
            put(COLUMN_TRAINING_DURATION, training.duration)
            put(COLUMN_TRAINING_CLIENT, training.clientId)
            put(COLUMN_TRAINING_PT, training.personalTrainerId)
        }
        db.insert(TABLE_TRAINING, null, values)
        db.close()
    }

    fun insertTrainingDetails(trainingDetails: TrainingDetailsModel){
        val db=writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TD_REPS, trainingDetails.reps)
            put(COLUMN_TD_SETS, trainingDetails.sets)
            put(COLUMN_TD_WEIGHT, trainingDetails.weight)
            put(COLUMN_TD_TRAINING, trainingDetails.trainingId)
            put(COLUMN_TD_EXERCISE, trainingDetails.exerciseId)
            put(COLUMN_TD_NOTE, trainingDetails.note)
            put(COLUMN_TD_BORG, trainingDetails.borg.toString())
            put(COLUMN_TD_EXTIME, trainingDetails.executionTime)
        }
        db.insert(TABLE_TRAININGDETAILS, null, values)
        db.close()
    }

    fun insertOperator(personalTrainerMode: PersonalTrainerModel){
        val db=writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_OP_NAME, personalTrainerMode.name)
            put(COLUMN_OP_UID, personalTrainerMode.uid)
        }
        db.insert(TABLE_OPERATOR, null, values)
        db.close()
    }

    fun getUserIdByNameAndSurname(name: String, surname: String): Int?{
        val db= readableDatabase
        val query = "SELECT id FROM client WHERE name=? AND surname=?"
        val cursor = db.rawQuery(query, arrayOf(name, surname))
        var userId: Int? = null
        if(cursor.moveToFirst()){
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        }
        cursor.close()
        return userId
    }

    fun getOpNameByUid(uid: String): String? {
        val db = readableDatabase
        val query = "SELECT name FROM operator WHERE uid=?"
        var name: String? = null
        val cursor = db.rawQuery(query, arrayOf(uid))
        try {
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            }
        } catch (e: Exception) {
            Log.e("DatabaseError", "Error while fetching name", e)
        } finally {
            cursor.close()
        }
        return name
    }

    fun getExerciseIdFromName(name:String):Int?{
        val db = readableDatabase
        val query = "SELECT id FROM exercise WHERE name=?"
        val cursor = db.rawQuery(query, arrayOf(name))
        var id: Int?= null
        if(cursor.moveToFirst()){
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        }
        cursor.close()
        return id
    }

    fun getExercisesName(query: String): List<String> {
        val db = this.readableDatabase
        val selection = "name LIKE ?"
        val selectionArgs = arrayOf("%$query%")
        val cursor = db.query(
            "exercise",
            arrayOf("name"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        val exercisesName = mutableListOf<String>()
        with(cursor) {
            while (moveToNext()) {
                val name = getString(getColumnIndexOrThrow("name"))
                exercisesName.add(name)
            }
        }
        cursor.close()
        return exercisesName
    }

    fun getUsersName(query: String): List<String> {
        val db = this.readableDatabase
        val selection = "name LIKE ? OR surname LIKE ? OR (name || ' ' || surname) LIKE ?"
        val selectionArgs = arrayOf("%$query%", "%$query%", "%$query%")
        val cursor = db.query(
            "client",
            arrayOf("name", "surname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        val usersName = mutableListOf<String>()
        with(cursor) {
            while (moveToNext()) {
                val fullName = getString(getColumnIndexOrThrow("name")) + " " + getString(getColumnIndexOrThrow("surname"))
                usersName.add(fullName)
            }
        }
        cursor.close()
        return usersName
    }

    fun countTrainingRows(): Int{
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_TRAINING"
        val cursor = db.rawQuery(query, null)
        var count=0
        if(cursor.moveToFirst()){
            count=cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    fun getAllData(): List<TrainingModel> {
        val trainingList = mutableListOf<TrainingModel>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_TRAINING"
        val cursor = db.rawQuery(query, null)
        while(cursor.moveToNext()){
            val trainingId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TRAINING_ID))
            val clientId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TRAINING_CLIENT))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRAINING_DATE))
            val duration = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRAINING_DURATION))
            val personalTrainerId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TRAINING_PT))
            val training = TrainingModel(trainingId, date, duration, clientId.toString(),personalTrainerId.toString())
            trainingList.add(training)
        }
        cursor.close()
        db.close()
        return trainingList
    }

    fun getLastFiveTraining(): List<TrainingModel> {
        val trainingList = mutableListOf<TrainingModel>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_TRAINING ORDER BY $COLUMN_TRAINING_ID DESC LIMIT 5"
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val trainingId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TRAINING_ID))
            val clientId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TRAINING_CLIENT))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRAINING_DATE))
            val duration = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRAINING_DURATION))
            val personalTrainerId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TRAINING_PT))
            val training = TrainingModel(trainingId, date, duration, clientId.toString(), personalTrainerId.toString())
            trainingList.add(training)
        }
        cursor.close()
        db.close()
        return trainingList
    }

    fun getClientById(id: String): String? {
        val db = readableDatabase
        val query = "SELECT name, surname FROM client WHERE id=?"
        val cursor = db.rawQuery(query, arrayOf(id))
        var client: String?= null
        try {
            if (cursor.moveToFirst()) {
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val surname = cursor.getString(cursor.getColumnIndexOrThrow("surname"))
                client = "$name $surname"
            }
        } catch (e: Exception) {
            Log.e("DatabaseError", "Error while fetching name", e)
        } finally {
            cursor.close()
        }
        return client
    }
}