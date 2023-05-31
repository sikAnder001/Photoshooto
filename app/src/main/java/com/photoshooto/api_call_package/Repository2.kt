package com.photoshooto.api_call_package

import com.photoshooto.api_call_package.network.Network
import com.photoshooto.api_call_package.network.UtilTo
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
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response

class Repository2 {
    suspend fun getJob(limit: Int, order: Int?, sort: String): Response<JobResponse> {
        return withContext(Dispatchers.IO) {
            UtilTo.print(
                Network().getJob(
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "", limit, order, sort
                )
            )
        }
    }

    suspend fun getJobById(id: String?): Response<JobByIdResponse> {
        return withContext(Dispatchers.IO) {
            UtilTo.print(
                Network().getJobById(
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "", id
                )
            )
        }
    }



    suspend fun getProductId(id: String?): Response<ProductByIdResponse> {
        return withContext(Dispatchers.IO) {
            UtilTo.print(
                Network().getProductById(
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "", id
                )
            )
        }
    }


    suspend fun getReportedSpam(limit: Int, page: Int, order: Int?): Response<SpamReportResponse> {

        return withContext(Dispatchers.IO) {
            UtilTo.print(
                Network().getReportedSpam(
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "", limit, page, order
                )
            )
        }
    }

    suspend fun approveDeclineStatus(
        status: String,
        jobs: ApproveRejectBody
    ): Response<ApproveDeclineResponse>? {
        return withContext(Dispatchers.IO) {
            UtilTo.print(
                Network().approveDeclineStatus(
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "", status, jobs
                )
            )
        }
    }

    suspend fun saveSpamReports(
        productId: String,
        message: String,
    ): Response<SaveSpamReportResponse>? {
        return withContext(Dispatchers.IO) {
            UtilTo.print(
                Network().saveSpamReports(
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "", productId, message
                )
            )
        }
    }

    suspend fun removeJob(id: String): Response<ApproveDeclineResponse>? {
        return withContext(Dispatchers.IO) {
            UtilTo.print(
                Network().removeJob(
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "", id
                )
            )
        }
    }


    suspend fun getProductList(limit: Int, page: Int?,order: Int?,sort: String): Response<ProductListResponse> {
        return withContext(Dispatchers.IO) {
            UtilTo.print(
                Network().getProductList(
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "", limit, page,order,sort
                ))
        }
    }

    suspend fun updateStatus(
        status: String,
        products: UpdateProductStatusBody
    ): Response<ApproveDeclineResponse>? {
        return withContext(Dispatchers.IO) {
            UtilTo.print(
                Network().updateStatus(
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "", status, products
                )
            )
        }
    }


    suspend fun getReportedList(limit: Int, page: Int, Spam_type: String,order: Int?,sort: String): Response<ReportedListResponse> {
        return withContext(Dispatchers.IO) {
            UtilTo.print(
                Network().getReportedList(
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "", limit, page, Spam_type,order,sort
                )
            )
        }
    }

    suspend fun removeList(id: String): Response<ApproveDeclineResponse>? {
        return withContext(Dispatchers.IO) {
            UtilTo.print(
                Network().removeList(
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "", id
                )
            )
        }
    }

    suspend fun getJobsList(currentPage: Int, prefillFilterJSON: JSONObject?, sortByPrefill: String?, order: Int?): Response<UserJobListingDataRS> {
        return withContext(Dispatchers.IO) {
            UtilTo.print(
                Network().getJobList(
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "",currentPage,prefillFilterJSON,sortByPrefill,order
                )
            )
        }
    }

    suspend fun getAllUsers(limit: Int, page: Int, sort: String?,order: Int?,type: String?): Response<AllEmployeesResponse> {
        return withContext(Dispatchers.IO) {
            UtilTo.print(
                Network().getAllUsers(
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "", limit, page,sort,order,type
                )
            )
        }
    }

   /* suspend fun getEmployeesDetails(id: String): Response<ApproveDeclineResponse>? {
        return withContext(Dispatchers.IO) {
            UtilTo.print(
                Network().getEmployeesDetails(
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN) ?: "", id
                )
            )
        }
    }*/


}