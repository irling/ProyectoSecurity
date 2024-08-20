package com.example.proyectosecurity

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class UbicationActivity : AppCompatActivity() {

    private val locationService: LocationService = LocationService()
    private lateinit var tvCoords: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ubication)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //No olvidar declarar estas variables y llamadas de ID
        tvCoords = findViewById<TextView>(R.id.tvCoords)
        val btnGetLocation = findViewById<Button>(R.id.btnCoords)
        btnGetLocation.setOnClickListener{getLocation()}


        getLocation()
    }

    private fun getLocation(){
        lifecycleScope.launch {
            val result = locationService.getUserLocation(this@UbicationActivity)
            if (result != null){
                tvCoords.text = "Latitude y Longitud: ${result.latitude} ${result.longitude} "
            }else{
                tvCoords.text = "Can't get the location, check the internet or permissions"
            }
        }
    }
}