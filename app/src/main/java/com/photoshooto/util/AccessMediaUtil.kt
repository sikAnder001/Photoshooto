package com.photoshooto.util

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.anilokcun.uwmediapicker.UwMediaPicker.Companion.with
import com.anilokcun.uwmediapicker.UwMediaPicker.GalleryMode
import com.anilokcun.uwmediapicker.model.UwMediaPickerMediaModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AccessMediaUtil : AppCompatActivity() {
    var currentMediaPath: String? = null
    var currentMediaPathList: MutableList<String> = ArrayList()
    var photoURI: Uri? = null
    var compressedImage: File? = null
    var isMultipleMediaSelection = false
    var isMediaFromCamera = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isMultipleMediaSelection = intent.hasExtra(MULTIPLE_MEDIA_SELECTION)
        if (isPermissionGranted(this)) {
            selectImage()
        }
    }

    fun selectImage() {
        val options: Array<CharSequence>
        options = if (intent.hasExtra(ONLY_CAMERA_OPTION)) {
            arrayOf("Take Photo", "Cancel")
        } else {
            arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Photo !!")
        builder.setCancelable(false)
        builder.setItems(options) { dialog: DialogInterface, item: Int ->
            if (options[item] == "Take Photo") {
                isMediaFromCamera = true
                dispatchTakePictureIntent()
            } else if (options[item] == "Choose from Gallery") {
                isMediaFromCamera = false
                dispatchPickFromGalleryIntent()
            } else {
                dialog.dismiss()
                onBackPressed()
            }
        }
        builder.show()
    }

    private fun dispatchPickFromGalleryIntent() {
        val mediaSelected = onMediaSelected(ArrayList())
        if (mediaSelected != null) {
            with(this) // Activity or Fragment
                .setGalleryMode(GalleryMode.ImageGallery) // GalleryMode: ImageGallery/VideoGallery/ImageAndVideoGallery, default is ImageGallery
                .setGridColumnCount(4) // Grid column count, default is 3
                .setMaxSelectableMediaCount(if (isMultipleMediaSelection) 5 else 1) // Maximum selectable media count, default is null which means infinite
                .setLightStatusBar(true) // Is llight status bar enable, default is true
                .enableImageCompression(true) // Is image compression enable, default is false
                .setCompressFormat(Bitmap.CompressFormat.JPEG) // Compressed image's format, default is JPEG
                .setCompressionQuality(65)
                .launch { media: List<UwMediaPickerMediaModel>? -> onMediaSelected(media) }
        }

//        Intent returnFromGalleryIntent = new Intent();
//        returnFromGalleryIntent.putExtra(LIBRARY_MEDIA_SELECTION, "");
//        setResult(RESULT_OK, returnFromGalleryIntent);
//        finish();
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(
                Date()
            )
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        // Save a file: path for use with ACTION_VIEW intents
        currentMediaPath = image.absolutePath
        return image
    }

    @Throws(IOException::class)
    private fun createVideoFile(): File {
        @SuppressLint("SimpleDateFormat") val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(
                Date()
            )
        val imageFileName = "Qoloni_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val video = File.createTempFile(imageFileName, ".mp4", storageDir)
        currentMediaPath = video.absolutePath
        return video
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Create the File where the photo should go
        var photoFile: File? = null
        try {
            photoFile = createImageFile()
        } catch (ex: IOException) {
            // Error occurred while creating the File...
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            photoURI = FileProvider.getUriForFile(this, "com.photoshooto.provider", photoFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
        }
    }

    private fun dispatchTakeVideoIntent() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.putExtra(
            MediaStore.EXTRA_SIZE_LIMIT,
            20 * 1024 * 1024L
        ) //Limit the recording size(10M=10 * 1024 * 1024L)
        intent.putExtra(
            MediaStore.EXTRA_DURATION_LIMIT,
            50
        ) //Limit the recording time(10 seconds=10)
        if (intent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createVideoFile()
            } catch (ex: IOException) {
                Log.i("mediaCreation", "error on media creation : " + ex.message)
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this, "com.photoshooto.provider", photoFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, REQUEST_TAKE_VIDEO)
            }
        }
    }

    private fun onMediaSelected(media: List<UwMediaPickerMediaModel>?) {
        Log.d("nex964", media.toString())
        if (media!!.size < 1) return
        if (!isMultipleMediaSelection) {
            currentMediaPath = media[0].mediaPath
            Log.i("path", currentMediaPath!!)
        } else {
            for (i in media.indices) {
                currentMediaPathList.add(media[i].mediaPath)
                Log.i("path", media[i].mediaPath)
            }
        }
        galleryAddPic()
        return
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
                compressedImage = bitmapFile
                currentMediaPath = compressedImage!!.path
                galleryAddPic()
            } else if (requestCode == REQUEST_TAKE_VIDEO && resultCode == RESULT_OK) {
                galleryAddPic()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }
    }

    private fun galleryAddPic() {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val returnFromGalleryIntent = Intent()
        if (!isMultipleMediaSelection || isMediaFromCamera) {
            val f = File(currentMediaPath)
            val contentUri = Uri.fromFile(f)
            mediaScanIntent.data = contentUri
            sendBroadcast(mediaScanIntent)
            returnFromGalleryIntent.putExtra(PICTURE_PATH, currentMediaPath)
        } else {
            for (i in currentMediaPathList.indices) {
                val f = File(currentMediaPathList[i])
                val contentUri = Uri.fromFile(f)
                mediaScanIntent.data = contentUri
                sendBroadcast(mediaScanIntent)
            }
            returnFromGalleryIntent.putStringArrayListExtra(
                PICTURE_PATH_LIST,
                currentMediaPathList as ArrayList<String>
            )
        }
        setResult(RESULT_OK, returnFromGalleryIntent)
        finish()
    }

    private val bitmapFile: File?
        private get() {
            val fullSizedBitmap = BitmapFactory.decodeFile(currentMediaPath)
            var reducedBitmap = ImageResizer.reduceBitmapSize(fullSizedBitmap, 307200)
            var ei: ExifInterface? = null
            try {
                ei = ExifInterface(currentMediaPath!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            assert(ei != null)
            val orientation = ei!!.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
            reducedBitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(reducedBitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(reducedBitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(reducedBitmap, 270f)
                ExifInterface.ORIENTATION_NORMAL -> reducedBitmap
                else -> reducedBitmap
            }
            @SuppressLint("SimpleDateFormat") val timeStamp =
                SimpleDateFormat("yyyyMMdd_HHmmss").format(
                    Date()
                )
            val imageFileName = "PhotoShooto_" + timeStamp + "_"
            val storageDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            var file: File? = null
            try {
                file = File.createTempFile(imageFileName, ".jpg", storageDir)
                val byteArrayOutputStream = ByteArrayOutputStream()
                reducedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
                val bitmapData = byteArrayOutputStream.toByteArray()
                file.createNewFile()
                val fos = FileOutputStream(file)
                fos.write(bitmapData)
                fos.flush()
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return file
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200) {
            if (!isPermissionGranted(this)) {
                Toast.makeText(
                    this,
                    "You have to allow all the permissions to access content from camera and gallery",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                selectImage()
            }
        }
    }

    companion object {
        fun rotateImage(source: Bitmap, angle: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(
                source, 0, 0, source.width, source.height,
                matrix, true
            )
        }
    }
}