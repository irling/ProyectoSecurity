package com.example.proyectosecurity

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

private fun setupWebSocket() {
    val client = OkHttpClient()
    val request = Request.Builder().url("https://8137-2001-1388-65-54ec-3dcb-49cc-3b17-b8ce.ngrok-free.app/").build()
    val webSocket = client.newWebSocket(request, object : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            if (text == "Solicitar ubicación") {
                sendLocation()
            }
        }
    })
}

private fun sendLocation() {
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location: Location? ->
            if (location != null) {
                // Envía la ubicación al backend
                sendLocationToServer(location)
            }
        }
}

private fun sendLocationToServer(location: Location) {
    // Implementa la lógica para enviar la ubicación al backend
    val latitude = location.latitude
    val longitude = location.longitude

    // Aquí podrías usar Retrofit para enviar la ubicación
    // Ejemplo:
    // apiService.sendLocation(latitude, longitude)
}