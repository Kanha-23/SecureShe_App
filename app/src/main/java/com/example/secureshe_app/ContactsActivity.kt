package com.example.secureshe_app

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
class ContactsActivity : AppCompatActivity() {
    private lateinit var databaseHelper: ContactsDatabaseHelper
    private lateinit var contactsAdapter: ContactsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contacts)

        databaseHelper = ContactsDatabaseHelper(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nameInput = findViewById<EditText>(R.id.name_input)
        val numberInput = findViewById<EditText>(R.id.number_input)
        val saveButton = findViewById<Button>(R.id.save_button)
        val recyclerView = findViewById<RecyclerView>(R.id.contacts_recyclerview)

        recyclerView.layoutManager = LinearLayoutManager(this)
        contactsAdapter = ContactsAdapter(this, databaseHelper)
        recyclerView.adapter = contactsAdapter

        saveButton.setOnClickListener {
            val name = nameInput.text.toString()
            val number = numberInput.text.toString()

            if (name.isNotEmpty() && number.isNotEmpty()) {
                saveContact(name, number)
                nameInput.text.clear()
                numberInput.text.clear()
                contactsAdapter.updateData()
            }
        }

        val button = findViewById<ImageButton>(R.id.action_contact)
        button.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveContact(name: String, number: String) {
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put(ContactsDatabaseHelper.COLUMN_NAME, name)
            put(ContactsDatabaseHelper.COLUMN_NUMBER, number)
        }
        db.insert(ContactsDatabaseHelper.TABLE_NAME, null, values)
    }
}
