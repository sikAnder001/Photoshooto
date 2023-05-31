package com.photoshooto.ui.usedPhotographyScreens.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.databinding.FileListItemBinding
import com.photoshooto.domain.model.FileImagePRQ


class FileUploadListAdapter(
    val onDelete: (FileImagePRQ) -> Unit,
    val onZoom: (FileImagePRQ) -> Unit
) : RecyclerView.Adapter<FileUploadListAdapter.RecyclerHolder>() {

    private var items: List<FileImagePRQ> = emptyList()

    fun swapList(items: ArrayList<FileImagePRQ>) {
        this.items = items

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder(
            FileListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.bind(items.get(position))
    }

    inner class RecyclerHolder(val binding: FileListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(file: FileImagePRQ) {


            if (file.imageUrl.length != 0) {
                Glide.with(itemView.context)
                    .load(file.imageUrl)
                    .into(binding.imgFile)
            } else {
                val bitmap = BitmapFactory.decodeFile(file.file?.path)
                binding.imgFile.setImageBitmap(bitmap)
            }


            binding.imgDelete.setOnClickListener {
                onDelete(file)
            }

            binding.imgZoom.setOnClickListener {
                onZoom(file)
            }
        }
    }

    override fun getItemCount() = items.size
}