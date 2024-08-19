package com.example.project1

import MyDatabaseHelper
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class LoginFragment : Fragment() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var etPass: EditText
    private lateinit var etEmail: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        sharedPref = requireActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE)
        editor = sharedPref.edit()

        etPass = view.findViewById(R.id.etPass)
        etEmail = view.findViewById(R.id.etEmail)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val textView = view.findViewById<TextView>(R.id.textView)
        val checkBox = view.findViewById<CheckBox>(R.id.checkBox)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val pass = etPass.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(activity, "Please enter all the fields", Toast.LENGTH_SHORT).show()
            } else {
                val db = MyDatabaseHelper(activity)
                val checkUserPass = db.checkUsernamePassword(email, pass)
                if (checkUserPass) {
                    val name = db.getNameByEmail(email)
                    Toast.makeText(activity, "Sign in successful", Toast.LENGTH_SHORT).show()

                    if (checkBox.isChecked) {
                        editor.putString("email", email)
                        editor.putString("pass", pass)
                        editor.putBoolean("isChecked", true)
                        editor.apply()
                    } else {
                        editor.putBoolean("isChecked", false)
                        editor.remove("email")
                        editor.remove("pass")
                        editor.apply()
                    }

                    val intent = Intent(context, NoteActivity::class.java).apply {
                        putExtra("name", name)
                        putExtra("email", email)
                    }
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    Toast.makeText(activity, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val spannableString = SpannableString("New Member? Register now")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val registerFragment = RegisterFragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main, registerFragment)
                    .commit()
            }
        }
        spannableString.setSpan(clickableSpan, 11, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setLinkTextColor(ContextCompat.getColor(requireContext(), R.color.cyan))

        return view
    }
}
