package com.example.mynoteapp


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        // Check if the user is already logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // If the user is logged in, redirect to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // If the user is not logged in, redirect to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish() // Close SplashActivity
    }
}
