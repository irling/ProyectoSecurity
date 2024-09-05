package com.example.proyectosecurity

import com.google.android.gms.common.api.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("api/data")
    suspend fun getData(): Response<DataResponse>
}

// Configuración de Retrofit
val retrofit = Retrofit.Builder()
    .baseUrl("https://8137-2001-1388-65-54ec-3dcb-49cc-3b17-b8ce.ngrok-free.app//")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val apiService = retrofit.create(ApiService::class.java)