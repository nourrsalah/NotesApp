package com.example.project1

import MyDatabaseHelper
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    private lateinit var textView: TextView
    private lateinit var textView6: TextView
    private lateinit var recyclerview: RecyclerView
    private lateinit var floating: FloatingActionButton
    private lateinit var custom: CustomAdapter
    private lateinit var myDB: MyDatabaseHelper
    private lateinit var image: ImageView
    private lateinit var search: ImageView
    private lateinit var settings: ImageView
    private lateinit var del: ImageView
    private lateinit var textView2: TextView
    private lateinit var textView3: TextView
    private lateinit var Notes: ArrayList<Note>
    private lateinit var email: String
    private var name: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views
        textView = view.findViewById(R.id.textView)
        recyclerview = view.findViewById(R.id.recyclerview)
        floating = view.findViewById(R.id.floatingActionButton3)
        textView6 = view.findViewById(R.id.textView6)
        textView2 = view.findViewById(R.id.textView2)
        textView3 = view.findViewById(R.id.textView3)
        image = view.findViewById(R.id.imageView2)
        settings = view.findViewById(R.id.settings2)
        search = view.findViewById(R.id.search2)
        del = view.findViewById(R.id.imgDELETE)

        // Retrieve email and name from arguments
        email = arguments?.getString("email").toString()
        name = arguments?.getString("name")

        Log.d("HomeFragment", "Email retrieved: $email")  // Debug log

        search.setOnClickListener {
            val searchFragment = SearchFragment().apply {
                arguments = Bundle().apply {
                    putString("email", email)
                }
            }
            parentFragmentManager.beginTransaction()
                .hide(this@HomeFragment)
                .add(R.id.note, searchFragment)
                .addToBackStack(null)
                .commit()
        }

        floating.setOnClickListener {
            val addFragment = AddFragment().apply {
                arguments = Bundle().apply {
                    putString("email", email)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.note, addFragment)
                .addToBackStack(null)
                .commit()
        }

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                del.setImageResource(R.drawable.del)
//                del.visibility = View.VISIBLE
                val position = viewHolder.adapterPosition
                val note = Notes[position]
                val noteTitle = note.title


                myDB.deleteNote(noteTitle, email)

                custom.removeItem(position)
                custom.notifyDataSetChanged()
                if (Notes.isEmpty()) {
                    image.visibility = View.VISIBLE
                    textView.visibility = View.VISIBLE
                    textView3.visibility = View.VISIBLE
                } else {
                    image.visibility = View.GONE
                    textView.visibility = View.GONE
                    textView3.visibility = View.GONE
                }

                Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show()
            }
        })


        itemTouchHelper.attachToRecyclerView(recyclerview)


        itemTouchHelper.attachToRecyclerView(recyclerview)

        myDB = MyDatabaseHelper(requireContext())
        Notes = ArrayList()

        custom = CustomAdapter(requireActivity(), requireContext(), Notes)
        recyclerview.adapter = custom
        recyclerview.layoutManager = LinearLayoutManager(requireContext())

        name?.let {
            textView6.text = "$it’s Notes"
            textView.text = "Hello $it"
        }

        storeDataInArrays()

        parentFragmentManager.setFragmentResultListener("noteAdded", viewLifecycleOwner) { _, bundle ->
            val isNoteAdded = bundle.getBoolean("isNoteAdded", false)
            if (isNoteAdded) {
                textView6.text = "${name ?: "User"}’s Notes"
                storeDataInArrays()
            }
        }
        parentFragmentManager.setFragmentResultListener("noteUpdated", viewLifecycleOwner) { _, bundle ->
            val isNoteUpdated = bundle.getBoolean("isNoteUpdated", false)
            if (isNoteUpdated) {
                storeDataInArrays() // Refresh notes
            }
        }
//        parentFragmentManager.setFragmentResultListener("noteUpdated", viewLifecycleOwner) { _, bundle ->
//            val isNoteUpdated = bundle.getBoolean("isNoteUpdated", false)
//            if (isNoteUpdated) {
//                textView6.text = "${name ?: "User"}’s Notes" // Update textView6
//                storeDataInArrays() // Refresh notes
//            }
//        }

        settings.setOnClickListener {
            showPopupMenu(settings)
        }
        return view
    }
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.menu_settings, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.logout -> {
                    logout()
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }
    private fun logout() {
        val sharedPref = requireActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()

        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun storeDataInArrays() {
        val cursor: Cursor? = myDB.readAllNotes(email)

        if (cursor != null) {
            if (cursor.count == 0) {
                textView.visibility = View.VISIBLE
                textView3.visibility = View.VISIBLE
                image.visibility = View.VISIBLE
            } else {
                textView.visibility = View.GONE
                textView2.visibility = View.GONE
                textView3.visibility = View.GONE
                image.visibility = View.GONE

                Notes.clear()
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                    val description = cursor.getString(cursor.getColumnIndexOrThrow("text"))
                    val color = cursor.getString(cursor.getColumnIndexOrThrow("color"))
                    val timestamp = cursor.getString(cursor.getColumnIndexOrThrow("time"))
                    Notes.add(Note(id, title, description, email, color, timestamp))
                }

                custom.notifyDataSetChanged()
            }
            cursor.close()
        }
    }
}
