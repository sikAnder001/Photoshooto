package com.photoshooto.ui.qrcodesetup.myQrCodes

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.ItemMyQrCodesBinding
import com.photoshooto.domain.model.GetEventResponse
import com.photoshooto.util.*

@SuppressWarnings("All")
class MyQRCodesAdapter(
    private val mContext: Context,
    private var listItem: List<GetEventResponse.GetEventData.EventModel?>,
    private var mListener: OnItemClickView<GetEventResponse.GetEventData.EventModel>,
    private var mListenerViewOrder: OnItemClick<GetEventResponse.GetEventData.EventModel>
) : RecyclerView.Adapter<MyQRCodesAdapter.ViewHolder>() {

    inner class ViewHolder(private var mBinding: ItemMyQrCodesBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bindData(position: Int, data: GetEventResponse.GetEventData.EventModel?) {
            if (data != null) {
                with(data) {
                    if (qrcode.isNullOrEmpty()) {
                        mBinding.imageItemQrMain.setImageDrawable(mContext.getDrawableValue(R.drawable.image_transparent))
                    } else {
                        val mQRImage = qrcode!![0]!!.url
                        if (checkStringValue(mQRImage)) {
                            mContext.loadImage(mBinding.imageItemQrMain, mQRImage)
                        } else {
                            mBinding.imageItemQrMain.setImageDrawable(mContext.getDrawableValue(R.drawable.image_transparent))
                        }
                    }

                    mBinding.tvItemQrTitle.text = checkStringReturnValue(event_name)
                    mBinding.tvItemQrId.text = "${mContext.getStringValue(R.string.qr_id)} : ${
                        checkStringReturnValue(qrcode_id)
                    }"
                    mBinding.tvItemQrQty.text = "${mContext.getStringValue(R.string.quantity)} : ${
                        checkStringReturnValue(quantity)
                    }"
                    mBinding.tvItemQrType.text =
                        "${mContext.getStringValue(R.string.standee_type)} : ${
                            checkStringReturnValue(standee_type)
                        }"
                    mBinding.tvItemQrDateGenerated.text =
                        "${mContext.getStringValue(R.string.date_generated)} : ${
                            checkStringReturnValue(event_start_date)
                        } ${checkStringReturnValue(event_start_time)}"
                    mBinding.tvItemQrOrderDate.text =
                        "${mContext.getStringValue(R.string.ordered_date)} : ${
                            checkStringReturnValue(event_start_date)
                        }"

                    mBinding.imageItemQrOption.setOnClickListener {
                        mListener.onItemClick(data, position, it)
                    }
                    mBinding.tvItemQrViewOrder.setOnClickListener {
                        mListenerViewOrder.onItemClick(data, position)
                    }
                    mBinding.tvItemQrDownload.setOnClickListener {
                        if (!qrcode.isNullOrEmpty()) {
                            val mQRImage = qrcode!![0]!!.url
                            if (checkStringValue(mQRImage)) {
                                mContext.createAndSaveFileFromBase64Url(mQRImage!!) {
                                    onToast("QR Saved to Download folder under storage", mContext)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMyQrCodesBinding.inflate(
                LayoutInflater.from(mContext), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(position, listItem[position])
    }

    override fun getItemCount(): Int {
        return listItem.size
    }
}
