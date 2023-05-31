package com.photoshooto.ui.job.utility

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class GetCurrentLocation {

    companion object {
        fun getLastLocation(
            mContext: Context,
            locationIno: (latitude: Double, longitude: Double, address: MutableList<Address>) -> Unit
        ) {

            val fusedLocationClient: FusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(mContext)
            if (ActivityCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnCompleteListener {
                    if (it.isSuccessful && it.result != null) {
                        val geocoder = Geocoder(mContext)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            geocoder.getFromLocation(
                                it.result.latitude, it.result.longitude, 1
                            ) { addresses ->
                                locationIno.invoke(
                                    it.result.latitude,
                                    it.result.longitude,
                                    addresses
                                )
                                //    saveLocation(addresses, it.result)
                            }
                        } else {
                            val address: MutableList<Address>? =
                                geocoder.getFromLocation(it.result.latitude, it.result.longitude, 1)
                            if (address?.isNotEmpty() == true) {
                                locationIno.invoke(
                                    it.result.latitude,
                                    it.result.longitude,
                                    address
                                )
                                // saveLocation(address, it.result)
                            }
                        }
                    }
                }
            }
        }
    }
}