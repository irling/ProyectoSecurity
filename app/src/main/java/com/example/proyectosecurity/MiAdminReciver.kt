package com.example.proyectosecurity

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MiAdminReciver : DeviceAdminReceiver() {

    //ESTO ES PARA VER LA SEGURIDAD EN EL DISPOSITIVO ANDROID
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)

        Toast.makeText(context, "Administrador de dispositivo activado con exito.", Toast.LENGTH_SHORT).show()
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
    }

}