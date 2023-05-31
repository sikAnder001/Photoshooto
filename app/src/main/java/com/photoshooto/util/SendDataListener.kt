package com.photoshooto.util

interface SendDataListener {
    fun sendData(mediaString: String?)
    fun sendData(checkedId: Int)
}