package com.example.proyectosecurity.InfoCellPhone

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.proyectosecurity.ApiService
import com.example.proyectosecurity.DeviceInfoRequest
import com.example.proyectosecurity.R
import com.example.proyectosecurity.SocketClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class GetInfoPhone() : AppCompatActivity() {

    private lateinit var lvInfoPhone: ListView
    private lateinit var apiService: ApiService
    private val originU = "https://3dfa-2001-1388-65-870d-50d0-d596-358e-132c.ngrok-free.app"


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Init apiService
        val retrofit = Retrofit.Builder().baseUrl(originU)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        enableEdgeToEdge()
        setContentView(R.layout.activity_get_info_phone)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initComponents()
        configSocket()
        getInfoCellPhone()
        senDeviceInfo()
    }

    private fun initComponents() {
        lvInfoPhone = findViewById(R.id.lvInfoPhone)
    }

    @SuppressLint("HardwareIds")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getInfoCellPhone(): DeviceInfoRequest {
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

        val deviceInfoRequest = DeviceInfoRequest(
            deviceModel, androidId, totalRam, cpuAbi, osVersion, manufacture, serialNumber
        )

        return deviceInfoRequest
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun senDeviceInfo(){
        val deviceInfo = getInfoCellPhone()
        sendDeviceInfoToServer(deviceInfo)
    }

    private fun sendDeviceInfoToServer(deviceInfo: DeviceInfoRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.sendDeviceInfo(deviceInfo)
                if (response.isSuccessful) {
                    val serverResponse = response.body()
                    Log.d("DeviceInfo", "Respuesta del servidor: ${serverResponse?.message}")
                } else {
                    Log.e("DeviceInfo", "Error en respuesta del servidor $response")
                }
            } catch (e: Exception) {
                Log.e("DeviceInfo", "No se pudo enviar la informacion del dispositivo: $e")
            }
        }
    }

    private fun configSocket() {
        // val originU2 = apiService
        val client = OkHttpClient()
        val request = Request.Builder().url(originU).build()

        Log.e("Location", "Config socket!!!!!!!!!!!!!!!!!!!")
        try {
            // val webSocket =
            client.newWebSocket(request, object : WebSocketListener() {
                @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                override fun onMessage(webSocket: WebSocket, text: String) {

                    if (text == "GetDeviceInfo") {
                        val deviceInfo = getInfoCellPhone()

                        val json = Gson().toJson(deviceInfo)

                        webSocket.send(json)

                        Log.d("DevideInfo", "Informacion del dispositivo enviado: $json")
                    }

                }
            })
        } catch (e: Exception) {
            Log.e("Error", "configSocket: ${e}")
        }
    }
}

