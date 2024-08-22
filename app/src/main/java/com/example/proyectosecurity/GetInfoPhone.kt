package com.example.proyectosecurity

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GetInfoPhone : AppCompatActivity() {

    private lateinit var lvInfoPhone: ListView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_get_info_phone)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initComponents()
        getInfoCellPhone()
    }

    private fun initComponents() {
        lvInfoPhone = findViewById(R.id.lvInfoPhone)
    }

    @SuppressLint("HardwareIds")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getInfoCellPhone (){
        val deviceModel = Build.MODEL
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        //val imei = telephonyManager.imei

        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalRam = memoryInfo.totalMem

        val cpuAbi = Build.SUPPORTED_ABIS.joinToString(", ")

        val osVersion = Build.VERSION.RELEASE

        val manufacture = Build.MANUFACTURER

        val serialNumber = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            try {
                Build.getSerial()
            }catch (e: SecurityException){
                "Permiso concedido"
            }
        }else{
            Build.SERIAL
        }

        val deviceInfo = listOf(
            "Device Model: $deviceModel",
            //"IMEI: $imei",
            "Total RAM: ${totalRam / (1024 * 1024)} MB",
            "CPU ABI: $cpuAbi",
            "OS Version: $osVersion",
            "Manufacturer: $manufacture",
            "Serial Number: $serialNumber"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceInfo)
        lvInfoPhone.adapter = adapter
    }
}