package com.example.proyectosecurity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView

class CallLogAdapter (context: Context, private val callLogs: List<CallLogEntry>)
    :ArrayAdapter<CallLogEntry>(context, 0, callLogs){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.activity_calls, parent, false)

        val itemTemplate = view.findViewById<LinearLayout>(R.id.listItemTemplate)

        val tvNumber = itemTemplate.findViewById<TextView>(R.id.tvNumber)
        val tvType = itemTemplate.findViewById<TextView>(R.id.tvType)
        val tvDate = itemTemplate.findViewById<TextView>(R.id.tvDate)
        val tvDuration = itemTemplate.findViewById<TextView>(R.id.tvDuration)

        val callLog = callLogs[position]

        tvNumber.text = callLog.number
        tvType.text = getCallType()
    }
}