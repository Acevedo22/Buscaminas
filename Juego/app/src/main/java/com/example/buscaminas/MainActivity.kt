package com.example.buscaminas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    private lateinit var btnLow: Button
    private lateinit var btnMedium: Button
    private lateinit var btnHard: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        addLogicToBtns()
    }

    fun addLogicToBtns(){
        btnLow = findViewById(R.id.btnLow)
        btnMedium = findViewById(R.id.btnMedium)
        btnHard = findViewById(R.id.btnHard)

        btnLow.setOnClickListener {
            // Navegar a LowActivity
            val intent = Intent(this, LowActivity::class.java)
            startActivity(intent)
        }


        btnMedium.setOnClickListener {
            // Navegar a MediumActivity
            val intent = Intent(this, LowActivity::class.java)
            startActivity(intent)
        }

        btnHard.setOnClickListener {
            // Navegar a HighActivity
            val intent = Intent(this, LowActivity::class.java)
            startActivity(intent)
        }
    }
}