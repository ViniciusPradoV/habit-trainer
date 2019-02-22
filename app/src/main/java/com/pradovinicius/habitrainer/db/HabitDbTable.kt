package com.pradovinicius.habitrainer.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.pradovinicius.habitrainer.Habit
import java.io.ByteArrayOutputStream

class HabitDbTable(context: Context) {

    private val TAG = HabitDbTable::class.java.simpleName

    private val dbHelper = HabitTrainerDb(context)

    fun store(habit: Habit): Long {


        val db = dbHelper.writableDatabase

        val values = ContentValues()
        with(values) {
            put(HabitEntry.TITLE_COL, habit.title)
            put(HabitEntry.DESCR_COL, habit.description)
            put(HabitEntry.IMAGE_COL, toByteArray(habit.image))
        }

            val id: Long = db.transaction {
                insert(HabitEntry.TABLE_NAME, null, values)
            }

            Log.d(TAG, "Stored new habit to the DB $habit" )

            return id
        }

    fun readAllHabits(): List<Habit>{

        val columns = arrayOf(HabitEntry._ID,
            HabitEntry.TITLE_COL,
            HabitEntry.DESCR_COL,
            HabitEntry.IMAGE_COL)

        val db = dbHelper.readableDatabase

        val order ="${HabitEntry._ID} ASC"

        val cursor = db.simpleQuery(HabitEntry.TABLE_NAME,columns,order)

        val habits = parseHabitsFrom(cursor)

        return habits


    }

    private fun parseHabitsFrom(cursor: Cursor): MutableList<Habit> {
        val habits = mutableListOf<Habit>()
        with(cursor) {
            while (moveToNext()) {
                val title = getString(HabitEntry.TITLE_COL)
                val description = getString(HabitEntry.DESCR_COL)
                val bitmap = getBitmap(HabitEntry.IMAGE_COL)
                habits.add(Habit(title, description, bitmap))
            }

            close()
        }
        return habits
    }

}



private fun toByteArray(bitmap: Bitmap): ByteArray {

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()

    }

private inline fun <T> SQLiteDatabase.transaction(function: SQLiteDatabase.()-> T): T{

    beginTransaction()
    val result = try {
        val returnValue = function()
        setTransactionSuccessful()
        returnValue
    }finally {
        endTransaction()
    }
    close()

    return result
}

private fun SQLiteDatabase.simpleQuery(table_name: String,
                                       columns: Array<String>,
                                       orderBy: String? = null,
                                       selection: String? = null,
                                       selectionArgs: Array<String>? = null,
                                       groupBy: String? = null,
                                       having: String? = null): Cursor{

    return this.query(table_name,columns,selection,selectionArgs,groupBy,having,orderBy)
}

private fun Cursor.getString(columnName: String): String = this.getString(this.getColumnIndex(columnName))

private fun Cursor.getBitmap(columnName: String): Bitmap{

    val byteArray = this.getBlob(this.getColumnIndex(columnName))
    return BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)


}
