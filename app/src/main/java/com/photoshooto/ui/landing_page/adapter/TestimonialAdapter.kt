package com.photoshooto.ui.landing_page.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.databinding.AdapterTestimonialBinding
import com.photoshooto.domain.model.Testimonial
import com.photoshooto.util.DOMAIN
import com.photoshooto.util.ucFirst
import java.util.*

class TestimonialAdapter constructor(
    private val context: Context, private val values: List<Testimonial>
) : RecyclerView.Adapter<TestimonialAdapter.ViewHolder>() {

    private val TAG = TestimonialAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = AdapterTestimonialBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = values[position]
        holder.bind(task, holder.itemView)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(private val itemBinding: AdapterTestimonialBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(testimonial: Testimonial, itemView: View) {

            Glide.with(context).load(DOMAIN + testimonial.user_profile?.profile_image)
                .into(itemBinding.userPic)

            val ratings: Float? = testimonial.rating

            itemBinding.rating.rating = ratings!!
            itemBinding.name.text =
                testimonial.user_profile?.studio_name?.uppercase(Locale.getDefault())
                    ?: ""
            itemBinding.subject.text = testimonial.subject?.let { ucFirst(it) } ?: ""
            itemBinding.desc.text = testimonial.message ?: ""
        }
    }
}