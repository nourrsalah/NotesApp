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
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class RegisterFragment : Fragment() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var etName: EditText
    private lateinit var etPass: EditText
    private lateinit var etEmail: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        sharedPref = requireActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE)
        editor = sharedPref.edit()

        etName = view.findViewById(R.id.etName)
        etPass = view.findViewById(R.id.etPass)
        etEmail = view.findViewById(R.id.etEmail)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)
        val textView = view.findViewById<TextView>(R.id.textView)

        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val pass = etPass.text.toString()
            val email = etEmail.text.toString()

            if (name.isEmpty() || pass.isEmpty() || email.isEmpty()) {
                Toast.makeText(activity, "Please enter all the fields", Toast.LENGTH_SHORT).show()
            } else {
                val db = MyDatabaseHelper(activity)
                val checkUser = db.checkUsername(name)
                if (!checkUser) {
                    val insert = db.insertUser(name, email, pass)
                    if (insert) {
                        Toast.makeText(activity, "Registered successfully", Toast.LENGTH_SHORT).show()
                        editor.putString("name", name)
                        editor.putString("email", email)
                        editor.putString("pass", pass)
                        editor.apply()

                        val loginFragment = LoginFragment()
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.main, loginFragment)
                            .commit()
                    } else {
                        Toast.makeText(activity, "Registration failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(activity, "User already exists! Please sign in", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val spannableString = SpannableString("Already a member? Log In")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val loginFragment = LoginFragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main, loginFragment)
                    .commit()
            }
        }
        spannableString.setSpan(clickableSpan, 17, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setLinkTextColor(ContextCompat.getColor(requireContext(), R.color.cyan))

        return view
    }
}
