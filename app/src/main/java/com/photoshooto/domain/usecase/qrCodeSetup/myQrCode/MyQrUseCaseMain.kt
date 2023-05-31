package com.photoshooto.domain.usecase.qrCodeSetup.myQrCode

import com.photoshooto.domain.exception.traceErrorException
import com.photoshooto.domain.usecase.base.UseCaseResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

abstract class MyQrUseCaseMain<MyQrCodes>() where MyQrCodes : Any {

    abstract suspend fun getEventList(token: String?): MyQrCodes

    fun getEventList(
        scope: CoroutineScope,
        token: String?,
        onResult: UseCaseResponse<MyQrCodes>?
    ) {
        scope.launch {
            try {
                val result = getEventList(token)
                onResult?.onSuccess(result)
            } catch (e: CancellationException) {
                e.printStackTrace()
                onResult?.onError(traceErrorException(e))
            } catch (e: Exception) {
                e.printStackTrace()
                onResult?.onError(traceErrorException(e))
            }
        }
    }
}