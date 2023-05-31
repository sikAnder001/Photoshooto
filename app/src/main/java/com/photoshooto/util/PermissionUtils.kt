package com.photoshooto.util

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.photoshooto.BuildConfig
import com.photoshooto.R


object PermissionUtils {
    /**
     * Function to request permission from the user
     */
    fun requestAccessFineLocationPermission(activity: AppCompatActivity, requestId: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            requestId
        )
    }

    fun requestAccessFineLocationPermission(activity: FragmentActivity, requestId: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            requestId
        )
    }

    /**
     * Function to check if the location permissions are granted or not
     */
    fun isAccessFineLocationGranted(context: Context): Boolean {
        return ContextCompat
            .checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Function to check if location of the device is enabled or not
     */
    fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    fun showGPSNotEnabledDialog(context: Context) {
        val mDialog = Dialog(context)
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialog.setContentView(R.layout.layout_alart_dialog)
        mDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog.setCanceledOnTouchOutside(false)
        mDialog.setCancelable(true)

        val tv_dialog_content = mDialog.findViewById<View>(R.id.tv_dialog_content) as TextView
        tv_dialog_content.text = context.getString(R.string.required_for_this_app)
        /*CANCEL*/
        val btn_dialog_cancel = mDialog.findViewById<View>(R.id.btn_dialog_cancel) as Button
        btn_dialog_cancel.setOnClickListener {
            mDialog.dismiss()
            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
        mDialog.show()
    }

    const val LOCATION_PERMISSION_REQUEST_CODE: Int = 100
    const val LOCATION_PERMISSION_CODE = 101

    val LOCATION_PERMISSION_LIST = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    fun noLocationPermissions(activity: Activity): Boolean {
        val FINE_LOCATION_PERMISSION =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        val COARSE_LOCATION_PERMISSION =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)

        return FINE_LOCATION_PERMISSION != PackageManager.PERMISSION_GRANTED || COARSE_LOCATION_PERMISSION != PackageManager.PERMISSION_GRANTED
    }

    fun askLocationPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity, LOCATION_PERMISSION_LIST,
            LOCATION_PERMISSION_CODE
        )
    }

    fun showSettingsDialog(activity: Activity) {
        val builder1: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder1.setMessage(
            "You have previously declined Location permission." +
                    "You must approve this permission in the app settings on your device."
        )
        builder1.setCancelable(false)
        builder1.setPositiveButton("OK") { dialog, id ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
            activity.startActivity(intent)
        }
        builder1.setNegativeButton("Cancel", null)
        val alert11: AlertDialog = builder1.create()
        alert11.show()
    }

//    fun showWidgetOverLayDialog(context: Context) {
//        val mDialog = Dialog(context)
//        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        mDialog.setContentView(R.layout.layout_alart_dialog)
//        mDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        mDialog.setCanceledOnTouchOutside(false)
//        mDialog.setCancelable(true)
//
//        val tv_dialog_content = mDialog.findViewById<View>(R.id.tv_dialog_content) as TextView
//        tv_dialog_content.text = context.getString(R.string.widget_overLay_message)
//
//        /*CANCEL*/
//        val btn_dialog_cancel = mDialog.findViewById<View>(R.id.btn_dialog_cancel) as Button
//        btn_dialog_cancel.setOnClickListener {
//            mDialog.dismiss()
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                val intent = Intent(
//                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                    Uri.parse("package:" + context.packageName)
//                )
//                context.startActivity(intent)
//            }
//
//        }
//        mDialog.show()
//    }
}