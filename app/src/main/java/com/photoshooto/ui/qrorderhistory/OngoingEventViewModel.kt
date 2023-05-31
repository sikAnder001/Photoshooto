package com.photoshooto.ui.qrorderhistory

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.domain.model.ApiError
import com.photoshooto.domain.model.EventOrderHistoryModel
import com.photoshooto.domain.usecase.base.UseCaseResponse
import com.photoshooto.domain.usecase.qr_event_order.EventOrderHistoryDetailsUseCase
import com.photoshooto.domain.usecase.qr_event_order.QREventOrderHistoryViewModel
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import kotlinx.coroutines.cancel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class OngoingEventViewModel constructor(private val eventDetailsUseCase: EventOrderHistoryDetailsUseCase) :
    ViewModel() {
    val eventOrderHistoyDetails = MutableLiveData<EventOrderHistoryModel>()
    val showProgressbar = MutableLiveData<Boolean>()
    val messageData = MutableLiveData<String>()
    val formatter = SimpleDateFormat("yyyy-MM-dd")

    @RequiresApi(Build.VERSION_CODES.O)
    var currentDate: Date = formatter.parse(LocalDateTime.now().toString())

    fun getEventOrderHistory(limit: Int, page: Int, className: String) {
        showProgressbar.value = true
        eventDetailsUseCase.getEventOrderHistory(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN).toString(),
            limit,
            page,
            object : UseCaseResponse<EventOrderHistoryModel> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onSuccess(result: EventOrderHistoryModel) {
                    for (item in result.data.list!!) {
                        if (className == "OngoingEventFragment") {
                            val isOngoingDate =
                                verifyDateWiseStatus(item.event_start_date, item.event_end_date)
                            if (isOngoingDate) {
                                eventOrderHistoyDetails.value = result
                                showProgressbar.value = false
                            }
                        } else if (className == "UpcomingEventFragment") {

                            val isUpcomingStatus = upcomingDateStatus(item.event_start_date)
                            if (isUpcomingStatus) {
                                eventOrderHistoyDetails.value = result
                                showProgressbar.value = false
                            }
                        } else if (className == "CompletedEventFragment") {
                            val isCompletedStatus = CompletedDateStatus(item.event_end_date)
                            if (isCompletedStatus) {
                                eventOrderHistoyDetails.value = result
                                showProgressbar.value = false
                            }
                        } else {
                            Log.i(TAG, "result: $result")
                            eventOrderHistoyDetails.value = result
                            showProgressbar.value = false
                        }
                    }
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                }
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun CompletedDateStatus(eventEndDate: String): Boolean {
        return currentDate > formatter.parse(eventEndDate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun verifyDateWiseStatus(eventStartDate: String, eventEndDate: String): Boolean {

        var startDate: Date = formatter.parse(eventStartDate)
        var endDate: Date = formatter.parse(eventEndDate)

        return startDate.compareTo(currentDate) * currentDate.compareTo(endDate) > 0
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun upcomingDateStatus(eventStartDate: String): Boolean {
        return currentDate < formatter.parse(eventStartDate)
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        private val TAG = QREventOrderHistoryViewModel::class.java.name
    }

}