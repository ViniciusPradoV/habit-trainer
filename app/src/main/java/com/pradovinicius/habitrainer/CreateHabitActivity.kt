package com.pradovinicius.habitrainer

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import com.pradovinicius.habitrainer.db.HabitDbTable
import kotlinx.android.synthetic.main.activity_create_habit.*
import java.io.IOException

class CreateHabitActivity : AppCompatActivity() {

    private val TAG = CreateHabitActivity::class.java.simpleName

    private val CHOOSE_IMAGE_REQUEST = 1

    private var imageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_habit)
    }

    fun storeHabit(v: View){
        if(habit_title.isBlank() || habit_description.isBlank()){
            Log.d(TAG, "No habit stored: title or description missing")
            displayErrorMessage("Your habit needs an engaging title and description")
            return
        }else if(imageBitmap == null){
            Log.d(TAG, "No habit stored: image is missing")
            displayErrorMessage("No habit stored: image is missing")
            return
        }

        // Store the habit

        val title = habit_title.text.toString()
        val description = habit_description.text.toString()
        val habit = Habit(title, description,imageBitmap!!)

        val id = HabitDbTable(this).store(habit)

        if(id == -1L){
            displayErrorMessage("Habit could not be stored")
        }else{

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }




    private fun displayErrorMessage(message: String) {
        error_message.text = message
        error_message.visibility = View.VISIBLE

    }

    fun chooseImage(v: View){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        val chooser = Intent.createChooser(intent, "Choose image for habit")
        startActivityForResult(chooser, CHOOSE_IMAGE_REQUEST)

        Log.d(TAG, "Intent to choose image sent...")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CHOOSE_IMAGE_REQUEST &&
            resultCode == Activity.RESULT_OK &&
            data != null && data.data != null){
            Log.d(TAG, "An image was chosen by the user")

            val bitmap = tryReadBitmap(data.data)

            bitmap?.let{
                imageBitmap = bitmap
                image_preview.setImageBitmap(bitmap)
                Log.d(TAG, "Read image bitmap and updated image view")
            }


        }
    }

    private fun tryReadBitmap(data: Uri): Bitmap? {
        return try{
            MediaStore.Images.Media.getBitmap(contentResolver, data)
        }catch (e: IOException){
            e.printStackTrace()
            null
        }

    }
}

private fun EditText.isBlank()= this.text.toString().isBlank()
