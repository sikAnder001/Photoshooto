package com.photoshooto.ui.admin_screen.manage_job_posting

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.R
import com.photoshooto.databinding.JobListItemBinding
import com.photoshooto.domain.model.spam_model.List
import com.photoshooto.util.urlAddingForPicture
import java.time.Instant
import java.time.OffsetDateTime
import java.util.*

class ReportedJobAdapter(
    var context: Context?,
    val selectedDeleteInterface: SelectedDeleteInterface,
    val viewDetailsInterface: ViewDetailsInterface,
    var removeInterface: RemoveInterface,

    ) : RecyclerView.Adapter<ReportedJobAdapter.ViewHolder>() {

    var data = ArrayList<List>()

    inner class ViewHolder(jobListItemBinding: JobListItemBinding) :
        RecyclerView.ViewHolder(jobListItemBinding.root) {

        var jobListItemBinding = jobListItemBinding

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(data: List, position: Int) {
            with(jobListItemBinding) {
                btnRemove.visibility = View.VISIBLE
                reportCount.visibility = View.VISIBLE
                layoutBtnAppDec.visibility = View.GONE

                Glide.with(context!!)
                    .load(urlAddingForPicture(data.userProfile!!.profileImage.toString()))
                    .error(R.drawable.ic_profile_placeholder).into(imageView)


                if (data.jobDetails?.created_at != null) {
                    timeAgoTv.visibility = View.VISIBLE
                    var odt: OffsetDateTime = OffsetDateTime.parse(data.jobDetails?.created_at)
                    var instant: Instant = odt.toInstant()

                    var dateO: Date = Date.from(instant)
                    var dateC = Date()
                    var difference_In_Time = dateC.time - dateO.time

                    Log.v("difference", (dateC.time - dateO.time).toString())

                    var difference_In_Minutes: Long = ((difference_In_Time / (1000 * 60)) % 60)

                    var difference_In_Hours: Long = ((difference_In_Time / (1000 * 60 * 60)) % 24)

                    var difference_In_Years: Long =
                        (difference_In_Time / (1000L * 60 * 60 * 24 * 365))

                    var difference_In_Days: Long =
                        ((difference_In_Time / (1000 * 60 * 60 * 24)) % 365)

                    Log.v(
                        "differencedays",
                        "days" + difference_In_Days + ",minutes" + difference_In_Minutes + ",hours" + difference_In_Hours + ",years" + difference_In_Years + ","
                    )

                    if (difference_In_Years >= 1) timeAgoTv.text =
                        "${difference_In_Years}years ago"
                    else if (difference_In_Days >= 1) timeAgoTv.text =
                        "${difference_In_Days}days ago"
                    else if (difference_In_Hours >= 2) timeAgoTv.text =
                        "${difference_In_Hours}hs ago"
                    else if (difference_In_Hours >= 1) timeAgoTv.text =
                        "${difference_In_Hours}h ago"
                    else if (difference_In_Minutes >= 1) timeAgoTv.text =
                        "${difference_In_Minutes}min ago"
                    else timeAgoTv.text = "Now"

                } else {
                    timeAgoTv.visibility = View.INVISIBLE
                }

                titleTv.text = data.userProfile?.studioName
                idTv.text = data.jobId
                locaionTv.text = data.userProfile!!.address
                equipmentTv.text = data.userProfile!!.equipmentUse
                photographyTypeTv.text = data.jobDetails?.photography_type

                shareTv.setOnClickListener {
                    try {
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
                        var shareMessage = ""
                        shareMessage =
                            """${shareMessage + "www.photoshooto.com/Remove/${data.id.toString()}"}
                            """.trimIndent()
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                        context!!.startActivity(Intent.createChooser(shareIntent, "choose one"))
                    } catch (e: java.lang.Exception) {
                        //e.toString();
                    }
                }


                viewDetailsTv.setOnClickListener {
                    viewDetailsInterface.onClick("Remove", data.jobId!!)
                }

                btnRemove.setOnClickListener {
                    removeInterface.onClick("remove", data.id!!)
                }

            }
        }
    }

    fun setList(data: ArrayList<List>) {
        this.data = data
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val jobListItemBinding = JobListItemBinding.inflate(inflater, parent, false)
        return ViewHolder(jobListItemBinding)
    }

    override fun getItemCount(): Int {
        return try {
            data.size
        } catch (e: Exception) {
            0
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(data[position], position)


    interface SelectedDeleteInterface {
        fun onClick(data: String)

    }

    interface ViewDetailsInterface {
        fun onClick(data: String, id: String)

    }

    interface RemoveInterface {
        fun onClick(data: String, id: String)

    }

}
