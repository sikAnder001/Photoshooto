package com.photoshooto.ui.usedPhotographyScreens.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Filter
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.SelectListItemBinding
import com.photoshooto.domain.model.BrandModel
import com.photoshooto.ui.job.utility.showKeyboard
import java.util.*


class SelectProductListAdapter(var mContext: Context, private val onTab: (String) -> Unit) :
    RecyclerView.Adapter<SelectProductListAdapter.RecyclerHolder>() {

    private var items: List<BrandModel> = emptyList()
    private var itemsFiltered: List<BrandModel> = ArrayList<BrandModel>()

    var selectedPosition = -1
    var isFirstTime = false

    fun swapList(items: ArrayList<BrandModel>) {
        this.items = items
        this.itemsFiltered = items

        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder(
            SelectListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.bind(itemsFiltered.get(position), position)
    }

    inner class RecyclerHolder(val binding: SelectListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BrandModel, position: Int) {

            binding.tvItem.text = item.name

            binding.etOther.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    onTab(binding.etOther.text.toString())

                    return@OnEditorActionListener true
                }
                false
            })

            itemView.setOnClickListener {
                selectedPosition = position
                isFirstTime = false

                notifyDataSetChanged()
            }

            if (isFirstTime && position == 0) {
                binding.imgRing.setImageResource(R.drawable.ic_ring_fill)
            } else if (selectedPosition == position) {
                binding.imgRing.setImageResource(R.drawable.ic_ring_fill)
                if (item.name.equals("Others")) {

                    editTextView()

                } else {
                    editTextHide()
                    onTab(item.name)
                }
            } else {
                binding.imgRing.setImageResource(R.drawable.ic_ring_unfill)
                editTextHide()
            }
        }


        private fun editTextView() {
            binding.etOther.isVisible = true
            binding.tvItem.isVisible = false
            binding.etOther.setText("")
            binding.etOther.requestFocus();
            itemView.context.showKeyboard()
            binding.etOther.setSelection(0)
        }

        private fun editTextHide() {
            binding.etOther.isVisible = false
            binding.tvItem.isVisible = true

        }
    }

    override fun getItemCount() = itemsFiltered.size


    val filter: Filter
        get() = object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                itemsFiltered = if (charString.isEmpty()) {
                    items
                } else {
                    val filteredList: ArrayList<BrandModel> =
                        ArrayList<BrandModel>()
                    for (row in items) {
                        if (row.name.lowercase(Locale.ROOT)
                                ?.contains(charString.lowercase(Locale.ROOT)) == true
                        ) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = itemsFiltered
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                itemsFiltered = filterResults.values as ArrayList<BrandModel>
                notifyDataSetChanged()
            }
        }
}