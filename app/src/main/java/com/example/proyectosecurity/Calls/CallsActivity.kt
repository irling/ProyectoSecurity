package com.example.proyectosecurity.Calls

import android.content.Context
import android.os.Bundle
import android.provider.CallLog
import android.provider.ContactsContract
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectosecurity.R

class CallsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calls)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupCallLogListView()
    }

    private fun setupCallLogListView() {
        val callLogs = getCallLogs(this)
        val listView = findViewById<ListView>(R.id.listViewCallLogs)
        val adapter = CallLogAdapter(this, callLogs)
        listView.adapter = adapter
    }

    private fun getCallLogs(context: Context): List<CallLogEntry>{
        val callLogList = mutableListOf<CallLogEntry>()
        //
        val cursor = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            null,
            null,
            CallLog.Calls.DATE + " DESC" //SE ORDENA POR ORDEN DE FECHA DESENDIENTE
        )

        cursor?.use {
            val numberColumnIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
            val typeColumnIndex = it.getColumnIndex(CallLog.Calls.TYPE)
            val dateColumnsIndex = it.getColumnIndex(CallLog.Calls.DATE)
            val durationColumnIndex = it.getColumnIndex(CallLog.Calls.DURATION)

            while (it.moveToNext()){
                val number = it.getString(numberColumnIndex)
                val type = it.getInt(typeColumnIndex)
                val date = it.getLong(dateColumnsIndex)
                val duration = it.getLong(durationColumnIndex)
                val contactName = getContactName(context, number)

                callLogList.add(
                    CallLogEntry(
                        number = number,
                        type = type,
                        date = date,
                        duration = duration,
                        contactName = contactName
                    )
                )
            }
        }
        return callLogList
    }

    private fun getContactName(context: Context, phoneNumber: String): String? {
        val projection = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            "${ContactsContract.CommonDataKinds.Phone.NUMBER} = ?",
            arrayOf(phoneNumber),
            null
        )
        cursor?.use{
            if (it.moveToFirst()){
                val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                return it.getString(nameIndex)
            }
        }
        return null
    }

    companion object {
        private const val REQUEST_CODE_READ_CALL_LOG = 100
    }
}
data class CallLogEntry(
    val number : String,
    val type: Int,
    val date: Long,
    val duration: Long,
    val contactName : String? = null
)