package com.photoshooto.ui.admin_screen.manage_job_posting

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.R
import com.photoshooto.databinding.JobListItemBinding
import com.photoshooto.domain.model.jobmodel.List
import com.photoshooto.util.urlAddingForPicture
import java.time.Instant
import java.time.OffsetDateTime
import java.util.*


class JobPostedAdapter(
    var context: Context?,
    val selectedDeleteInterface: SelectedDeleteInterface,
    val viewDetailsInterface: ViewDetailsInterface,
    var approveInterface: ApproveInterface,
    var declineInterface: DeclineInterface
) : RecyclerView.Adapter<JobPostedAdapter.ViewHolder>() {

    var data = ArrayList<List>()

    inner class ViewHolder(jobListItemBinding: JobListItemBinding) :
        RecyclerView.ViewHolder(jobListItemBinding.root) {

        var jobListItemBinding = jobListItemBinding

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(data: List, position: Int) {
            with(jobListItemBinding) {

                Glide.with(context!!)
                    .load(urlAddingForPicture(data.userProfile!!.backgroundImage.toString()))
                    .error(R.drawable.ic_profile_placeholder).into(imageView)

                var odt: OffsetDateTime = OffsetDateTime.parse(data.createdAt)
                var instant: Instant = odt.toInstant()

                var dateO: Date = Date.from(instant)
                var dateC = Date()
                var difference_In_Time = dateC.time - dateO.time

                Log.v("difference", (dateC.time - dateO.time).toString())

                var difference_In_Minutes: Long = ((difference_In_Time / (1000 * 60)) % 60)

                var difference_In_Hours: Long = ((difference_In_Time / (1000 * 60 * 60)) % 24)

                var difference_In_Years: Long = (difference_In_Time / (1000L * 60 * 60 * 24 * 365))

                var difference_In_Days: Long = ((difference_In_Time / (1000 * 60 * 60 * 24)) % 365)

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


                titleTv.text = data.userProfile!!.studioName
                idTv.text = data.id
                locaionTv.text = data.address?.location

                var vid = StringJoiner(",")

                for (element in data.photoCameraUse) {
                    if (!element.isNullOrEmpty()) {
                        vid.add(element)
                    }
                }
                equipmentTv.text = vid.toString()

                photographyTypeTv.text = data.photographyType

                shareTv.setOnClickListener {
                    try {
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
                        var shareMessage = ""
                        shareMessage =
                            """${shareMessage + "www.photoshooto.com/Approve/${data.id.toString()}"}
                            """.trimIndent()
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                        context!!.startActivity(Intent.createChooser(shareIntent, "choose one"))
                    } catch (e: java.lang.Exception) {
                        //e.toString();
                    }
                }

                if (data.status == "approved") {
                    layoutBtnAppDec.visibility = View.GONE
                    viewDetailsTv.visibility = View.GONE
                    viewDetailsTv2.visibility = View.VISIBLE
                    statusTv.visibility = View.VISIBLE
                    statusTv.text = "Approved"
                    statusTv.setTextColor(ContextCompat.getColor(context!!, R.color.green_status))
                    statusTv.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.tick_green_hollow,
                        0,
                        0,
                        0
                    )
                } else if (data.status == "rejected") {
                    layoutBtnAppDec.visibility = View.GONE
                    viewDetailsTv.visibility = View.GONE
                    viewDetailsTv2.visibility = View.VISIBLE
                    statusTv.visibility = View.VISIBLE
                    statusTv.text = "Declined"
                    statusTv.setTextColor(ContextCompat.getColor(context!!, R.color.orange_clr))
                    statusTv.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.cross_orage_hollow,
                        0,
                        0,
                        0
                    )
                } else {
                    layoutBtnAppDec.visibility = View.VISIBLE
                    viewDetailsTv.visibility = View.VISIBLE
                    viewDetailsTv2.visibility = View.GONE
                    statusTv.visibility = View.GONE
                }

                viewDetailsTv.setOnClickListener {
                    viewDetailsInterface.onClick("Approve", data.id!!)
                }

                viewDetailsTv2.setOnClickListener {
                    viewDetailsInterface.onClick("Approve", data.id!!)
                }

                btnApproval.setOnClickListener {
                    approveInterface.onClick("approve", data.id!!)
                }

                btnDecline.setOnClickListener {
                    declineInterface.onClick("decline", data.id!!)
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

    interface ApproveInterface {
        fun onClick(data: String, id: String)

    }

    interface DeclineInterface {
        fun onClick(data: String, id: String)

    }
}
