package com.example.mynoteapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mynoteapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var editTextDisplayName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        toolbar.setNavigationOnClickListener {
            finish() // This will close the current activity and return to the previous one
        }

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        editTextDisplayName = findViewById(R.id.editTextDisplayName)
        editTextEmail = findViewById(R.id.editTextEmailSignUp)
        editTextPassword = findViewById(R.id.editTextPasswordSignUp)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPasswordSignUp)
        btnSignUp = findViewById(R.id.btnSignUp)
        progressBar = findViewById(R.id.progressBar)

        btnSignUp.setOnClickListener {
            val displayName = editTextDisplayName.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val confirmPassword = editTextConfirmPassword.text.toString()

            if (password == confirmPassword) {
                // Show progress bar before signing up
                progressBar.visibility = ProgressBar.VISIBLE
                signUpUser(displayName, email, password)
            } else {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signUpUser(displayName: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                // Hide progress bar after sign-up process completes
                progressBar.visibility = ProgressBar.GONE

                if (task.isSuccessful) {
                    // Update user profile with display name
                    val user = auth.currentUser
                    user?.let {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName)
                            .build()

                        it.updateProfile(profileUpdates)
                            .addOnCompleteListener { profileTask ->
                                if (profileTask.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Sign Up successful. Welcome, $displayName!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // Navigate to MainActivity
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish() // Finish this activity so the user cannot go back to it

                                } else {
                                    Toast.makeText(
                                        this,
                                        "Failed to update display name",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(this, "Sign Up failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
