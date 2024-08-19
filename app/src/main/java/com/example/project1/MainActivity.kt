package com.example.project1

import MyDatabaseHelper
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val isChecked = sharedPref.getBoolean("isChecked", false)

        if (isChecked) {
            val email = sharedPref.getString("email", "")
            val pass = sharedPref.getString("pass", "")
            val db = MyDatabaseHelper(this)

            if (!email.isNullOrEmpty() && !pass.isNullOrEmpty()) {
                val checkUserPass = db.checkUsernamePassword(email, pass)
                if (checkUserPass) {
                    val name = db.getNameByEmail(email)
                    val intent = Intent(this, NoteActivity::class.java).apply {
                        putExtra("name", name)
                        putExtra("email", email)
                    }
                    startActivity(intent)
                    finish()
                    return
                }
            }
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main, RegisterFragment())
                .commit()
        }
    }
}
