package com.example.mynoteapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddEditNoteActivity : AppCompatActivity() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextContent: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonDelete: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var noteId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_note)

        // Initialize views
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show back button in the toolbar
        toolbar.setNavigationOnClickListener {
            onBackPressed() // Navigate back when back button is clicked
        }

        editTextTitle = findViewById(R.id.editTextTitle)
        editTextContent = findViewById(R.id.editTextContent)
        buttonSave = findViewById(R.id.buttonSave)
        buttonDelete = findViewById(R.id.buttonDelete)
        progressBar = findViewById(R.id.progressBar)

        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        noteId = intent.getStringExtra("noteId")

        if (noteId != null && noteId!!.isNotEmpty()) {
            loadNote()
        } else {
            // Show the EditText fields and buttons for new note
            showNoteDetails()
        }

        buttonSave.setOnClickListener {
            saveNote()
        }

        buttonDelete.setOnClickListener {
            deleteNote()
        }
    }

    private fun loadNote() {
        progressBar.visibility = View.VISIBLE // Show progress spinner

        noteId?.let { id ->
            db.collection("notes").document(id)
                .get()
                .addOnSuccessListener { document ->
                    editTextTitle.setText(document.getString("title"))
                    editTextContent.setText(document.getString("content"))
                    buttonDelete.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE // Hide progress spinner

                    // Show the EditText fields and buttons after loading note
                    showNoteDetails()
                }
                .addOnFailureListener { e ->
                    // Handle error while loading note
                    Toast.makeText(this, "Error loading note: ${e.message}", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE // Hide progress spinner on failure
                }
        }
    }

    private fun showNoteDetails() {
        // Show note title, content, and buttons
        editTextTitle.visibility = View.VISIBLE
        editTextContent.visibility = View.VISIBLE
        findViewById<View>(R.id.buttonLayout).visibility = View.VISIBLE // Show buttonLayout
    }

    private fun saveNote() {
        val title = editTextTitle.text.toString().trim()
        val content = editTextContent.text.toString().trim()
        val uid = auth.currentUser?.uid

        if (title.isNotEmpty() && content.isNotEmpty() && uid != null) {
            progressBar.visibility = View.VISIBLE // Show progress spinner

            val note = hashMapOf(
                "title" to title,
                "content" to content,
                "timestamp" to Timestamp.now(),
                "uid" to uid // Add user ID to note
            )

            if (noteId != null) {
                db.collection("notes").document(noteId!!)
                    .set(note)
                    .addOnSuccessListener {
                        progressBar.visibility = View.GONE // Hide progress spinner
                        finish() // Close the activity and go back to the previous one
                    }
                    .addOnFailureListener { e ->
                        progressBar.visibility = View.GONE // Hide progress spinner
                        // Handle error while saving note
                        Toast.makeText(this, "Error saving note: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                db.collection("notes")
                    .add(note)
                    .addOnSuccessListener {
                        progressBar.visibility = View.GONE // Hide progress spinner
                        finish() // Close the activity and go back to the previous one
                    }
                    .addOnFailureListener { e ->
                        progressBar.visibility = View.GONE // Hide progress spinner
                        // Handle error while saving note
                        Toast.makeText(this, "Error saving note: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(this, "Please enter title and content", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteNote() {
        noteId?.let { id ->
            progressBar.visibility = View.VISIBLE // Show progress spinner
            db.collection("notes").document(id)
                .delete()
                .addOnSuccessListener {
                    progressBar.visibility = View.GONE // Hide progress spinner
                    finish() // Close the activity and go back to the previous one
                }
                .addOnFailureListener { e ->
                    progressBar.visibility = View.GONE // Hide progress spinner
                    // Handle error while deleting note
                    Toast.makeText(this, "Error deleting note: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
