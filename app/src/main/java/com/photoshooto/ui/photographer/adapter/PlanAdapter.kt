package com.photoshooto.ui.photographer.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.R
import com.photoshooto.databinding.DialogDeletionBinding
import com.photoshooto.databinding.PackageItemViewBinding
import com.photoshooto.domain.model.PhotographerPlanResponse
import com.photoshooto.ui.job.utility.setSafeOnClickListener

class PlanAdapter(
    var context: Context,
    var dataList: List<PhotographerPlanResponse.PlanDetails>,
    var onItemClickListener: OnItemClickListener,
    val isPhotographer: Boolean = true

) : RecyclerView.Adapter<PlanAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: PackageItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PhotographerPlanResponse.PlanDetails) {
            binding.planType.text = data.type


            binding.amount.text = data.amount




            binding.tvBookNow.visibility = if (!isPhotographer) View.VISIBLE else View.GONE
            binding.deleteIcon.visibility = if (!isPhotographer) View.GONE else View.VISIBLE
            binding.editIcon.visibility = if (!isPhotographer) View.GONE else View.VISIBLE


            binding.session.text = data.session_hours
            Glide.with(binding.root.context).load(data.icon)
                .error(R.drawable.ic_wedding_photography)
                .placeholder(R.drawable.ic_wedding_photography)
                .into(binding.profileImage)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            PackageItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
        holder.binding.apply {
            editIcon.setOnClickListener {
                onItemClickListener.onDetailsClick(position, dataList[position])
            }
            deleteIcon.setOnClickListener {
                val dialog = Dialog(context!!, R.style.DialogSlideAnim)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                var dialogBinding = DialogDeletionBinding.inflate(LayoutInflater.from(context))
                dialog.setContentView(dialogBinding.root)

                dialogBinding.yesBtn.setSafeOnClickListener {
                    onItemClickListener.onDeleteClicked(position, dataList[position])
                    val mutableList = dataList.toMutableList()
                    mutableList.removeAt(position)
                    dataList = mutableList
                    notifyDataSetChanged()
                    dialog.dismiss()
                }

                dialogBinding.cancelBtn.setSafeOnClickListener {
                    dialog.dismiss()
                }

                dialogBinding.crossBtn.setSafeOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()

                /*  MaterialAlertDialogBuilder(
                      holder.binding.root.context, R.style.Theme_BottomNavWithSideNav_Dialog_Alert
                  ).setTitle("Delete Package")
                      .setMessage("Are you sure you want to delete this Package ?")
                      .setCancelable(false)
                      .setPositiveButton(
                          "Yes, Delete"
                      ) { _, _ ->
                          onItemClickListener.onDeleteClicked(position, dataList[position])
                          val mutableList = dataList.toMutableList()
                          mutableList.removeAt(position)
                          dataList = mutableList
                          notifyDataSetChanged()
                      }.setNegativeButton(
                          "Cancel"
                      ) { dialog, _ -> dialog.cancel() }.show()*/
            }
        }
        holder.binding.tvBookNow.setOnClickListener {
            onItemClickListener.onBookNowClicked()
        }
    }

    override fun getItemCount() = dataList.size

    interface OnItemClickListener {
        fun onDetailsClick(position: Int, data: PhotographerPlanResponse.PlanDetails)
        fun onBookNowClicked()
        fun onDeleteClicked(position: Int, data: PhotographerPlanResponse.PlanDetails)
    }
}