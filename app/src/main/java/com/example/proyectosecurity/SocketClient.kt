package com.example.proyectosecurity

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.io.PrintWriter
import java.net.Socket
class SocketClient(private val serverIp: String, private val serverPort: Int) {

    private var socket: Socket? = null
    private var outputStream: OutputStream? = null
    private var writer: PrintWriter? = null

    suspend fun connect(): Boolean {
        return withContext(Dispatchers.IO) {

            try {
                println("Before")
                socket = Socket(serverIp, serverPort)
                println("Here")
                Log.e("Socket", "$serverIp $serverPort")
                outputStream = socket?.getOutputStream()
                writer = PrintWriter(outputStream, true)
                Log.e("Socket","Conexión establecida con el servidor $serverIp:$serverPort")
                true
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Socket","Error al conectar al servidor: ${e.message}")
                false
            }
        }
    }

    suspend fun sendData(data: String): Boolean {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                writer?.println(data)
                Log.e("Socket","Datos enviados al servidor: $data")
                true
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Socket","Error al enviar datos: ${e.message}")
                false
            }
        }
    }

    suspend fun closeConnection() {
        withContext(Dispatchers.IO) {
            try {
                writer?.close()
                outputStream?.close()
                socket?.close()
                println("Conexión cerrada")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error al cerrar la conexión: ${e.message}")
            }
        }
    }
}