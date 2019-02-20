package com.pradovinicius.habitrainer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        tv_title.text = getString(R.string.drink_water)
        tv_description.text = getString(R.string.drink_water_description)
        iv_icon.setImageResource(R.drawable.water)
    }

}
