package com.example.proyectosecurity

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("v1/api/location/send")
    suspend fun sendLocation(@Body locationRequest: LocationRequest): Response<LocationResponse>;

}

// For location request
data class LocationRequest(
    val latitude: Double,
    val longitude: Double
);

// For request response
data class LocationResponse(
    val message: String
);
