package com.photoshooto.domain.usecase.reset_pwd

import com.photoshooto.domain.exception.traceErrorException
import com.photoshooto.domain.usecase.base.UseCaseResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

abstract class ResetPwdUseCaseMain<Type, in Params>() where Type : Any {

    abstract suspend fun resetpassword(
        body: PwdChangeRequestModel? = null,
        token: String? = null
    ): Type

    fun resetpassword(
        scope: CoroutineScope,
        body: PwdChangeRequestModel? = null, token: String?,
        onResult: UseCaseResponse<Type>?
    ) {
        scope.launch {
            try {
                val result = resetpassword(body, token)
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