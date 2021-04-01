package com.tutorialsbuzz.devicelocationsettings

import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    override fun onResume() {
        super.onResume()
        requestDeviceLocationSettings();
    }

    // request device

    fun requestDeviceLocationSettings() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
            val state = locationSettingsResponse.locationSettingsStates

            val label =
                "GPS >> (Present: ${state.isGpsPresent}  | Usable: ${state.isGpsUsable} ) \n\n" +
                        "Network >> ( Present: ${state.isNetworkLocationPresent} | Usable: ${state.isNetworkLocationUsable} ) \n\n" +
                        "Location >> ( Present: ${state.isLocationPresent} | Usable: ${state.isLocationUsable} )"

            showToast(label)

            textView.text = label

        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        this@MainActivity,
                        100
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    textView.text = sendEx.message.toString()
                }
            }
        }

    }

    fun showToast(msg: String) {
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
    }
}