package com.photoshooto.api_call_package.network

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import android.view.MotionEvent
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Response
import java.util.regex.Matcher
import java.util.regex.Pattern


class UtilTo {
    fun validEmail(email: String?): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    companion object {
        fun isValid(password: String?): Boolean {
            val PASSWORD_PATTERN =
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$"
            val pattern = Pattern.compile(PASSWORD_PATTERN)
            val matcher = pattern.matcher(password)
            return matcher.matches()
        }

        fun toast(context: Context, string: String) =
            Toast.makeText(context, string, Toast.LENGTH_LONG).show()

        fun <T> error(error: ResponseBody?, type: Class<T>?): T {
            return Gson().fromJson(error!!.charStream(), type)
        }

        @SuppressLint("LongLogTag")
        fun <T> print(body: Response<T>): Response<T> {
            //Log.e(TAG, "${body.code()} <<<<<< ${Gson().toJson(body.body() ?: body.errorBody())}")
            return body
        }


        fun sendMessage(context: Context, message: String) {
            try {
                val uri =
                    Uri.parse(/*"whatsapp://send?phone=90${context.getString(R.string.whatsapp_mobile_no)}"*/
                        ""
                    )
                val i = Intent(Intent.ACTION_VIEW, uri)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(i)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                Toast.makeText(context, "WhatsApp not installed.", Toast.LENGTH_SHORT).show()
            }
        }

        fun getVideoId(videoUrl: String): String {
            var videoId = ""
            val regex =
                "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)"
            val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(videoUrl)
            if (matcher.find()) {
                videoId = matcher.group(1)
            }
            return videoId!!
        }

        fun checkIfUrlOK(url: String): Boolean {
            val URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$"

            val p = Pattern.compile(URL_REGEX)
            val m: Matcher = p.matcher(url) //replace with string to compare

            return m.find()
        }

        fun checkUrl(url: String): Boolean {
            val pattern = ".*(youtube|youtu.be).*"
            val compiledPattern = Pattern.compile(pattern)
            var matcher = compiledPattern.matcher(url)
            return matcher.find()
        }

        fun recyclerSlideIssueFix(recyclerview: RecyclerView) {
            val listener = object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    val action = e.action
                    return if (recyclerview.canScrollHorizontally(RecyclerView.FOCUS_FORWARD)) {
                        when (action) {
                            MotionEvent.ACTION_MOVE -> rv.parent
                                .requestDisallowInterceptTouchEvent(true)
                        }
                        false
                    } else if (recyclerview.canScrollHorizontally(RecyclerView.FOCUS_BACKWARD)) {
                        when (action) {
                            MotionEvent.ACTION_MOVE -> rv.parent
                                .requestDisallowInterceptTouchEvent(true)
                        }
                        false
                    } else {
                        when (action) {
                            MotionEvent.ACTION_MOVE -> rv.parent
                                .requestDisallowInterceptTouchEvent(false)
                        }
                        recyclerview.removeOnItemTouchListener(this)
                        true
                    }
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            }
            recyclerview.addOnItemTouchListener(listener)
        }
    }
}

