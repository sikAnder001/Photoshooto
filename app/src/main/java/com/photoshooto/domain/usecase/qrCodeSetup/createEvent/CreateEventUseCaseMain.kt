package com.photoshooto.domain.usecase.qrCodeSetup.createEvent

import com.photoshooto.domain.exception.traceErrorException
import com.photoshooto.domain.usecase.base.UseCaseResponse
import com.photoshooto.domain.usecase.qrCodeSetup.createEvent.requestModel.CreateEventRequest
import com.photoshooto.domain.usecase.qrCodeSetup.createEvent.requestModel.CreateFolderRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

abstract class CreateEventUseCaseMain<Type, Standee, QRCode, EventTypes, CreateFolder, CreateEvent>() where Type : Any, Standee : Any, QRCode : Any, EventTypes : Any, CreateFolder : Any, CreateEvent : Any {

    abstract suspend fun getFolderList(token: String?): Type
    abstract suspend fun getStandeeList(token: String?): Standee
    abstract suspend fun getQRCodeList(token: String?): QRCode
    abstract suspend fun getEventTypesList(token: String?): EventTypes
    abstract suspend fun callCreateFolder(
        token: String?,
        createFolderRequest: CreateFolderRequest
    ): CreateFolder

    abstract suspend fun callCreateEvent(
        token: String?,
        createEventRequest: CreateEventRequest
    ): CreateEvent

    fun getFolderList(
        scope: CoroutineScope,
        token: String?,
        onResult: UseCaseResponse<Type>?
    ) {
        scope.launch {
            try {
                val result = getFolderList(token)
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

    fun getStandeeList(
        scope: CoroutineScope,
        token: String?,
        onResult: UseCaseResponse<Standee>?
    ) {
        scope.launch {
            try {
                val result = getStandeeList(token)
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

    fun getEventTypesList(
        scope: CoroutineScope,
        token: String?,
        onResult: UseCaseResponse<EventTypes>?
    ) {
        scope.launch {
            try {
                val result = getEventTypesList(token)
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

    fun getQRCodeList(
        scope: CoroutineScope,
        token: String?,
        onResult: UseCaseResponse<QRCode>?
    ) {
        scope.launch {
            try {
                val result = getQRCodeList(token)
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

    fun callCreateFolder(
        scope: CoroutineScope,
        token: String?,
        createFolderRequest: CreateFolderRequest,
        onResult: UseCaseResponse<CreateFolder>?
    ) {
        scope.launch {
            try {
                val result = callCreateFolder(token, createFolderRequest)
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

    fun callCreateEvent(
        scope: CoroutineScope,
        token: String?,
        createEventRequest: CreateEventRequest,
        onResult: UseCaseResponse<CreateEvent>?
    ) {
        scope.launch {
            try {
                val result = callCreateEvent(token, createEventRequest)
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