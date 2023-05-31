package com.photoshooto.util

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {

        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, msg)
        }

        fun <T> loading(): Resource<T> {
            return Resource(Status.LOADING, null, null)
        }

    }

}

//sealed class Resource<T>(
//    val data: T?,
//    val message: String? = null
//) {
//    class Success<T>(data: T, message: String? = null) : Resource<T>(data, message)
//    class Loading<T>(data: T? = null) : Resource<T>(data)
//    class Error<T>(message: String?, data: T? = null) : Resource<T>(data, message)
//}
