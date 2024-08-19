package com.example.project1

import MyDatabaseHelper
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.project1.R

class AddFragment : Fragment() {

    private lateinit var title: EditText
    private lateinit var text: EditText
    private lateinit var save: ImageView
    private lateinit var pink: ImageView
    private lateinit var yellow: ImageView
    private lateinit var blue: ImageView
    private lateinit var green: ImageView
    private lateinit var back: ImageView
    private var email: String? = null
    private var color: String = "grey"

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title = view.findViewById(R.id.title)
        text = view.findViewById(R.id.text)
        save = view.findViewById(R.id.save)
        pink = view.findViewById(R.id.pink)
        yellow = view.findViewById(R.id.yellow)
        blue = view.findViewById(R.id.blue)
        green = view.findViewById(R.id.green)
        back = view.findViewById(R.id.back)

        // Retrieve email from arguments
        email = arguments?.getString("email")

        // Log the email for debugging
        Log.d("AddFragment", "Email retrieved: $email")

        fun updateColorSelection(selectedColor: String, selectedView: ImageView) {
            pink.background = ContextCompat.getDrawable(requireContext(), R.drawable.no_border)
            yellow.background = ContextCompat.getDrawable(requireContext(), R.drawable.no_border)
            blue.background = ContextCompat.getDrawable(requireContext(), R.drawable.no_border)
            green.background = ContextCompat.getDrawable(requireContext(), R.drawable.no_border)

            selectedView.background = ContextCompat.getDrawable(requireContext(), R.drawable.border)
            color = selectedColor
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

        back.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        save.setOnClickListener {
            if (email != null) {
                val myDB = MyDatabaseHelper(requireContext())
                val titleText = title.text.toString().trim()
                val noteText = text.text.toString().trim()
                if (titleText.isNotEmpty() && noteText.isNotEmpty()) {
                    myDB.addNote(
                        titleText,
                        noteText,
                        email!!,
                        color,
                        requireContext()
                    )
                    Toast.makeText(requireContext(), "Note saved successfully", Toast.LENGTH_SHORT).show()

                    parentFragmentManager.setFragmentResult("noteAdded", Bundle().apply {
                        putBoolean("isNoteAdded", true)
                    })

                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Title and text cannot be empty", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Email is not provided", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
