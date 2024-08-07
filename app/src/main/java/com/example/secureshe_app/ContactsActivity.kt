package com.example.secureshe_app

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
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
            applyScaleAnimation(button)
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        val button2 = findViewById<ImageButton>(R.id.action_info)
        button2.setOnClickListener {
            applyScaleAnimation(button2)
            val intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)
        }


        val button3 = findViewById<ImageButton>(R.id.image_z)
        button3.setOnClickListener {
            applyScaleAnimation(button3)
            val intent = Intent(this, GestureActivity::class.java)
            startActivity(intent)
        }

        val button4 = findViewById<ImageButton>(R.id.image_y)
        button4.setOnClickListener {
            applyScaleAnimation(button4)
            val intent = Intent(this, EmergencyActivity::class.java)
            startActivity(intent)
        }
    }
    private fun applyScaleAnimation(view: ImageView) {
        val scaleUp = ScaleAnimation(
            1.0f, 1.2f,  // Start and end values for the X axis scaling
            1.0f, 1.2f,  // Start and end values for the Y axis scaling
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f,  // Pivot point of X scaling
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f   // Pivot point of Y scaling
        )
        scaleUp.duration = 150 // Duration for the scale-up animation

        val scaleDown = ScaleAnimation(
            1.2f, 1.0f,  // Start and end values for the X axis scaling
            1.2f, 1.0f,  // Start and end values for the Y axis scaling
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f,  // Pivot point of X scaling
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f   // Pivot point of Y scaling
        )
        scaleDown.duration = 150 // Duration for the scale-down animation
        scaleDown.startOffset = 150 // Start scale-down after scale-up finishes

        val animationSet = AnimationSet(true)
        animationSet.addAnimation(scaleUp)
        animationSet.addAnimation(scaleDown)
        view.startAnimation(animationSet)
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
