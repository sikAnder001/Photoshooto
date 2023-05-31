package com.photoshooto.domain.adapter

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.QrCodeEventItemListBinding
import com.photoshooto.domain.model.EventOrderHistoryElement
import com.photoshooto.ui.qrorderhistory.EventOrderDetailsFragment
import com.photoshooto.util.checkStringValue
import com.photoshooto.util.createAndSaveFileFromBase64Url
import com.photoshooto.util.onToast

class QREventOrderHistoryAdapter(
    private var mContext: FragmentActivity,
    var eventList: List<EventOrderHistoryElement>
) : RecyclerView.Adapter<QREventOrderHistoryAdapter.ViewPagerHolder>(), View.OnClickListener {


    class ViewPagerHolder(var binding: QrCodeEventItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: EventOrderHistoryElement) {
            binding.apply {

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        mContext = parent.context as FragmentActivity
        return ViewPagerHolder(
            QrCodeEventItemListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        holder.bind(eventList[holder.position])
        holder.itemView.setOnClickListener(this)
        holder.itemView.setOnClickListener {
            val eventOrderDetailsFragment = EventOrderDetailsFragment()
            val bundle = Bundle()
            bundle.putString("NAME", "Test")
            eventOrderDetailsFragment.arguments = bundle
        }
        holder.binding.apply {
            val listItem: EventOrderHistoryElement = eventList.get(position);
            tvEventName.text = listItem.standee_type + " " + mContext!!.getString(R.string.qr_code)
            tvOrderId.text = listItem.qrcode_id
            tvStandeeType.text = listItem.standee_type
            tvQty.text = listItem.quantity.toString()
            tvDate.text = listItem.event_start_date

            tvItemDownload.setOnClickListener {
                if (!listItem.qrcode.isNullOrEmpty()) {
                    val mQRImage = listItem.qrcode!![0]!!.url
                    if (checkStringValue(mQRImage)) {
                        mContext.createAndSaveFileFromBase64Url(mQRImage!!) {
                            onToast("QR Saved to Download folder under storage", mContext)
                        }
                    }
                }
            }

            for (qrItem in listItem.qrcode) {
                var qrURL = qrItem.url.toString()
                val imageBytes = Base64.decode(qrURL, Base64.DEFAULT)
                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                /* QREventOrderHistoryAdapter.context.apply {
                     Glide.with(this).load(decodedImage)
                         .fitCenter()
                         .into(ivQrEvent);
                 }*/
                // val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                //ivQrEvent.setImageBitmap(decodedImage)
            }

        }
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            (R.id.card_event_item) -> {

            }
        }
    }
}