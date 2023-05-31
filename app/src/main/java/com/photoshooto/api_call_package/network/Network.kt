package com.photoshooto.api_call_package.network

import com.photoshooto.api_call_package.view_model.ApproveDeclineResponse
import com.photoshooto.domain.model.ApproveRejectBody
import com.photoshooto.domain.model.UpdateProductStatusBody
import com.photoshooto.domain.model.UserJobListingDataRS
import com.photoshooto.domain.model.all_employees_model.AllEmployeesResponse
import com.photoshooto.domain.model.get_productlist_model.ProductListResponse
import com.photoshooto.domain.model.job_by_id.JobByIdResponse
import com.photoshooto.domain.model.jobmodel.JobResponse
import com.photoshooto.domain.model.product_by_id.ProductByIdResponse
import com.photoshooto.domain.model.reportedListModel.ReportedListResponse
import com.photoshooto.domain.model.save_report_model.SaveSpamReportResponse
import com.photoshooto.domain.model.spam_model.SpamReportResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface Network {


    companion object {
        val create = null
        operator fun invoke(): Network {
            return if (create == null) {
                var loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                var okHttpClient = OkHttpClient.Builder()
                    .readTimeout(5, TimeUnit.MINUTES)
                    .writeTimeout(5, TimeUnit.MINUTES)
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .addInterceptor(loggingInterceptor)
                    .build()



                Retrofit.Builder()
                    .baseUrl(API.BASE_URL2)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(Network::class.java)
            } else create!!
        }
    }


    @GET(API.JOBS)
    suspend fun getJob(
        @Header(Constants.AUTHORIZATION) token: String,
        @Query("limit") limit: Int,
        @Query("order") order: Int?,
        @Query("sort_by") sort: String
    ): Response<JobResponse>

    @GET(API.JOB_BY_ID + "/{id}")
    suspend fun getJobById(
        @Header(Constants.AUTHORIZATION) token: String,
        @Path("id") id: String?
    ): Response<JobByIdResponse>

    @GET(API.PRODUCT_BY_ID + "/{id}")
    suspend fun getProductById(
        @Header(Constants.AUTHORIZATION) token: String,
        @Path("id") id: String?
    ): Response<ProductByIdResponse>

    @GET(API.SPAM_REPORT)
    suspend fun getReportedSpam(
        @Header(Constants.AUTHORIZATION) token: String,
        @Query("limit") limit: Int,
        @Query("page") page: Int,
        @Query("order") order: Int?
    ): Response<SpamReportResponse>

    @PUT(API.JOB_STATUS + "/{status}")
    suspend fun approveDeclineStatus(
        @Header(Constants.AUTHORIZATION) token: String,
        @Path("status") limit: String,
        @Body jobs: ApproveRejectBody,
    ): Response<ApproveDeclineResponse>

    @DELETE(API.DELETE_JOB + "/{id}")
    suspend fun removeJob(
        @Header(Constants.AUTHORIZATION) token: String,
        @Path("id") id: String
    ): Response<ApproveDeclineResponse>


    @GET(API.PRODUCT_LIST)
    suspend fun getProductList(
        @Header(Constants.AUTHORIZATION) token: String,
        @Query("limit") limit: Int,
        @Query("page") page: Int?,
        @Query("order") order: Int?,
        @Query("sort_by") sort: String



      /*  @Query("sort_by") sort: String?,
        @Query("status") status: String?,*/

     /*   @Query("FILTER_KEY") min_price: String*/
    ): Response<ProductListResponse>

    @PUT(API.UPDATE_STATUS + "/{status}")
    suspend fun updateStatus(
        @Header(Constants.AUTHORIZATION) token: String,
        @Path("status") status: String,
        @Body products: UpdateProductStatusBody,
    ): Response<ApproveDeclineResponse>

    @GET(API.REPORTED_LIST)
    suspend fun getReportedList(
        @Header(Constants.AUTHORIZATION) token: String,
        @Query("limit") limit: Int,
        @Query("page") page: Int,
        @Query("spam_type") spam_type: String,
        @Query("order") order: Int?,
        @Query("sort_by") sort: String
    ): Response<ReportedListResponse>

    @DELETE(API.REMOVE_LIST + "/{id}")
    suspend fun removeList(
        @Header(Constants.AUTHORIZATION) token: String,
        @Path("id") id: String
    ): Response<ApproveDeclineResponse>

    @GET(API.JOB_LIST)
    suspend fun getJobList(
        @Header(Constants.AUTHORIZATION) token: String,
        @Query("page") currentPage: Int,
        @Body prefillFilterJSON: JSONObject?,
        @Query("sort_by") sortByPrefill: String?,
        @Query("order") order: Int?,
    ): Response<UserJobListingDataRS>


    @FormUrlEncoded
    @POST(API.SAVE_SPAM_REPORTS)
    suspend fun saveSpamReports(
        @Header(Constants.AUTHORIZATION) token: String,
        @Field("product_id") productId: String,
        @Field("message") message: String
    ): Response<SaveSpamReportResponse>

    @GET(API.MANAGE_EMPLOYEES_TYPE )
    suspend fun getAllUsers(
        @Header(Constants.AUTHORIZATION) token: String,
        @Query("limit") limit: Int,
        @Query("page") page: Int,
        @Query("sort-by") sort: String?,
        @Query("order") order: Int?,
        @Query("type") type: String?,
    ): Response<AllEmployeesResponse>

   /* @GET(API.EMPLOYEES_DETAILS )
    suspend fun getEmployeesDetails(
        @Header(Constants.AUTHORIZATION) token: String,
        @Query("id") id: String,
    ): Response<SpamReportResponse>*/


/*    @FormUrlEncoded
    @POST(API.SIGNUP_WITH_EMAIL)
    suspend fun signUpWithEmail(
        @Header(Constants.AUTHORIZATION) token: String,
        @Field("email") email: String,
        @Field("device_type") deviceType: Int
    ): Response<SignUpWithEmailResponse>

    @GET(API.SNC_PROGRAM)
    suspend fun getSNCPrograms(
        @Header(Constants.AUTHORIZATION) token: String,
        @Query("program_id") date: String
    ): Response<SNCProgramModel>*/

}