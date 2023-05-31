package com.photoshooto.domain.usecase.signup

import com.photoshooto.domain.exception.traceErrorException
import com.photoshooto.domain.usecase.base.UseCaseResponse
import com.photoshooto.domain.usecase.verify_otp.SendOtpRequestModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

abstract class SendOtpMobileUseCaseMain<Type, in Params>() where Type : Any {

    abstract suspend fun sendOtpCall(params: SendOtpRequestModel? = null): Type

    fun sendOtpMobile(
        scope: CoroutineScope,
        params: SendOtpRequestModel?,
        onResult: UseCaseResponse<Type>?
    ) {
        scope.launch {
            try {
                val result = sendOtpCall(params)
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