package com.example.proyectosecurity.Location

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.proyectosecurity.ApiService
import com.example.proyectosecurity.LocationRequest
import kotlinx.coroutines.launch
import com.example.proyectosecurity.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class UbicationActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var apiService: ApiService
    private val originU = "https://3dfa-2001-1388-65-870d-50d0-d596-358e-132c.ngrok-free.app"
    private val REQUEST_CODE = 101

    //se llama a la clase LocationService
    private val locationService: LocationService = LocationService()
    private lateinit var tvCoords: TextView

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //Inicializacion del apiService
        val retrofit = Retrofit.Builder().baseUrl(originU)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        enableEdgeToEdge()
        setContentView(R.layout.activity_ubication)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //No olvidar declarar estas variables y llamadas de ID si se añaden más parametros
        tvCoords = findViewById<TextView>(R.id.tvCoords)
        val btnGetLocation = findViewById<Button>(R.id.btnCoords)
        btnGetLocation.setOnClickListener { getLocation() }

        configSocket()
        getLocation()

    }

    //Se obtine la localizacion, si result es diferente a null, si retorna un null
    //devuelve el mensaje que se requiere permiso del gps o internet
    //(se tiene que validar que ambos esten activos)
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun getLocation() {

        //AÑADIENDO SOLICITUD DE PERMISOS
        if (ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED){
            requestPermission()
            return
        }

        lifecycleScope.launch {
            val result = locationService.getUserLocation(this@UbicationActivity)
            Log.e("LocationUbi", "Location ${result}")

            if (result != null) {
                tvCoords.text = "Latitude y Longitud: ${result.latitude} ${result.longitude} "
                sendLocationToServer(result)
            } else {
                tvCoords.text = "Can't get the location, check the internet or permissions"
            }
        }
    }



    //ENVIO DE DATOS AL SERVIDOR
    private fun sendLocationToServer(location: Location) {
        // To call a suspend function
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val locationRequest = LocationRequest(
                    latitude = location.latitude,
                    longitude = location.longitude
                )

                val resp = apiService.sendLocation(locationRequest)
                Log.e("Location", "$resp")

                if (resp.isSuccessful) {
                    val locationResponse = resp.body()
                    println("Respuesta del servidor: ${locationResponse?.message}")
                } else {
                    Log.e("Location", "Respuesta negativa del servidor: ${resp}")
                }

            } catch (e: Exception) {
                Log.e("Location", "No se pudo enviar la localización: ${e}")
            }
        }
    }

    //Request Permissions
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun requestPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.FOREGROUND_SERVICE_LOCATION
        )
        if (permissions.any {
                ActivityCompat.checkSelfPermission(
                    this, it
                ) != PackageManager.PERMISSION_GRANTED
            }) {
            ActivityCompat.requestPermissions(this, permissions, 0)
        }
    }

    //config SOCKET
    fun configSocket() {
        // val originU2 = apiService
        val client = OkHttpClient()
        val request = Request.Builder().url(originU).build()

        Log.e("Location", "Config socket!!!!!!!!!!!!!!!!!!!")
        try {
            // val webSocket =
            client.newWebSocket(request, object : WebSocketListener() {
                @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                override fun onMessage(webSocket: WebSocket, text: String) {

                    //NUEVO A PROBAR SI FUNCIONA
                    if (text == "GetLocation"){
                        lifecycleScope.launch {
                            val result = locationService.getUserLocation(this@UbicationActivity)
                            if (result != null){
                                sendLocationToServer(result)
                            }
                        }
                    }
                    //ANTIGUO PARA VER  REEMPLAZAR
//                    if (text == "GetLocation") {
//                        getLocation()
//                    }
                }
            })
        } catch (e: Exception) {
            Log.e("Error", "configSocket: ${e}")
        }
    }
}
