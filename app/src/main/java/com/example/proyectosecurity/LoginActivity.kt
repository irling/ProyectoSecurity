package com.example.proyectosecurity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener{accessLogin()}
    }

    private fun accessLogin() {
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etUsername = findViewById<EditText>(R.id.etUsername)

        val username = etUsername.text.toString()
        val password = etPassword.text.toString()


        if(username == "Admin" && password == "Ciber2024$"){
            val intentLogin = Intent(this, MainActivity::class.java)
            startActivity(intentLogin)
        }else{
            Toast.makeText(this, "usuario o contraseña incorrecto", Toast.LENGTH_SHORT).show()
        }
    }
}