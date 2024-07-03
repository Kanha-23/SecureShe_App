package com.example.secureshe_app

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class ContactsAdapter(private val context: Context, private val databaseHelper: ContactsDatabaseHelper) :
    RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    private var cursor: Cursor? = null

    init {
        updateData()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        cursor?.moveToPosition(position)

        val id = cursor?.getInt(cursor?.getColumnIndexOrThrow(ContactsDatabaseHelper.COLUMN_ID) ?: 0)
        val name = cursor?.getString(cursor?.getColumnIndexOrThrow(ContactsDatabaseHelper.COLUMN_NAME) ?: 0)
        val number = cursor?.getString(cursor?.getColumnIndexOrThrow(ContactsDatabaseHelper.COLUMN_NUMBER) ?: 0)

        holder.nameTextView.text = name
        holder.numberTextView.text = number

        holder.deleteButton.setOnClickListener {
            deleteContact(id ?: 0)
            updateData()
        }
    }

    override fun getItemCount(): Int {
        return cursor?.count ?: 0
    }

    fun updateData() {
        cursor = databaseHelper.readableDatabase.query(
            ContactsDatabaseHelper.TABLE_NAME,
            null, null, null, null, null, null
        )
        notifyDataSetChanged()
    }

    private fun deleteContact(id: Int) {
        databaseHelper.writableDatabase.delete(
            ContactsDatabaseHelper.TABLE_NAME,
            "${ContactsDatabaseHelper.COLUMN_ID}=?",
            arrayOf(id.toString())
        )
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.contact_name)
        val numberTextView: TextView = itemView.findViewById(R.id.contact_number)
        val deleteButton: Button = itemView.findViewById(R.id.delete_button)
    }
}
