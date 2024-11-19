package com.example.loginui

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.loginui.models.RPRModel
import com.example.loginui.models.ClientModel
import com.example.loginui.models.ExerciseModel
import com.example.loginui.models.PersonalTrainerModel
import com.example.loginui.models.TrainingDetailsModel
import com.example.loginui.models.TrainingModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class WorkoutDatabaseHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "ptl.db"
        private const val DATABASE_VERSION = 1
        private const val DATABASE_PATH = "/data/data/com.example.loginui/databases/"
    }

    private val dbFilePath: String = DATABASE_PATH + DATABASE_NAME
    private val appContext: Context = context.applicationContext

    init{
        copyDatabaseIfNeeded()
    }

    override fun onCreate(db: SQLiteDatabase?) {

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dbFile = File(dbFilePath)
        if (dbFile.exists()) {
            dbFile.delete()
        }
        copyDatabaseIfNeeded()
    }

    private fun copyDatabaseIfNeeded() {
        val dbFile = File(dbFilePath)
        if (!dbFile.exists()) {
            try {
                val dbFolder = File(DATABASE_PATH)
                if (!dbFolder.exists()) {
                    dbFolder.mkdirs()
                }
                copyDatabaseFromAssets()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun copyDatabaseFromAssets() {
        try {
            val inputStream: InputStream = appContext.assets.open(DATABASE_NAME)
            val outputStream: OutputStream = FileOutputStream(dbFilePath)

            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getWritableDatabase(): SQLiteDatabase {
        return SQLiteDatabase.openDatabase(dbFilePath, null, SQLiteDatabase.OPEN_READWRITE)
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        return SQLiteDatabase.openDatabase(dbFilePath, null, SQLiteDatabase.OPEN_READONLY)
    }

    fun insertRPR(rprModel: RPRModel){
        val db = writableDatabase
        val values = ContentValues().apply{
            put("doms", rprModel.doms)
            put("energy", rprModel.energy)
            put("sleep", rprModel.sleep)
            put("mood", rprModel.mood)
            put("client", rprModel.clientID)
            put("borg", rprModel.borg)
            put("avg", rprModel.index)
            put("training", rprModel.training)
        }
        db.insert("rpr", null, values)
        db.close()
    }


    fun insertClient(clientModel: ClientModel){
        val db = writableDatabase
        val values = ContentValues().apply{
            put("name", clientModel.name)
            put("surname", clientModel.surname)
        }
        db.insert("client", null, values)
        db.close()
    }

    fun insertExercise(exerciseModel: ExerciseModel){
        val db = writableDatabase
        val values = ContentValues().apply{
            put("name", exerciseModel.name)
            put("category", exerciseModel.category)
            put("physicalGoal", exerciseModel.physicalGoal)
            put("type", exerciseModel.type)
        }
        db.insert("exercise", null, values)
        db.close()
    }

    fun insertTraining(training: TrainingModel){
        val db= writableDatabase
        val values = ContentValues().apply{
            put("id", training.id)
            put("date", training.date)
            put("duration", training.duration)
            put("client", training.clientId)
            put("operator", training.personalTrainerId)
            put("workoutNumber", training.workoutNumber)
        }
        db.insert("training", null, values)
        db.close()
    }

    fun insertTrainingDetails(trainingDetails: TrainingDetailsModel){
        val db=writableDatabase
        val values = ContentValues().apply {
            put("reps", trainingDetails.reps)
            put("sets", trainingDetails.sets)
            put("weight", trainingDetails.weight)
            put("training", trainingDetails.trainingId)
            put("exercise", trainingDetails.exerciseId)
            put("note", trainingDetails.note)
            put("borg", trainingDetails.borg.toString())
            put("executionTime", trainingDetails.executionTime)
        }
        db.insert("trainingDetails", null, values)
        db.close()
    }

    fun insertOperator(personalTrainerMode: PersonalTrainerModel){
        val db=writableDatabase
        val values = ContentValues().apply {
            put("name", personalTrainerMode.name)
            put("uid", personalTrainerMode.uid)
        }
        db.insert("operator", null, values)
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
        val query = "SELECT COUNT(*) FROM training"
        val cursor = db.rawQuery(query, null)
        var count=0
        if(cursor.moveToFirst()){
            count=cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    fun getTrainingByClientId(id: String): List<TrainingModel> {
        val trainingList = mutableListOf<TrainingModel>()
        val db = readableDatabase
        val query = "SELECT * FROM training WHERE client=?"
        val cursor = db.rawQuery(query, arrayOf(id))
        while (cursor.moveToNext()) {
            val training = TrainingModel(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("date")),
                cursor.getString(cursor.getColumnIndexOrThrow("duration")),
                cursor.getInt(cursor.getColumnIndexOrThrow("client")).toString(),
                cursor.getInt(cursor.getColumnIndexOrThrow("operator")).toString(),
                cursor.getInt(cursor.getColumnIndexOrThrow("workoutNumber"))
            )
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

    fun getRprByTrainingId(trainingId: String): RPRModel? {
        val db = readableDatabase
        val query = "SELECT * FROM rpr WHERE training=?"
        var rprModel: RPRModel? = null
        val cursor = db.rawQuery(query, arrayOf(trainingId))
        if (cursor.moveToFirst()) {
            rprModel = RPRModel(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("client")),
                cursor.getString(cursor.getColumnIndexOrThrow("mood")),
                cursor.getString(cursor.getColumnIndexOrThrow("sleep")),
                cursor.getString(cursor.getColumnIndexOrThrow("energy")),
                cursor.getString(cursor.getColumnIndexOrThrow("doms")),
                cursor.getString(cursor.getColumnIndexOrThrow("avg")),
                cursor.getString(cursor.getColumnIndexOrThrow("borg")),
                cursor.getInt(cursor.getColumnIndexOrThrow("training"))
            )
        }
        cursor.close()
        db.close()
        return rprModel
    }

    fun getWorkoutNumberByClientId(userId: String): Int{
        val workoutNumber: Int
        val db = readableDatabase
        val query = "SELECT MAX(workoutNumber) FROM training WHERE client=?"
        val cursor = db.rawQuery(query, arrayOf(userId))
        workoutNumber = if (cursor.moveToFirst()){
            cursor.getInt(0)+1
        } else {
            1
        }
        cursor.close()
        db.close()
        return workoutNumber
    }

    fun getActiveTimeByTrainingId(trainingId: Int): Int {
        var activeTime = 0
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT executionTime FROM trainingDetails WHERE training = ?", arrayOf(trainingId.toString()))
        if (cursor.moveToFirst()) {
            do {
                activeTime += cursor.getInt(0)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return activeTime
    }

}