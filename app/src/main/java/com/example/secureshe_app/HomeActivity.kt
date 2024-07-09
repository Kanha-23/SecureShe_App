package com.example.secureshe_app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.location.*
import android.os.Bundle
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.telephony.SmsManager
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import java.io.IOException
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var lm: LocationManager
    private var currentLocation: String = ""
    private lateinit var databaseHelper: ContactsDatabaseHelper
    private lateinit var firebaseAuth: FirebaseAuth

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseHelper = ContactsDatabaseHelper(this)

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

        val imageX = findViewById<ImageView>(R.id.image_x)
        imageX.setOnClickListener {
            applyScaleAnimation(imageX)
            if (areLocationPermissionsGranted()) {
                getLocationUpdates()
            } else {
                requestLocationPermissions()
            }
        }

        val profileButton = findViewById<Button>(R.id.action_profile)
        profileButton.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
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

    private fun areLocationPermissionsGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun getLocationUpdates() {
        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                reverseGeocode(location)
                lm.removeUpdates(this)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        try {
            if (areLocationPermissionsGranted()) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100.2f, locationListener)
                val lastKnownLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                lastKnownLocation?.let { reverseGeocode(it) }
            }
        } catch (ex: SecurityException) {
            ex.printStackTrace()
        }
    }

    private fun reverseGeocode(location: Location) {
        val geocoder = Geocoder(this, Locale.getDefault())

        try {
            val addresses: List<Address> =
                geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                currentLocation = "${address.getAddressLine(0)}, ${address.locality}"
                shareLocation(currentLocation)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    private fun shareLocation(location: String) {
        if (areSmsPermissionsGranted()) {
            sendSmsToAllContacts(location)
        } else {
            requestSmsPermissions()
        }
    }

    private fun areSmsPermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestSmsPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.SEND_SMS),
            SMS_PERMISSION_REQUEST_CODE
        )
    }

    private fun sendSmsToAllContacts(location: String) {
        val db = databaseHelper.readableDatabase
        val cursor: Cursor = db.query(
            ContactsDatabaseHelper.TABLE_NAME,
            arrayOf(ContactsDatabaseHelper.COLUMN_NUMBER),
            null, null, null, null, null
        )

        val smsManager: SmsManager = SmsManager.getDefault()
        val message = "I need you rn! My current location is: $location"

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val number = it.getString(it.getColumnIndexOrThrow(ContactsDatabaseHelper.COLUMN_NUMBER))
                    smsManager.sendTextMessage(number, null, message, null, null)
                } while (it.moveToNext())
            }
        }
        Toast.makeText(this, "Emergency messages sent to all contacts", Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (areLocationPermissionsGranted()) {
                    getLocationUpdates()
                }
            }
            SMS_PERMISSION_REQUEST_CODE -> {
                if (areSmsPermissionsGranted()) {
                    sendSmsToAllContacts(currentLocation)
                }
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 111
        private const val SMS_PERMISSION_REQUEST_CODE = 112
    }
}
