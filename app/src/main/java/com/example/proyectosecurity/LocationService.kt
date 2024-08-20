package com.example.proyectosecurity

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.location.Location
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalCoroutinesApi::class)
class LocationService {

    @SuppressLint("MissingPermission")
    suspend fun getUserLocation(context: Context): Location? {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        val isUserLocationPermissionsGranted = true
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnable =
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER
            )

        if (isGPSEnable != isUserLocationPermissionsGranted) {
            return null
        }

        return suspendCancellableCoroutine<Location?> { cont ->
            fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    cont.resume(result) {}
                } else {
                    cont.resume(null) {}
                }
            }.addOnFailureListener { e ->
                cont.resumeWithException(e)
            }
        }

    }
}