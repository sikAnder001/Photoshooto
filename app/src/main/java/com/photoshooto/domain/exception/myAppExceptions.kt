package com.photoshooto.domain.exception

sealed class MyException(t: Throwable) : Exception() {
    class InternetConnectionException(e: Exception) : MyException(e)
    class JsonException(e: Exception) : MyException(e)
    class NetworkCallCancelException(e: Exception) : MyException(e)
    class ServerNotAvailableException(e: Exception) : MyException(e)
    class DatabaseException(e: Exception) : MyException(e)
    class UnknownException(e: Exception) : MyException(e)
}