package com.photoshooto.ui.usedPhotographyScreens.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Filter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.SelectListItemBinding
import com.photoshooto.ui.job.utility.showKeyboard
import java.util.*


class SelectItemListAdapter(var mContext: Context, private val onTab: (String) -> Unit) :
    RecyclerView.Adapter<SelectItemListAdapter.RecyclerHolder>() {

    private var items: List<String> = emptyList()
    private var itemsFiltered: List<String> = ArrayList<String>()

    private var isCondition: Boolean = false

    var selectedPosition = -1
    var isFirstTime = false

    fun swapList(items: ArrayList<String>, isCondition: Boolean = false) {
        this.items = items
        this.itemsFiltered = items
        this.isCondition = isCondition

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder(
            SelectListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount() = itemsFiltered.size

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.bind(itemsFiltered.get(position), position)
    }

    inner class RecyclerHolder(val binding: SelectListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NewApi")
        fun bind(item: String, position: Int) {

            binding.tvItem.text = item

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

            val typefaceRegular: Typeface = mContext.getResources().getFont(R.font.poppins_regular)
            val typefaceMedium: Typeface = mContext.getResources().getFont(R.font.poppins_medium)

            if (isCondition) {
                if (isFirstTime && position == 0) {
                    binding.imgRing.setImageResource(R.drawable.ic_ring_fill)
                    binding.tvItem.setTypeface(typefaceMedium)
                    binding.tvItem.setTextColor(
                        ContextCompat.getColor(
                            mContext,
                            R.color.text_F0481B
                        )
                    )

                } else if (selectedPosition == position) {
                    binding.imgRing.setImageResource(R.drawable.ic_ring_fill)
                    binding.tvItem.setTypeface(typefaceMedium)
                    binding.tvItem.setTextColor(
                        ContextCompat.getColor(
                            mContext,
                            R.color.text_F0481B
                        )
                    )
                    if (item.equals("Others")) {
                        if (isFirstTime && position == 0) {
                            binding.imgRing.setImageResource(R.drawable.ic_ring_fill)
                        } else if (selectedPosition == position) {
                            binding.imgRing.setImageResource(R.drawable.ic_ring_fill)
                            if (item.equals("Others")) {

                                editTextView()
                                binding.etOther.doOnTextChanged { text, start, count, after ->
                                    onTab(text.toString())
                                }
                            } else {
                                editTextHide()
                                onTab(item)
                            }

                        } else {
                            binding.imgRing.setImageResource(R.drawable.ic_ring_unfill)
                            binding.tvItem.setTypeface(typefaceRegular)
                            binding.tvItem.setTextColor(
                                ContextCompat.getColor(
                                    mContext,
                                    R.color.grey21
                                )
                            )
                            editTextView()

                        }
                    } else {
                        if (isFirstTime && position == 0) {
                            binding.imgRing.setImageResource(R.drawable.ic_ring_fill)
                        } else if (selectedPosition == position) {
                            binding.imgRing.setImageResource(R.drawable.ic_ring_fill)
                            if (item.equals("Others")) {

                                editTextView()

                            } else {
                                editTextHide()
                                onTab(item)
                            }
                        } else {
                            binding.imgRing.setImageResource(R.drawable.ic_ring_unfill)
                            editTextHide()
                        }
                        onTab(item)
                    }
                } else {
                    binding.imgRing.setImageResource(R.drawable.ic_ring_unfill)
                    editTextHide()

                }
            } else {
                if (isFirstTime && position == 0) {
                    binding.imgRing.setImageResource(R.drawable.ic_ring_fill)
                } else if (selectedPosition == position) {
                    binding.imgRing.setImageResource(R.drawable.ic_ring_fill)
                    if (item.equals("Others")) {

                        editTextView()

                    } else {
                        editTextHide()
                        onTab(item)
                    }
                } else {
                    binding.imgRing.setImageResource(R.drawable.ic_ring_unfill)
                    editTextHide()
                }
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


    val filter: Filter
        get() = object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                itemsFiltered = if (charString.isEmpty()) {
                    items
                } else {
                    val filteredList: ArrayList<String> =
                        ArrayList<String>()
                    for (row in items) {
                        if (row.lowercase(Locale.ROOT)
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
                itemsFiltered = filterResults.values as ArrayList<String>
                notifyDataSetChanged()
            }
        }
}