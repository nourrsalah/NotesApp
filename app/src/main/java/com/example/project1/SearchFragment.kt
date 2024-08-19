package com.example.project1

import MyDatabaseHelper
import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var customAdapter: CustomAdapter
    private lateinit var myDB: MyDatabaseHelper
    private lateinit var email: String
    private lateinit var Notes: ArrayList<Note>
    private lateinit var imageView5: ImageView
    private lateinit var textView4: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        searchView = view.findViewById(R.id.view)
        searchRecyclerView = view.findViewById(R.id.recyclerview)
        imageView5 = view.findViewById(R.id.imageView5)
        textView4 = view.findViewById(R.id.textView4)

        email = arguments?.getString("email").toString()

        myDB = MyDatabaseHelper(requireContext())
        Notes = ArrayList()

        customAdapter = CustomAdapter(requireActivity(), requireContext(), Notes)
        searchRecyclerView.adapter = customAdapter
        searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchNotes(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchNotes(it)
                    if (it.isEmpty()) {
                        Notes.clear()
                        customAdapter.notifyDataSetChanged()
//                        imageView5.visibility = View.VISIBLE
//                        textView4.visibility = View.VISIBLE
                    }
                }
                return true
            }
        })

        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                parentFragmentManager.popBackStack()
                true
            } else {
                false
            }
        }
        return view
    }

    private fun searchNotes(query: String) {
        val cursor: Cursor? = myDB.searchNotes(email, query)
        if (cursor != null) {
            Notes.clear()
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val text = cursor.getString(cursor.getColumnIndexOrThrow("text"))
                val color = cursor.getString(cursor.getColumnIndexOrThrow("color"))
                val timestamp = cursor.getString(cursor.getColumnIndexOrThrow("time"))
                Notes.add(Note(id, title, text, email, color, timestamp))
            }
            cursor.close()

            customAdapter.notifyDataSetChanged()

            Log.d("SearchFragment", "Notes size: ${Notes.size}")
            if (Notes.isEmpty()) {
                imageView5.visibility = View.VISIBLE
                textView4.visibility = View.VISIBLE
            } else {
                imageView5.visibility = View.GONE
                textView4.visibility = View.GONE
            }
        }
    }
}

