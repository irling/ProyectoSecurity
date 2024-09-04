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
import android.widget.ListView
import com.example.proyectosecurity.R
import java.time.Month

class SmsActivity : AppCompatActivity() {

    private lateinit var lvShowSms: ListView
    private lateinit var smsAdapter: SmsAdapter
    private val smsList = mutableListOf<SmsMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        readAllSms()
    }

    // Con este apartado obtenemos los datos del SMS
    private fun readAllSms() {
        val smsUri: Uri = Telephony.Sms.CONTENT_URI
        val contentResolver: ContentResolver = contentResolver

        //PARA PODER FILTRAR POR FECHA QUE SE REQUIERA.
        //instancia para calcular los datos de la feche a filtrar
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, Calendar.JUNE)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfJune = calendar.timeInMillis
        // Configurar la fecha de fin para el 30 de junio (inclusive)
        calendar.set(Calendar.MONTH, Calendar.JUNE)
        calendar.set(Calendar.DAY_OF_MONTH, 30)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfJune = calendar.timeInMillis
        // Condición de selección para filtrar por fecha
        val selection = "${Telephony.Sms.DATE} >= ? AND ${Telephony.Sms.DATE} <= ?"
        val selectionArgs = arrayOf(startOfJune.toString(), endOfJune.toString())


        val cursor: Cursor? = contentResolver.query(
            smsUri,
            null,
            selection, //SE AÑADIO LA CONDICION --
            selectionArgs, //SE AÑADIO LA CONDICION --
            null
        )

        if (cursor != null && cursor.moveToFirst()){
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms._ID))
                val address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                val body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY))
                val date = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE))

                val smsMessage = SmsMessage(id, address, body, date)
                smsList.add(smsMessage)
            }while (cursor.moveToNext())
            cursor.close()
        }
        smsAdapter.notifyDataSetChanged()
    }
}