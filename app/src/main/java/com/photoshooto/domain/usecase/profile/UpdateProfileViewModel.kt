//package com.photoshooto.domain.usecase.profile
//
//import android.util.Log
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.photoshooto.domain.model.ApiError
//import com.photoshooto.domain.model.UpdateProfileModel
//import com.photoshooto.domain.model.UserProfileModel
//import com.photoshooto.domain.usecase.base.UseCaseResponse
//import kotlinx.coroutines.cancel
//import okhttp3.RequestBody
//
//
//class UpdateProfileViewModel constructor(private val updateProfileUseCase: UpdateProfileUseCase) :
//    ViewModel() {
//
//    val updateProfileData = MutableLiveData<UserProfileModel>()
//    val showProgressbar = MutableLiveData<Boolean>()
//    val messageData = MutableLiveData<String>()
//
////    val _profileUpdated = MutableLiveData<Boolean>()
////    var profileUpdated = _profileUpdated.value
////    fun updateChangeListen(value: Boolean) {
////        _profileUpdated.value = value
////    }
//
//    fun updateProfileUseCase(body: UpdateProfileModel?, token: String?) {
//        showProgressbar.value = true
//        updateProfileUseCase.updateUserProfile(
//            viewModelScope, body, token,
//            object : UseCaseResponse<UserProfileModel> {
//                override fun onSuccess(result: UserProfileModel) {
//                    Log.i(TAG, "result: $result")
//                    updateProfileData.value = result
//                    showProgressbar.value = false
//                }
//
//                override fun onError(apiError: ApiError?) {
//                    messageData.value = apiError?.getErrorMessage()
//                    showProgressbar.value = false
//                    println("praveen apiError " + apiError)
//                }
//            },
//        )
//    }
//
//    override fun onCleared() {
//        viewModelScope.cancel()
//        super.onCleared()
//    }
//
//    companion object {
//        private val TAG = UpdateProfileViewModel::class.java.name
//    }
//
//}