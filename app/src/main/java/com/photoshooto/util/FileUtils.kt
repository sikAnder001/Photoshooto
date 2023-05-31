package com.photoshooto.util

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import java.io.*
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {

    fun File.appendTimeStampInFileName(): String {
        val originalFileName = this.name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileExt = originalFileName.substring(originalFileName.lastIndexOf("."))
        return originalFileName.substring(
            0,
            originalFileName.lastIndexOf(".")
        ) + "_" + timeStamp + fileExt
    }

    @SuppressLint("NewApi", "Recycle")
    @Throws(URISyntaxException::class)
    fun getPath(context: Context, uri: Uri): String? {
        var mURI = uri
        val needToCheckUri = Build.VERSION.SDK_INT >= 19
        var selection: String? = null
        var selectionArgs: Array<String>? = null

        if (needToCheckUri && DocumentsContract.isDocumentUri(context.applicationContext, mURI)) {
            when {
                isExternalStorageDocument(mURI) -> {
                    val docId = DocumentsContract.getDocumentId(mURI)
                    val split =
                        docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
                isDownloadsDocument(mURI) -> {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val extPath =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
                    if (docId == "downloads") {
                        mURI = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            java.lang.Long.valueOf(docId)
                        )
                        return mURI.path
                    }/*else if (docId == "20") {
                        mURI = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            java.lang.Long.valueOf(docId)
                        )
                        return mURI.path
                    }*/ else if (docId.matches("^ms[df]\\:.*".toRegex())) {
                        val fileName: String? = getFileName(uri, context)
                        return "$extPath/$fileName"
                    } else if (docId.startsWith("raw:")) {
                        return docId.split(":".toRegex()).toTypedArray()[1]
                    } else if (docId != "downloads") {
                        mURI = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            java.lang.Long.valueOf(docId)
                        )
                        return mURI.path
                    }
                    return null
                }
                isMediaDocument(mURI) -> {
                    val docId = DocumentsContract.getDocumentId(mURI)
                    val split =
                        docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    when (type) {
                        "image" -> mURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> mURI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> mURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }

                    selection = "_id=?"
                    selectionArgs = arrayOf(split[1])
                }
            }
        }

        if ("content".equals(mURI.scheme!!, ignoreCase = true)) {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            var cursor: Cursor? = null
            try {
                cursor =
                    context.contentResolver.query(mURI, projection, selection, selectionArgs, null)
                val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (cursor.moveToFirst()) {
                    return cursor.getString(columnIndex)
                }
            } catch (e: Exception) {
            }
        } else if ("file".equals(mURI.scheme!!, ignoreCase = true)) {
            return mURI.path
        }
        return null
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is GoogleDrive.
     */
    fun isGoogleDrive(uri: Uri): Boolean {
        return uri.authority?.lowercase(Locale.ROOT) == "com.google.android.apps.docs.storage"
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = MediaStore.Images.Media.DATA
        val projection = arrayOf(
            column
        )
        try {
            cursor = uri?.let {
                context.contentResolver.query(
                    it,
                    projection,
                    selection,
                    selectionArgs,
                    null
                )
            }
            if (cursor != null && cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            if (cursor != null) cursor.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun makeEmptyFileIntoExternalStorageWithTitle(title: String?): File {
        val root = Environment.getExternalStorageDirectory().absolutePath
        return File(root, title)
    }

    @SuppressLint("Range")
    fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme.equals("content")) {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            cursor.use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                if (cut != null) {
                    result = result?.substring(cut + 1)
                }
            }
        }
        return result ?: ""
    }

    @Throws(Exception::class)
    fun saveBitmapFileIntoExternalStorageWithTitle(bitmap: Bitmap, title: String) {
        val fileOutputStream =
            FileOutputStream(makeEmptyFileIntoExternalStorageWithTitle("$title.png"))
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.close()
    }

    @Throws(Exception::class)
    fun saveFileIntoExternalStorageByUri(context: Context, uri: Uri): File? {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val originalSize: Int = inputStream?.available() ?: 0
        var bis: BufferedInputStream? = null
        var bos: BufferedOutputStream? = null
        val fileName = getFileName(context, uri)
        val file: File = makeEmptyFileIntoExternalStorageWithTitle(fileName)
        bis = BufferedInputStream(inputStream)
        bos = BufferedOutputStream(
            FileOutputStream(
                file,
                false
            )
        )
        val buf = ByteArray(originalSize)
        bis.read(buf)
        do {
            bos.write(buf)
        } while (bis.read(buf) !== -1)
        bos.flush()
        bos.close()
        bis.close()
        return file
    }

    private fun getFileName(uri: Uri, context: Context): String? {
        var result: String? = null
        try {
            if (uri.scheme == "content") {
                val cursor = context.contentResolver.query(
                    uri,
                    arrayOf(OpenableColumns.DISPLAY_NAME),
                    null,
                    null,
                    null
                )
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result =
                            cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                    }
                } finally {
                    cursor!!.close()
                }
            }
            if (result == null) {
                result = uri.path
                val cut = result!!.lastIndexOf('/')
                if (cut != -1) {
                    result = result.substring(cut + 1)
                }
            }
        } catch (ex: java.lang.Exception) {
            Log.e("TAG", "Failed to handle file name: $ex")
        }
        return result
    }
}
