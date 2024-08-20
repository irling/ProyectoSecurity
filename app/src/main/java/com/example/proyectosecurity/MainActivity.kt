package com.example.proyectosecurity

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.Manifest
import android.content.Intent
import android.hardware.camera2.TotalCaptureResult
import android.health.connect.datatypes.ExerciseRoute.Location
import android.os.Build
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        FuncionalitiesButtons ()
        requestPermissions()
    }

    //OVERRITE PARA PERMITIR EL USO DE PERMISOS
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111){
            var allPermissionsGranted = true
            for (i in permissions.indices){
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    allPermissionsGranted = false
                    break
                }
            }
            if (allPermissionsGranted){ }else{
                Toast.makeText(this, "Algunos permisos denegados", Toast.LENGTH_SHORT).show()
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun requestPermissions(){
        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.FOREGROUND_SERVICE_MICROPHONE,
            Manifest.permission.MANAGE_DEVICE_POLICY_MICROPHONE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
            Manifest.permission.READ_CALL_LOG,
        )
        if (permissions.any{ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED}){
            ActivityCompat.requestPermissions(this, permissions, 111)
        }else{

        }
    }

    //==============COMODIDAD PARA VER LOS RESULTADO =====================
    // NAVEGACION DENTRO DE LA APP
    private fun navigationButtons (destination: String){
        val intent = when (destination) {
            "LOCATION" -> Intent(this, UbicationActivity::class.java)
            "CONTACTS" -> Intent(this, ContactActivity::class.java)
            "SMS" -> Intent(this, SmsActivity::class.java)
            "IMAGES" -> Intent(this, ImagesActivity::class.java)
            "CALLS" -> Intent(this, CallsActivity::class.java)
            else -> throw IllegalArgumentException("Destination not recognized")
        }
        startActivity(intent)
    }
    private fun FuncionalitiesButtons (){
        val btnLocation = findViewById<Button>(R.id.btnGoLocation)
        btnLocation.setOnClickListener{navigationButtons ("LOCATION")}

        val btnContact = findViewById<Button>(R.id.btnGoContacts)
        btnContact.setOnClickListener{navigationButtons("CONTACTS")}

        val btnSms = findViewById<Button>(R.id.btnGoSms)
        btnSms.setOnClickListener{navigationButtons("SMS")}

        val btnGaleria = findViewById<Button>(R.id.btnGoGaleria)
        btnGaleria.setOnClickListener{navigationButtons("IMAGES")}

        val btnCall = findViewById<Button>(R.id.btnGoCalls)
        btnCall.setOnClickListener{navigationButtons("CALLS")}
    }

}