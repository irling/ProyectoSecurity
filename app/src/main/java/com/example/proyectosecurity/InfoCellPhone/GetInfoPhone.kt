package com.example.proyectosecurity.InfoCellPhone

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectosecurity.R
import com.example.proyectosecurity.SocketClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GetInfoPhone : AppCompatActivity() {

    private lateinit var lvInfoPhone: ListView

    //integracion de IP y Port
    private val serverIp = "192.168.1.1"
    private val serverPort = 12345

    private val socketClient = SocketClient(serverIp, serverPort)

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
        //inicializacion del socket
        CoroutineScope(Dispatchers.Main).launch {
            socketClient.connect()
            getInfoCellPhone()
        }
    }

    private fun initComponents() {
        lvInfoPhone = findViewById(R.id.lvInfoPhone)
    }

    @SuppressLint("HardwareIds")
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getInfoCellPhone() {
        val deviceModel = Build.MODEL
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        //  val imei = telephonyManager.getImei()

        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalRam = memoryInfo.totalMem

        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        val cpuAbi = Build.SUPPORTED_ABIS.joinToString(", ")

        val osVersion = Build.VERSION.RELEASE

        val manufacture = Build.MANUFACTURER

        val serialNumber = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Build.getSerial()
            } catch (e: SecurityException) {
                "No se obtuvo el SN"
            }
        } else {
            Build.SERIAL
        }

        val deviceInfo = listOf(
            "Device Model: $deviceModel",
            "Android ID: $androidId",
            "Total RAM: ${totalRam / (1024 * 1024)} MB",
            "CPU ABI: $cpuAbi",
            "OS Version: $osVersion",
            "Manufacturer: $manufacture",
            "Serial Number: $serialNumber"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceInfo)
        lvInfoPhone.adapter = adapter

        sendDevideInfoToServer(deviceInfo)
    }

    private suspend fun sendDevideInfoToServer(deviceInfo: List<String>) {

        //combinancion de informacion en un solo string
        val deviceInfoString = deviceInfo.joinToString("\n")

        //envio de la informacion a trabes del socket
        socketClient.sendData(deviceInfoString)

        //Close connection
        socketClient.closeConnection()
    }

    override fun onDestroy() {
        super.onDestroy()
        CoroutineScope(Dispatchers.Main).launch {
            socketClient.closeConnection()
        }
    }

}