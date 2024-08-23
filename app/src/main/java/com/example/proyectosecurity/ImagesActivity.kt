package com.example.proyectosecurity

import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.ByteArrayOutputStream
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


class ImagesActivity : AppCompatActivity() {

    private val imageList = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var listView: ListView

    private val encryptionKey = "1234567890123456"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_images)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        listView = findViewById(R.id.encryptedImageListView)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, imageList)
        listView.adapter = adapter

        loadImagesAndEncrypt()
    }

    private fun loadImagesAndEncrypt() {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null)

        cursor?.use{
            val columIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            while (cursor.moveToNext()){
                val imagePath = cursor.getString(columIndex)
                val encryptedImagePath = compressAndEncryptImage(imagePath)
                imageList.add(encryptedImagePath)
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun compressAndEncryptImage(imagePath: String): String {
    //cargar la imagen en un bitmap
        val bitmap = BitmapFactory.decodeFile(imagePath)

        //comprenseion de la imagen en un formato JPED manteniendo el 50% de la calidad
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val compressedByte = byteArrayOutputStream.toByteArray()

        //Encriptar los bytes comprimiedos usando AES
        //val key = "1234567890123456"
        val secretKeySpec = SecretKeySpec(encryptionKey.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        val encryptedBytes = cipher.doFinal(compressedByte)

        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

}
