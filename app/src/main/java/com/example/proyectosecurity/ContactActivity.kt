package com.example.proyectosecurity

import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.provider.ContactsContract
import android.util.Log
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.widget.Toast

class ContactActivity : AppCompatActivity() {
    private lateinit var lvShowContacts: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }
    //con la vareable cursor, ingresamos a la base de datos del dispositivo
    //para poder leer los contactos que tiene guardados, los parametros del URI,
    //lo ponemos todos en "null" para obtener todos los datos del equipo
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
}