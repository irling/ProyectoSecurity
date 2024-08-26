package com.example.proyectosecurity.Location

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.proyectosecurity.R

class UbicationActivity : AppCompatActivity() {

    //se llama a la clase LocationService
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
        //No olvidar declarar estas variables y llamadas de ID si se añaden más parametros
        tvCoords = findViewById<TextView>(R.id.tvCoords)
        val btnGetLocation = findViewById<Button>(R.id.btnCoords)
        btnGetLocation.setOnClickListener{getLocation()}


        getLocation()
    }

    //Se obtine la localizacion, si resul es diferente a null, si retorna un null
    //devuelve el mensaje que se requiere permiso del gps o internet
    //(se tiene que validar que ambos esten activos)
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
    //Esta parte es para mostrarlo en el .xml, se tendria que modificar para que la obtencion,
    //sea enviada directamente a la base de datos.
}