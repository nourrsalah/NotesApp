package com.example.project1

import MyDatabaseHelper
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class EditFragment : Fragment() {

    private lateinit var titleInput: EditText
    private lateinit var textInput: EditText
    private lateinit var saveButton: ImageView
    private lateinit var backButton: ImageView
    private lateinit var pink: ImageView
    private lateinit var yellow: ImageView
    private lateinit var blue: ImageView
    private lateinit var green: ImageView
    private var id: String? = null
    private var title: String? = null
    private var text: String? = null
    private var email: String? = null
    private var color: String = "grey"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleInput = view.findViewById(R.id.title11)
        textInput = view.findViewById(R.id.text1)
        saveButton = view.findViewById(R.id.save)
        backButton = view.findViewById(R.id.back)
        pink = view.findViewById(R.id.pink)
        yellow = view.findViewById(R.id.yellow)
        blue = view.findViewById(R.id.blue)
        green = view.findViewById(R.id.green)

        // Retrieve note details from arguments
        id = arguments?.getString("id")
        title = arguments?.getString("title")
        text = arguments?.getString("text")
        color = arguments?.getString("color") ?: "grey"
        email = arguments?.getString("email")

        Log.d("EditFragment", "Email retrieved: $email")
        Log.d("EditFragment", "Note ID: $id")

        fun updateColorSelection(selectedColor: String, selectedView: ImageView) {
            pink.background = ContextCompat.getDrawable(requireContext(), R.drawable.no_border)
            yellow.background = ContextCompat.getDrawable(requireContext(), R.drawable.no_border)
            blue.background = ContextCompat.getDrawable(requireContext(), R.drawable.no_border)
            green.background = ContextCompat.getDrawable(requireContext(), R.drawable.no_border)

            selectedView.background = ContextCompat.getDrawable(requireContext(), R.drawable.border)
            color = selectedColor
        }

        // Populate fields with existing note details
        titleInput.setText(title)
        textInput.setText(text)
        updateColorSelection(color, when (color) {
            "pink" -> pink
            "yellow" -> yellow
            "blue" -> blue
            "green" -> green
            else -> pink // Default color
        })

        saveButton.setOnClickListener {
            confirmSaveDialog()
        }

        backButton.setOnClickListener {
            confirmDiscardDialog()
        }

        pink.setOnClickListener {
            updateColorSelection("pink", pink)
            Toast.makeText(requireContext(), "Pink selected", Toast.LENGTH_SHORT).show()
        }

        yellow.setOnClickListener {
            updateColorSelection("yellow", yellow)
            Toast.makeText(requireContext(), "Yellow selected", Toast.LENGTH_SHORT).show()
        }

        blue.setOnClickListener {
            updateColorSelection("blue", blue)
            Toast.makeText(requireContext(), "Blue selected", Toast.LENGTH_SHORT).show()
        }

        green.setOnClickListener {
            updateColorSelection("green", green)
            Toast.makeText(requireContext(), "Green selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmSaveDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Save changes?")
        builder.setPositiveButton("Yes") { _, _ ->
            saveNote()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun saveNote() {
        val myDB = MyDatabaseHelper(requireContext())
        email?.let {
            val result = myDB.updateData(
                id ?: "",  // Note ID
                titleInput.text.toString().trim(),
                textInput.text.toString().trim(),
                color
            )
            if (result) {
                Toast.makeText(requireContext(), "Note updated successfully", Toast.LENGTH_SHORT).show()

                // Send a result back to HomeFragment
                parentFragmentManager.setFragmentResult("noteUpdated", Bundle().apply {
                    putBoolean("isNoteUpdated", true)
                })

                // Navigate back to HomeFragment
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Failed to update note", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(requireContext(), "Email is null, cannot update note", Toast.LENGTH_SHORT).show()
        }
    }



    private fun confirmDiscardDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Are you sure you want to discard changes?")
        builder.setPositiveButton("Yes") { _, _ ->
            discardChanges()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun discardChanges() {
        parentFragmentManager.popBackStack()
    }
}
