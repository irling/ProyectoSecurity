package com.example.proyectosecurity

import ImageAdapter
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

//INVESTIGAR LA OBTENCION DE FOTOS ===================================================================================

class ImagesActivity : AppCompatActivity() {

    private val imageByteList = mutableListOf<ByteArray>()
    private lateinit var secretKey: SecretKey


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_images)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(256)
        secretKey = keyGenerator.generateKey()

        loadImages()

        val listView: ListView = findViewById(R.id.lvImages)
        val adapter = ImageAdapter(this, imageByteList)
        listView.adapter = adapter

    }

    private fun loadImages() {
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null)

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val data = it.getString(dataColumn)
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                // Leer y encriptar la imagen
                val imageBytes = contentResolver.openInputStream(uri)?.readBytes()
                imageBytes?.let {
                    val encryptedBytes = encryptImage(it, secretKey)
                    imageByteList.add(encryptedBytes)
                }
            }
        }
    }

    private fun encryptImage(imageBytes: ByteArray, secretKey: SecretKey): ByteArray {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher.doFinal(imageBytes)
    }
}
