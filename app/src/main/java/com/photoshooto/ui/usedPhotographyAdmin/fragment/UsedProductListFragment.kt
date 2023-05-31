package com.photoshooto.ui.usedPhotographyAdmin.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.photoshooto.R
import com.photoshooto.bottomsheet.FilterByActionSheetFragment
import com.photoshooto.bottomsheet.SortByActionSheetFragment
import com.photoshooto.databinding.FragmentUsedProductListBinding
import com.photoshooto.domain.model.CategoryModel
import com.photoshooto.domain.model.LocationFilter
import com.photoshooto.domain.model.User
import com.photoshooto.ui.admin_screen.manage_job_posting.JobTabViewPagerAdapter
import com.photoshooto.ui.usedPhotographyAdmin.adapter.TabViewPagerAdapter
import com.photoshooto.util.SharedPrefsHelper

class UsedProductListFragment : Fragment() {

    lateinit var binding: FragmentUsedProductListBinding

    var user: User? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUsedProductListBinding.inflate(inflater, container, false)

        openTabs("")

        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.imgFilter.setOnClickListener {

            filterByBottomSheet()
        }

        binding.imgSort.setOnClickListener {

            openSort()
        }

        return binding.root
    }

    private fun openSort() {
        var dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_product_sort_by)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val rgGroup = dialog.findViewById(R.id.rdGroup) as RadioGroup
        val btnApply = dialog.findViewById(R.id.btnApply) as Button

        btnApply.setOnClickListener {
            var id: Int = rgGroup.checkedRadioButtonId
            if (id != -1) {
                val radio: RadioButton = dialog.findViewById(id)
                openTabs(radio.text as String)
                dialog.dismiss()
            }
        }

        dialog.show()
    }


    private fun openTabs(param: String) {

        /*   val tabProductListed = binding.tbLayout.newTab().setText(getString(R.string.product_listed))
           val tabReportedProduct =
               binding.tbLayout.newTab().setText(getString(R.string.reported_product))
           binding.tbLayout.addTab(tabProductListed)
           binding.tbLayout.addTab(tabReportedProduct)

           tabProductListed.view.setOnClickListener {
               binding.vpTab.currentItem = 0
               //  setBottomText(0)

           }
           tabReportedProduct.view.setOnClickListener {
               binding.vpTab.currentItem = 1
               // setBottomText(1)

           }*/



        binding.vpTab.adapter =  TabViewPagerAdapter(childFragmentManager, lifecycle, param)
        TabLayoutMediator(binding.tbLayout, binding.vpTab) { tab, position ->
            tab.text =
                arrayOf(getString(R.string.product_listed), getString(R.string.reported_product))[position]
        }.attach()



/*
        val myPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tbLayout.getTabAt(position)?.select()

                //  setBottomText(position)
            }
        }
        binding.vpTab.registerOnPageChangeCallback(myPageChangeCallback)*/



    }


/*

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = SharedPrefsHelper.getUserCommon()
        val tabProductListed = binding.tbLayout.newTab().setText(getString(R.string.product_listed))
        val tabReportedProduct =
            binding.tbLayout.newTab().setText(getString(R.string.reported_product))
        binding.tbLayout.addTab(tabProductListed)
        binding.tbLayout.addTab(tabReportedProduct)

        tabProductListed.view.setOnClickListener {
            binding.vpTab.currentItem = 0
            //  setBottomText(0)

        }
        tabReportedProduct.view.setOnClickListener {
            binding.vpTab.currentItem = 1
            // setBottomText(1)

        }

        *//*val adapter = TabViewPagerAdapter(childFragmentManager, lifecycle, param)

        binding.vpTab.adapter = adapter*//*


        val myPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tbLayout.getTabAt(position)?.select()

                //  setBottomText(position)
            }
        }
        binding.vpTab.registerOnPageChangeCallback(myPageChangeCallback)


    }*/

    /* private fun setBottomText(position: Int) {
         if (position == 0) {
             binding.tvJobId.isVisible = true
             binding.tvJobTitle.text = getString(R.string.job_approved)
             binding.tvJobId.text = ": AD045664"
         } else {
             binding.tvJobTitle.text = getString(R.string.post_removed)
             binding.tvJobId.isVisible = false
         }

     }*/


    private fun sortByBottomSheet() {
        val sheet = SortByActionSheetFragment.newInstance("", onTap = {

        })

        sheet.show(childFragmentManager, "")
    }

    private fun filterByBottomSheet() {
        val categoryList: ArrayList<CategoryModel> = ArrayList()
        val locationsList: ArrayList<LocationFilter> = ArrayList()

        val sheet = FilterByActionSheetFragment.newInstance(
            categoryList,
            locationsList,
            onProductTap = { selectCategory, selectLocation, startPrice, endPrice ->

            },
            onCancer = {

            },
            onClear = {

            })

        sheet.show(childFragmentManager, "")
    }
}