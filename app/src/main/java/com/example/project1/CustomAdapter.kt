package com.example.project1

import MyDatabaseHelper
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(
    private val activity: FragmentActivity,
    private val context: Context,
    private val Notes: ArrayList<Note>
) : RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {

    private lateinit var myDB: MyDatabaseHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.my_row, parent, false)
        myDB = MyDatabaseHelper(context) // Initialize the database helper
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val note = Notes[position]
        holder.title_txt.text = note.title
        holder.timestamp.text = note.timestamp
        val color = note.color ?: "grey"
        when (color) {
            "pink" -> holder.mainLayout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.pink))
            "yellow" -> holder.mainLayout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.yellow))
            "blue" -> holder.mainLayout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.blue))
            "green" -> holder.mainLayout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green))
            else -> holder.mainLayout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.grey))
        }

        holder.mainLayout.setOnClickListener {
            val fragment = EditFragment().apply {
                arguments = Bundle().apply {
                    putString("id", note.id.toString())
                    putString("title", note.title)
                    putString("text", note.text)
                    putString("email", note.email)
                    putString("color", note.color)
                }
            }
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.note, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount() = Notes.size

    fun removeItem(position: Int) {
        if (position >= 0 && position < Notes.size) {
            Notes.removeAt(position)
            notifyItemRemoved(position)
        } else {
            Log.e("CustomAdapter", "Invalid position: $position, list size: ${Notes.size}")

        }
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title_txt: TextView = itemView.findViewById(R.id.textView5)
        val mainLayout: CardView = itemView.findViewById(R.id.cardView)
        val timestamp: TextView = itemView.findViewById(R.id.time)
    }
}
