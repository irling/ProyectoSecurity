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

    //El ""MissingPermission es añadido porque algunas funcionalidad todavia
    //todavia siguen en fase experimental.
    @SuppressLint("MissingPermission")
    //la suspend fun es necesario ya que para devolver un return con suspend, se necesita que la funcion
    //main sea suspendida.
    suspend fun getUserLocation(context: Context): Location? {
        //con esta variable obtenemos la ubicacion exacta del usuario
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        val isUserLocationPermissionsGranted = true

        //estas dos variables, verifican el estado del GPS, si se esta usando internet o gps
        //para obtener la ubicación exacta y validad si esta activado.
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnable =
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER
            )

        //si isGPSEnable es diferente a isUserLocationPermissionGranted, retorna null
        //sino, sigue con el codigo.
        if (isGPSEnable != isUserLocationPermissionsGranted) {
            return null
        }

        //retorna la ubicacion si la "task" es existosa, sino
        //retorna null, en caso de que suceda algun error
        //con la obtencion de la ubicacion, se reanuda la corrutina y muestra un excepcion.
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