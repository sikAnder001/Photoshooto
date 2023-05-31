package com.photoshooto.ui.events.live_event.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.databinding.AuthorisedItemListBinding


class AuthorisedUserAdapter(
    val context: Context?,
    var selectedDetailsInterface: SelectedDetailInterface,
    var selectedDeleteInterface: SelectedDeleteInterface
) : RecyclerView.Adapter<AuthorisedUserAdapter.ViewHolder>() {

    var data = ArrayList<String>()

    inner class ViewHolder(authorizedUserListBinding: AuthorisedItemListBinding) :
        RecyclerView.ViewHolder(authorizedUserListBinding.root) {

        var authorizedUserListBinding = authorizedUserListBinding

        fun bind(data: String, position: Int) {
            with(authorizedUserListBinding) {
                btnDetails.setOnClickListener {
                    selectedDetailsInterface.onClick(data)
                }
                imgDelete.setOnClickListener {
                    selectedDeleteInterface.onClick(data)
                }
            }
        }
    }

    fun setList(data: ArrayList<String>) {
        this.data = data
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val authorizedUserListBinding = AuthorisedItemListBinding.inflate(inflater, parent, false)
        return ViewHolder(authorizedUserListBinding)
    }

    override fun getItemCount(): Int {
        return try {
            data.size
        } catch (e: Exception) {
            0
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(data[position], position)


    interface SelectedDetailInterface {
        fun onClick(data: String)

    }

    interface SelectedDeleteInterface {
        fun onClick(data: String)

    }
}


