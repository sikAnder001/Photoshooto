package com.photoshooto.domain.usecase.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.domain.AllStatusModel
import com.photoshooto.domain.model.RegisterVerifyOtpModel
import com.photoshooto.domain.model.UserProfileModel
import com.photoshooto.domain.model.VerifyMovileModel
import com.photoshooto.domain.usecase.verify_otp.SendOtpRequestModel
import com.photoshooto.util.Resource
import kotlinx.coroutines.launch
import org.json.JSONObject

class UserLoginViewModel constructor(
    private val userLoginUseCase: UserLoginUseCase
) : ViewModel() {

    var requestRegister = RegisterVerifyOtpModel()

    private val _loginResponse = MutableLiveData<Resource<UserProfileModel>>()
    val loginResponse: LiveData<Resource<UserProfileModel>> get() = _loginResponse

/*    private val _sendMobileOtp = MutableLiveData<Resource<VerifyMovileModel>>()
    val sendMobileOtp: LiveData<Resource<VerifyMovileModel>> get() = _sendMobileOtp*/

    private val _sendMobileOtp = MutableLiveData<Resource<VerifyMovileModel>>()
    val sendMobileOtp: LiveData<Resource<VerifyMovileModel>> get() = _sendMobileOtp

    private val _reSendMobileOtp = MutableLiveData<Resource<VerifyMovileModel>>()
    val reSendMobileOtp: LiveData<Resource<VerifyMovileModel>> get() = _reSendMobileOtp

    /* private val _registerPhotographer = MutableLiveData<Resource<VerifyMovileModel>>()
     val registerPhotographer: LiveData<Resource<VerifyMovileModel>> get() = _registerPhotographer*/

    private val _registerPhotographer = MutableLiveData<Resource<VerifyMovileModel>>()
    val registerPhotographer: LiveData<Resource<VerifyMovileModel>> get() = _registerPhotographer

    val showProgressbar = MutableLiveData<Boolean>()
    val messageData = MutableLiveData<String>()

//    val userLogoutStatus = MutableLiveData<AllStatusModel>()

    private val _userLogoutStatus = MutableLiveData<Resource<AllStatusModel>>()
    val userLogoutStatus: LiveData<Resource<AllStatusModel>> get() = _userLogoutStatus

    fun userLogin(partMap: UserLoginRequest) {
        showProgressbar.value = true
        _loginResponse.postValue(Resource.loading())
        viewModelScope.launch {
            userLoginUseCase.userLogin(partMap).let {
                if (it.isSuccessful) {
                    _loginResponse.postValue(Resource.success(it.body()))
                } else {
                    val jsonObj = it.errorBody()?.charStream()?.readText()
                        ?.let { it1 -> JSONObject(it1) }
                    val message = jsonObj?.getString("message")
                    _loginResponse.postValue(message?.let { it1 -> Resource.error(it1, null) })
//                    _loginResponse.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }

    fun logout(token: String?) {
        showProgressbar.value = true
        _loginResponse.postValue(Resource.loading())
        viewModelScope.launch {
            userLoginUseCase.logout(token).let {
                if (it.isSuccessful) {
                    Log.e("logout response", "" + it.body().toString())
                    _userLogoutStatus.postValue(Resource.success(it.body()))
                } else {
                    /*val jsonObj = JSONObject(it.errorBody()!!.charStream().readText())
                    val message = jsonObj.getString("message")*/
                    _userLogoutStatus.postValue(Resource.error("Something went wrong", null))
                }
            }
        }
    }

    fun registerPhotographerCall() {
        //showProgressbar.value = true
        _registerPhotographer.postValue(Resource.loading())
        viewModelScope.launch {
            userLoginUseCase.signupVerifyOtpCall(requestRegister).let {
                if (it.isSuccessful) {
                    _registerPhotographer.postValue(Resource.success(it.body()))
                } else {
                    _registerPhotographer.postValue(Resource.error("Something went wrong", null))
                }
            }
        }
    }

    fun signupVerifyOtpCall(verifyOtp: RegisterVerifyOtpModel) {
        showProgressbar.value = true
        _sendMobileOtp.postValue(Resource.loading())
        viewModelScope.launch {
            userLoginUseCase.signupVerifyOtpCall(verifyOtp).let {
                if (it.isSuccessful) {
                    _sendMobileOtp.postValue(Resource.success(it.body()))
                } else {
                    val jsonObj = JSONObject(it.errorBody()!!.charStream().readText())
                    val message = jsonObj.getString("message")
                    _sendMobileOtp.postValue(Resource.error(message, null))
                }
            }
        }
    }

    fun getMobileCheck(sendOtpModel: SendOtpRequestModel) {
        showProgressbar.value = true
        _sendMobileOtp.postValue(Resource.loading())
        viewModelScope.launch {
            userLoginUseCase.sendOtpMobile(sendOtpModel).let {
                showProgressbar.value = false
                if (it.isSuccessful) {
                    _sendMobileOtp.postValue(Resource.success(it.body()))
                } else {
                    val jsonObj = JSONObject(it.errorBody()!!.charStream().readText())
                    val message = jsonObj.getString("message")
                    _sendMobileOtp.postValue(Resource.error(message, null))
                }
            }
        }
    }

    fun resendOTPMobileCheck(sendOtpModel: SendOtpRequestModel) {
        showProgressbar.value = true
        _reSendMobileOtp.postValue(Resource.loading())
        viewModelScope.launch {
            userLoginUseCase.sendOtpMobile(sendOtpModel).let {
                if (it.isSuccessful) {
                    _reSendMobileOtp.postValue(Resource.success(it.body()))
                } else {
                    /*val jsonObj = JSONObject(it.errorBody()!!.charStream().readText())
                    val message = jsonObj.getString("message")*/
                    _reSendMobileOtp.postValue(Resource.error("Something went wrong", null))
                }
            }
        }
    }


//    fun getMobileCheck(sendOtpModel: SendOtpRequestModel) {
//
//        showProgressbar.value = true
//        verifyMobileUseCase.sendOtpMobile(
//
//            viewModelScope, sendOtpModel,
//            object : UseCaseResponse<VerifyMovileModel> {
//                override fun onSuccess(result: VerifyMovileModel) {
//                    Log.i(TAG, "result: $result")
//                    with(PreferenceManager) {
//                        saveVerification_key = result.data?.verification_key
//                        saveOtpKey = result.data?.otp
//                        //Log.i(TAG, "server Key "+saveVerification_key.toString())
//                        Log.i(TAG, "serveotp "+ saveOtpKey.toString())
//
//                    }
//                    sendMobileOtp.value = result
//                    showProgressbar.value = false
//                }
//
//                override fun onError(apiError: ApiError?) {
//                    Log.i(TAG, "result: "+apiError?.getErrorMessage())
//                    messageData.value = apiError?.getErrorMessage()
//                    showProgressbar.value = false
//                }
//            },
//        )
//    }


    companion object {
        private val TAG = UserLoginViewModel::class.java.name
    }

}