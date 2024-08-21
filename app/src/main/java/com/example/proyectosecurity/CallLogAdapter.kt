package com.example.proyectosecurity

import android.content.Context
import android.icu.text.DateFormat
import android.provider.CallLog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import java.sql.Date

class CallLogAdapter (context: Context, private val callLogs: List<CallLogEntry>)
    :ArrayAdapter<CallLogEntry>(context, 0, callLogs){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.activity_calls, parent, false)

        val itemTemplate = view.findViewById<LinearLayout>(R.id.listItemTemplate)
        itemTemplate.visibility = View.VISIBLE

        val tvNumber = itemTemplate.findViewById<TextView>(R.id.tvNumber)
        val tvType = itemTemplate.findViewById<TextView>(R.id.tvType)
        val tvDate = itemTemplate.findViewById<TextView>(R.id.tvDate)
        val tvDuration = itemTemplate.findViewById<TextView>(R.id.tvDuration)
        val tvName = itemTemplate.findViewById<TextView>(R.id.tvName)

        val callLog = callLogs[position]

        tvNumber.text = callLog.number
        tvType.text = getCallType(callLog.type)
        tvDate.text = DateFormat.getDateInstance().format(Date(callLog.date))
        tvDuration.text = "${callLog.duration} seconds"

        //MOSTRAR EL NOMBRE SI ESTA DISPONIBLE, SINO MOSTRAR "Nombre desconocido"
        tvName.text = callLog.contactName ?: "Nombre desconocido"

        return  view
    }

    private fun getCallType(type: Int): String{
        return when (type){
            CallLog.Calls.INCOMING_TYPE -> "Llamada Recibida"
            CallLog.Calls.OUTGOING_TYPE -> "llamada Realizada"
            CallLog.Calls.MISSED_TYPE -> "Llamada Perdida"
            else -> "Desconocido"
        }
    }
}