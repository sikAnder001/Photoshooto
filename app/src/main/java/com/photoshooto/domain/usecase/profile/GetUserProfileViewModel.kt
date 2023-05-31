package com.photoshooto.domain.usecase.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.photoshooto.domain.AllStatusModel
import com.photoshooto.domain.model.*
import com.photoshooto.domain.usecase.base.UseCaseResponse
import com.photoshooto.domain.usecase.manage_address.ManageAddressUseCase
import com.photoshooto.domain.usecase.manage_admin.ManageUsersUseCase
import com.photoshooto.util.Resource
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import com.photoshooto.util.SingleLiveEvent
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class GetUserProfileViewModel constructor(
    private val getProfileByIDUseCase: GetProfileByIDUseCase,
    private val manageUsersUseCase: ManageUsersUseCase,
    private val manageAddressUseCase: ManageAddressUseCase,
    val deletePlanResponse: MutableLiveData<CommonResponse> = MutableLiveData<CommonResponse>()

) : ViewModel() {

    private val _getUserData = MutableLiveData<Resource<GetProfileModel>>()
    val getUserData: LiveData<Resource<GetProfileModel>> get() = _getUserData

    private val _getReferralCode = MutableLiveData<Resource<UserReferralCodeModal>>()
    val getReferralCode: LiveData<Resource<UserReferralCodeModal>> get() = _getReferralCode

    private val _updateProfileData = MutableLiveData<Resource<UserProfileModel>>()
    val updateProfileData: LiveData<Resource<UserProfileModel>> get() = _updateProfileData

    private val _getPlan = MutableLiveData<Resource<PhotographerPlanResponse>?>()
    val getPlan: LiveData<Resource<PhotographerPlanResponse>?> get() = _getPlan

    private val _getPlanById = MutableLiveData<Resource<PhotographerPlanResponse>?>()
    val getPlanById: LiveData<Resource<PhotographerPlanResponse>?> get() = _getPlanById

    private val _updatePlan = MutableLiveData<Resource<AllStatusModel>?>()
    val updatePlan: LiveData<Resource<AllStatusModel>?> get() = _updatePlan

    private val _savePlan = MutableLiveData<Resource<AllStatusModel>?>()
    val savePlan: LiveData<Resource<AllStatusModel>?> get() = _savePlan

    private val _deletePlan = MutableLiveData<Resource<CommonResponse>?>()
    val deletePlan: LiveData<Resource<CommonResponse>?> get() = _deletePlan

    private val _getReview = MutableLiveData<Resource<ReviewResponse>?>()
    val getReview: LiveData<Resource<ReviewResponse>?> get() = _getReview

    private val _getReviewByUserId = MutableLiveData<Resource<ReviewByUserIdResponse>?>()
    val getReviewByUserId: LiveData<Resource<ReviewByUserIdResponse>?> get() = _getReviewByUserId

    private val _getWorkDemo = MutableLiveData<Resource<WorkDemoResponse>?>()
    val getWorkDemo: LiveData<Resource<WorkDemoResponse>?> get() = _getWorkDemo

    private val _sendFeedback = MutableLiveData<Resource<AllStatusModel>?>()
    val sendFeedback: LiveData<Resource<AllStatusModel>?> get() = _sendFeedback

    val getCityResponse = MutableLiveData<GetCityResponse>()

    val getStatesResponse = MutableLiveData<GetStatesResponse>()

    val photographerServiceResponse = MutableLiveData<PhotographerServiceModel>()

    val userReferredResponse = MutableLiveData<UserReferredModel>()

    val userResponse = MutableLiveData<GetUsersResponse>()

    val addEnquiryResponse = SingleLiveEvent<CommonResponse>()

    val getEnquiryResponse = SingleLiveEvent<GetEnquiryResponseModel>()

    val showProgressbar = MutableLiveData<Boolean>()

    fun getProfileByIDUseCase(id: String?, token: String?) {
        showProgressbar.value = true
        viewModelScope.launch {
            getProfileByIDUseCase.getUserProfileByID(id, token).let {
                showProgressbar.value = false
                if (it.isSuccessful) {
                    _getUserData.postValue(Resource.success(it.body()))
                } else {
                    _getUserData.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }

    fun getReferralCode(id: String?, token: String?) {
        showProgressbar.value = true
        viewModelScope.launch {
            getProfileByIDUseCase.getReferral(id, token).let {
                showProgressbar.value = false
                if (it.isSuccessful) {
                    _getReferralCode.postValue(Resource.success(it.body()))
                } else {
                    _getReferralCode.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }

    fun getPhotographerUsers() {
        showProgressbar.value = true
        manageUsersUseCase.getPhotographerUsers(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            "photographer",
            object : UseCaseResponse<GetUsersResponse> {
                override fun onSuccess(result: GetUsersResponse) {
                    Log.i(TAG, "result: $result")
                    userResponse.value = result
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    Log.i(TAG, "result: $apiError")
                    userResponse.value = GetUsersResponse(success = false, message = "")
                    showProgressbar.value = false
                    println("apiError $apiError")
                }
            }
        )
    }

    fun getPhotographerServices() {
        showProgressbar.value = true
        viewModelScope.launch {
            getProfileByIDUseCase.getPhotographerServices(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: ""
            ).let {
                showProgressbar.value = false
                photographerServiceResponse.postValue(it)
            }
        }
    }

    fun getReferredUsers(referralCode: String) {
        showProgressbar.value = true
        viewModelScope.launch {
            getProfileByIDUseCase.getReferredUsers(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
                referralCode
            ).let {
                showProgressbar.value = false
                userReferredResponse.postValue(it)
            }
        }
    }

    fun getCityList() {
        showProgressbar.value = true
        manageUsersUseCase.getCityList(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            object : UseCaseResponse<GetCityResponse> {
                override fun onSuccess(result: GetCityResponse) {
                    Log.i(TAG, "result: $result")
                    getCityResponse.postValue(result)
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    showProgressbar.value = false
                    getCityResponse.postValue(GetCityResponse(success = false, message = ""))
                    Log.i(TAG, "API Error: ${apiError?.message}")
                }
            }
        )
    }

    fun getStates() {
        showProgressbar.value = true
        manageAddressUseCase.getStates(
            viewModelScope,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",
            object : UseCaseResponse<GetStatesResponse> {
                override fun onSuccess(result: GetStatesResponse) {
                    Log.i(TAG, "result: $result")
                    getStatesResponse.postValue(result)
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    getStatesResponse.postValue(GetStatesResponse(success = false, message = ""))
                    showProgressbar.value = false
                    println("apiError " + apiError)
                }
            }
        )
    }

    fun updateProfileUseCase(body: UpdateProfileModel?, token: String?) {
        Log.i("profileReq", "${Gson().toJson(body)}\nToken : $token")
        showProgressbar.value = true
        viewModelScope.launch {
            getProfileByIDUseCase.updateUserProfile(body, token).let {
                showProgressbar.value = false
                if (it.isSuccessful) {
                    _updateProfileData.postValue(Resource.success(it.body()))
                } else {
                    _updateProfileData.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }

    fun getPlan(token: String?, limit: Int, page: Int) {
        showProgressbar.value = true
        viewModelScope.launch {
            getProfileByIDUseCase.getPlan(token, limit, page).let {
                showProgressbar.value = false
                if (it.isSuccessful) {
                    _getPlan.postValue(Resource.success(it.body()))
                } else {
                    _getPlan.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }

    fun getPlanByUserId(userId: String?, token: String?, limit: Int, page: Int) {
        showProgressbar.value = true
        viewModelScope.launch {
            getProfileByIDUseCase.getPlanByUserId(userId, token, limit, page).let {
                showProgressbar.value = false
                if (it.isSuccessful) {
                    _getPlanById.postValue(Resource.success(it.body()))
                } else {
                    _getPlanById.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }

    fun deletePlan(id: String, token: String?) {
        showProgressbar.value = true
        viewModelScope.launch {
            getProfileByIDUseCase.deletePlan(id, token).let {
                showProgressbar.value = false
                if (it.success == true) {
                    _deletePlan.postValue(Resource.success(it))
                } else {
                    _deletePlan.postValue(Resource.success(it))
                }
            }
        }
    }

    fun resetGetPlan() {
        _getPlan.value = null
    }

    fun savePlan(body: PhotographerPlanBody?, token: String?) {
        showProgressbar.value = true
        viewModelScope.launch {
            getProfileByIDUseCase.savePlan(token, body).let {
                showProgressbar.value = false
                if (it.isSuccessful) {
                    _savePlan.postValue(Resource.success(it.body()))
                } else {
                    _savePlan.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }

    fun updatePlan(id: String?, body: PhotographerPlanBody?, token: String?) {
        showProgressbar.value = true
        viewModelScope.launch {
            getProfileByIDUseCase.updatePlan(id, token, body).let {
                showProgressbar.value = false
                if (it.isSuccessful) {
                    _updatePlan.postValue(Resource.success(it.body()))
                } else {
                    _updatePlan.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }

    fun resetUpdatePlan() {
        _updatePlan.value = null
    }

    fun getReview(token: String?, limit: Int, page: Int) {
        showProgressbar.value = true
        viewModelScope.launch {
            getProfileByIDUseCase.getReview(token, limit, page).let {
                showProgressbar.value = false
                if (it.isSuccessful) {
                    _getReview.postValue(Resource.success(it.body()))
                } else {
                    _getReview.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }

    fun getReviewByUserId(token: String?, userId: String?, limit: Int, page: Int) {
        showProgressbar.value = true
        viewModelScope.launch {
            getProfileByIDUseCase.getReviewByUserId(token, userId, limit, page).let {
                showProgressbar.value = false
                if (it.isSuccessful) {
                    _getReviewByUserId.postValue(Resource.success(it.body()))
                } else {
                    _getReviewByUserId.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }

    fun resetReview() {
        _getReview.value = null
    }

    fun addEnquiryAPI(body: AddEnquiryReqModel) {
        showProgressbar.value = true
        viewModelScope.launch {
            getProfileByIDUseCase.addEnquiry(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                body
            ).let {
                showProgressbar.value = false
                addEnquiryResponse.postValue(it)
            }
        }
    }

    fun getEnquiryAPI() {
        showProgressbar.value = true
        viewModelScope.launch {
            getProfileByIDUseCase.getEnquiry(
            ).let {
                showProgressbar.value = false
                getEnquiryResponse.postValue(it)
            }
        }
    }

    fun getWorkDemo(token: String?) {
        showProgressbar.value = true
        viewModelScope.launch {
            getProfileByIDUseCase.getWorkDemoResponse(token).let {
                showProgressbar.value = false
                if (it.isSuccessful) {
                    _getWorkDemo.postValue(Resource.success(it.body()))
                } else {
                    _getWorkDemo.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }

    fun resetWorkDemo() {
        _getReview.value = null
    }

    fun sendFeedback(token: String?, sendFeedbackRequest: SendFeedbackRequest) {
        showProgressbar.value = true
        viewModelScope.launch {
            getProfileByIDUseCase.sendFeedback(token, sendFeedbackRequest).let {
                showProgressbar.value = false
                if (it.isSuccessful) {
                    _sendFeedback.postValue(Resource.success(it.body()))
                } else {
                    _sendFeedback.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }

    fun resetSendFeedback() {
        _sendFeedback.value = null
    }


    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        private val TAG = GetUserProfileViewModel::class.java.name
    }

}