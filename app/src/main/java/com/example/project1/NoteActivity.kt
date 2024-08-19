package com.example.project1

import MyDatabaseHelper
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class NoteActivity : AppCompatActivity(), DialogInterface.OnClickListener {

    private var email: String? = null
    private var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        val name = intent.getStringExtra("name")
        val email = intent.getStringExtra("email")

        if (email == null) {
            Log.e("NoteActivity", "Email is null")
            // Handle the error, perhaps by redirecting back to login
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        Log.d("NoteActivity", "Received email: $email and name: $name")

        // Pass the email and name to the fragments if needed
        val bundle = Bundle().apply {
            putString("email", email)
            putString("name", name)
        }
        val homeFragment = HomeFragment().apply {
            arguments = bundle
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.note, homeFragment)
            .commit()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            ActivityCompat.recreate(this) // Refresh
        }
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        val myDB = MyDatabaseHelper(this@NoteActivity)
        email?.let {
            myDB.deleteNote(title ?: "", it)
            finish() // Close current activity and return to mainActivity
        } ?: run {
            // Handle the case where email is missing
        }
    }
}
