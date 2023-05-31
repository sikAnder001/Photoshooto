package com.photoshooto.domain.usecase.profile

import com.photoshooto.domain.model.AddEnquiryReqModel
import com.photoshooto.domain.model.PhotographerPlanBody
import com.photoshooto.domain.model.SendFeedbackRequest
import com.photoshooto.domain.model.UpdateProfileModel
import com.photoshooto.domain.repository.PostsRepository
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper

class GetProfileByIDUseCase constructor(
    private val postsRepository: PostsRepository
) {

    suspend fun getUserProfileByID(id: String?, token: String?) =
        postsRepository.getUserProfileByID(id, token)

    suspend fun getReferral(id: String?, token: String?) =
        postsRepository.getReferral(id, token)

    suspend fun getPhotographerServices(token: String?) =
        postsRepository.getPhotographersServices(token)

    suspend fun getReferredUsers(token: String?, referralCode: String?) =
        postsRepository.getReferredUsers(token, referralCode)

    suspend fun updateUserProfile(body: UpdateProfileModel?, token: String?) =
        postsRepository.updateUserProfile(body, token)

    suspend fun getPlan(token: String?, limit: Int, page: Int) =
        postsRepository.getPlan(token, limit, page)

    suspend fun getPlanByUserId(userId: String?, token: String?, limit: Int, page: Int) =
        postsRepository.getPlanByUserId(userId, token, limit, page)

    suspend fun updatePlan(id: String?, token: String?, body: PhotographerPlanBody?) =
        postsRepository.updatePlan(id, token, body)

    suspend fun savePlan(token: String?, body: PhotographerPlanBody?) =
        postsRepository.savePlan(token, body)

    suspend fun deletePlan(id: String, token: String?) =
        postsRepository.deletePlan(id, token)

    suspend fun getReview(token: String?, limit: Int, page: Int) =
        postsRepository.getReview(token, limit, page)

    suspend fun getReviewByUserId(token: String?, userId: String?, limit: Int, page: Int) =
        postsRepository.getReviewByUserId(token, userId, limit, page)

    suspend fun getWorkDemoResponse(token: String?) =
        postsRepository.getWorkDemo(token)

    suspend fun addEnquiry(token: String?, body: AddEnquiryReqModel) =
        postsRepository.addEnquiry(token, body)

    suspend fun getEnquiry() =
        postsRepository.getEnquiries(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))

    suspend fun sendFeedback(token: String?, sendFeedbackRequest: SendFeedbackRequest) =
        postsRepository.sendFeedback(token, sendFeedbackRequest)

}
