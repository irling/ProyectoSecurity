package com.example.proyectosecurity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class SmsAdapter (context: Context, private val smsList: List<SmsMessage>):
        ArrayAdapter<SmsMessage>(context, 0 , smsList){


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if(view == null){
            view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false)
        }
        val smsMessage = getItem(position)
        val addresstexView = view!!.findViewById<TextView>(android.R.id.text1)
        val bodyTextView = view.findViewById<TextView>(android.R.id.text2)

        addresstexView.text = smsMessage?.address
        bodyTextView.text = smsMessage?.body

        return view
    }
}