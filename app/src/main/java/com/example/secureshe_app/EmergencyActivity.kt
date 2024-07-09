package com.example.secureshe_app

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EmergencyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_emergency)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val button = findViewById<ImageButton>(R.id.action_call)
        button.setOnClickListener {
            applyScaleAnimation(button)
            val intent = Intent(this, ContactsActivity::class.java)
            startActivity(intent)
        }

        val button2 = findViewById<ImageButton>(R.id.action_contact)
        button2.setOnClickListener {
            applyScaleAnimation(button2)
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        val button21 = findViewById<ImageButton>(R.id.action_info)
        button21.setOnClickListener {
            applyScaleAnimation(button21)
            val intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)
        }
        val button3 = findViewById<ImageButton>(R.id.image_z)
        button3.setOnClickListener {
            applyScaleAnimation(button3)
            val intent = Intent(this, GestureActivity::class.java)
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
}