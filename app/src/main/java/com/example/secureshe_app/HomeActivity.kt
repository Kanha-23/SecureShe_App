package com.example.secureshe_app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.telephony.SmsManager
import java.io.IOException
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var lm: LocationManager
    private var currentLocation: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button = findViewById<ImageButton>(R.id.action_call)
        button.setOnClickListener {
            val intent = Intent(this, ContactsActivity::class.java)
            startActivity(intent)
        }

        val imageX = findViewById<ImageView>(R.id.image_x)
        imageX.setOnClickListener {
            if (areLocationPermissionsGranted()) {
                getLocationUpdates()
            } else {
                requestLocationPermissions()
            }
        }
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
                sendSms("9173432482", "I know am Atmanirbhar but I need you rn! My current location is: $currentLocation")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
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

    private fun sendSms(phoneNumber: String, message: String) {
        if (areSmsPermissionsGranted()) {
            try {
                val smsManager: SmsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                Toast.makeText(this, "Emergency message sent to $phoneNumber", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to send SMS: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            requestSmsPermissions()
        }
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
                    sendSms("9173432482", "I know am Atmanirbhar but I need you rn! My current location is: $currentLocation")
                }
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 111
        private const val SMS_PERMISSION_REQUEST_CODE = 112
    }
}
