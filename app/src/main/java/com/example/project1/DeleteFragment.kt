package com.example.sqlite

import MyDatabaseHelper
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.project1.R

class DeleteFragment : Fragment() {

    private lateinit var titleInput: EditText
    private lateinit var textInput: EditText
    private lateinit var saveButton: ImageView
    private lateinit var backButton: ImageView
    private lateinit var deleteButton: ImageView
    private var id: Int? = null
    private var title: String? = null
    private var text: String? = null
    private var email: String? = null

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit, container, false)

        titleInput = view.findViewById(R.id.title11)
        textInput = view.findViewById(R.id.text1)
        deleteButton = view.findViewById(R.id.back)

//        setArgumentsData()

            deleteNote()


        return view
    }

    private fun setArgumentsData() {
        arguments?.let {
            if (it.containsKey("id") && it.containsKey("title") && it.containsKey("text") && it.containsKey("email")) {
                id = it.getInt("id")
                title = it.getString("title")
                text = it.getString("text")
                email = it.getString("email")

                titleInput.setText(title)
                textInput.setText(text)
            } else {
                Toast.makeText(requireContext(), "NO DATA", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteNote() {
        val dbHelper = MyDatabaseHelper(requireContext())
        if (title != null && email != null) {
            dbHelper.deleteNote(title!!, email!!)
            Toast.makeText(requireContext(), "Note deleted", Toast.LENGTH_SHORT).show()
            activity?.supportFragmentManager?.popBackStack()
        } else {
            Toast.makeText(requireContext(), "Invalid note title or email", Toast.LENGTH_SHORT).show()
        }
    }
}
