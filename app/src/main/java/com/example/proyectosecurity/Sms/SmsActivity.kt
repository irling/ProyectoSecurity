package com.example.proyectosecurity.Sms

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.provider.Telephony
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.widget.ListView
import com.example.proyectosecurity.R

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

    private fun readAllSms(){
        val smsUri: Uri = Telephony.Sms.CONTENT_URI
        val contentResolver: ContentResolver = contentResolver

        val cursor: Cursor? = contentResolver.query(
            smsUri,
            null,
            null,
            null,
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