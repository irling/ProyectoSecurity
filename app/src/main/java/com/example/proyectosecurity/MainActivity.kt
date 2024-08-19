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
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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
        )
        if (permissions.any{ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED}){
            ActivityCompat.requestPermissions(this, permissions, 111)
        }else{

        }
    }


//    private fun launcUbicationAct() {
//        val intent = Intent(this, UbicationActivity::class.java)
//        startActivity(intent)
//    }


}