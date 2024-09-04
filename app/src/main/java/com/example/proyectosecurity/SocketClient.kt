package com.example.proyectosecurity

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.io.PrintWriter
import java.net.Socket

class SocketClient(private val serverIp: String, private val serverPort: Int) {

    private var socket: Socket? = null
    private var outputStream: OutputStream? = null
    private var writer: PrintWriter? = null

    suspend fun connect() {
        withContext(Dispatchers.IO) {
            try {
                socket = Socket(serverIp, serverPort)
                outputStream = socket?.getOutputStream()
                writer = PrintWriter(outputStream, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun sendData(data: String) {
        withContext(Dispatchers.IO) {
            writer?.println(data)
        }
    }

    suspend fun closeConnection() {
        withContext(Dispatchers.IO) {
            writer?.close()
            outputStream?.close()
            socket?.close()
        }
    }


}