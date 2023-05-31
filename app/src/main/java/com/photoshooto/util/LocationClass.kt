package com.photoshooto.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import java.io.IOException
import java.util.*


fun Context.getAddress(latitude: Double?, longitude: Double?, locationAddress: (String) -> Unit) {
    try {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>? = geocoder.getFromLocation(
            latitude!!,
            longitude!!, 1
        )
        if (addresses != null && addresses.isNotEmpty()) {
            val address: String =
                addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            val city: String = checkStringReturnValue(addresses[0].locality)
            val state: String = checkStringReturnValue(addresses[0].adminArea)
            val country: String = checkStringReturnValue(addresses[0].countryName)
            val postalCode: String = checkStringReturnValue(addresses[0].postalCode)
            val knownName: String =
                addresses[0].featureName // Only if available else return NULL

            /*locationData.setCity(city)
            locationData.setFull_address(address)
            locationData.setPincode(postalCode)
            locationData.setCountry(country)*/
            locationAddress.invoke(address)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}