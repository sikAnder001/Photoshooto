package com.photoshooto.data

import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import com.photoshooto.base.Either
import com.photoshooto.domain.exception.MyException
import com.photoshooto.domain.model.BaseResponse
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

open class BaseRepository {

    suspend fun <R> either(service: suspend () -> R): Either<MyException, R> {

        return try {
            val response = service.invoke()
            if (response is BaseResponse) {
                if (!response.success) {
                    //Logger.d("Api Unauthorized >>>>>")
                    /*if (MyApplication.context != null) {
                        Prefs.putString(PrefKeys.AuthKey, "")
                        Prefs.putString(PrefKeys.UserProfile, "")
                        val i = Intent(MyApplication.context, LoginActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        MyApplication.context!!.startActivity(i)
                    }*/
                }
            }
            Either.Right(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Either.Left(transformException(e))
        }
    }


    private fun transformException(e: Exception): MyException {
        if (e is HttpException) {
            when (e.code()) {
                422,
                502 -> return MyException.JsonException(e)
                500 -> return MyException.InternetConnectionException(e)
                404 -> return MyException.ServerNotAvailableException(e)
                else -> return MyException.UnknownException(e)
            }
        } else {
            if (e is CancellationException) {
                return MyException.NetworkCallCancelException(e)
            } else if (e is ConnectException || e is UnknownHostException || e is SocketTimeoutException || e is SocketException) {
                return MyException.InternetConnectionException(e)
            } else if (e is IllegalStateException || e is JsonSyntaxException || e is MalformedJsonException) {
                return MyException.JsonException(e)
            }
        }
        return MyException.UnknownException(e)
    }
}