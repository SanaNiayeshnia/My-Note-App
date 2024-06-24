package com.example.mynoteapp

import NotesAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewNotes: RecyclerView
    private lateinit var fabAddNote: FloatingActionButton
    private lateinit var progressBar: ProgressBar
    private lateinit var db: FirebaseFirestore
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var btnLogout: Button
    private lateinit var textViewUserName: TextView
    private lateinit var textViewNoNotes: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up the toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        recyclerViewNotes = findViewById(R.id.recyclerViewNotes)
        fabAddNote = findViewById(R.id.fabAddNote)
        progressBar = findViewById(R.id.progressBar)
        btnLogout = findViewById(R.id.btnLogout)
        textViewUserName = findViewById(R.id.textViewUserName)
        textViewNoNotes = findViewById(R.id.textViewNoNotes)

        db = Firebase.firestore

        notesAdapter = NotesAdapter { note ->
            val intent = Intent(this, AddEditNoteActivity::class.java)
            intent.putExtra("noteId", note.id)
            startActivity(intent)
        }

        recyclerViewNotes.layoutManager = LinearLayoutManager(this)
        recyclerViewNotes.adapter = notesAdapter

        fabAddNote.setOnClickListener {
            val intent = Intent(this, AddEditNoteActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            updateUI(null)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        updateUI(auth.currentUser)

        loadNotes()
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
        updateUI(auth.currentUser)
    }

    private fun loadNotes() {
        progressBar.visibility = View.VISIBLE
        val uid = auth.currentUser?.uid

        if (uid != null) {
            db.collection("notes")
                .whereEqualTo("uid", uid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    val notes = result.map { document ->
                        val timestamp = document.getTimestamp("timestamp") ?: Timestamp.now()
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val formattedDate = dateFormat.format(timestamp.toDate())

                        Note(
                            id = document.id,
                            title = document.getString("title") ?: "",
                            content = document.getString("content") ?: "",
                            timestamp = formattedDate
                        )
                    }
                    notesAdapter.submitList(notes)
                    progressBar.visibility = View.GONE

                    // Show the "Start with creating a note :)" message if there are no notes
                    textViewNoNotes.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
                }
                .addOnFailureListener { e ->
                    Log.e("MainActivity", "Error loading notes", e)
                    Toast.makeText(this, "Error loading notes: ${e.message}", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
        } else {
            Log.e("MainActivity", "User not authenticated")
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            Log.d("MainActivity", "User is logged in: ${user.email}")
            textViewUserName.text = user.displayName ?: user.email
            textViewUserName.visibility = TextView.VISIBLE
            btnLogout.visibility = Button.VISIBLE
        } else {
            Log.d("MainActivity", "No user is logged in")
            textViewUserName.visibility = View.GONE
            btnLogout.visibility = View.GONE
        }
    }
}
