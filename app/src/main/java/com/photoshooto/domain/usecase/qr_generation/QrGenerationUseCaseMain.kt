package com.photoshooto.domain.usecase.qr_generation

import com.photoshooto.domain.exception.traceErrorException
import com.photoshooto.domain.usecase.base.UseCaseResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

abstract class QrGenerationUseCaseMain<Type, GenerateQr>() where Type : Any, GenerateQr : Any {

    abstract suspend fun getStandee(token: String): Type
    abstract suspend fun generateQrCode(token: String): GenerateQr
    fun getStandee(
        scope: CoroutineScope,
        token: String,
        onResult: UseCaseResponse<Type>?
    ) {
        scope.launch {
            try {
                val result = getStandee(token)
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

    fun generateQrCode(
        scope: CoroutineScope,
        token: String,
        onResult: UseCaseResponse<GenerateQr>?
    ) {
        scope.launch {
            try {
                val result = generateQrCode(token)
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
