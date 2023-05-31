package com.photoshooto.ui.job.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.photoshooto.R
import com.photoshooto.databinding.FragmentSaveJobBinding
import com.photoshooto.ui.job.JobViewModel
import com.photoshooto.ui.job.utility.removeOverScroll
import com.photoshooto.ui.job.utility.setSafeOnClickListener
import com.photoshooto.ui.job.utility.visible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates


const val NUM_PAGES = 2

class SavedJobsMyPostFragment : Fragment() {
    companion object {
        var sortByPrefill = ""
        lateinit var savedFragment: SavedFragment
        lateinit var myPostFragment: MyPostFragment
    }

    lateinit var binding: FragmentSaveJobBinding
    private lateinit var viewPagerAdapter: CustomViewPagerAdapter

    private val jobHomeViewModel: JobViewModel by viewModel()
    var selectedTab by Delegates.observable(0) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            onTabSelect(newValue)
            /*binding.viewPager.setCurrentItem(newValue, true)*/
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSaveJobBinding.inflate(inflater, container, false)
        val toolbarBinding = binding.toolbarSavedPost
        toolbarBinding.tvTitle.text = "Posts"
        toolbarBinding.endIcon.setImageResource(R.drawable.arrow_sort_down_lines)
        toolbarBinding.endIcon.visible()

        toolbarBinding.backBtn.setSafeOnClickListener {
            findNavController().navigateUp()
        }

        requireActivity().supportFragmentManager.setFragmentResultListener(
            "dialog_sort", viewLifecycleOwner
        ) { requestKey, result ->
            sortByPrefill = result.getString("sort_by") ?: ""

            if (binding.viewPager.currentItem == 1) {
                savedFragment.loadData(true)
            } else {
                myPostFragment.loadData(true)
            }
            //  loadData()
        }

        toolbarBinding.endIcon.setSafeOnClickListener {
            val args = Bundle().apply {
                putString("prefill", sortByPrefill)
                putString("type", "hireyou")
            }
            findNavController().navigate(R.id.dialog_bottom_sort, args)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {
            setupViewPager()
        }

        //onTabSelect(0)
        binding.tabMyPost.tabLabel.text = "My Post"
        binding.tabSavedPost.tabLabel.text = "Saved Post"

        binding.tabMyPost.tabLabel.setOnClickListener {
            selectedTab = 0
            binding.viewPager.setCurrentItem(selectedTab, true)
        }

        binding.viewPager.removeOverScroll()

        binding.tabSavedPost.tabLabel.setOnClickListener {
            selectedTab = 1
            binding.viewPager.setCurrentItem(selectedTab, true)
        }
    }

    private fun onTabSelect(pos: Int) {
        val selectColor = requireContext().resources.getColor(R.color.colorOrange)
        val unSelectColor = requireContext().resources.getColor(R.color.grey_C4)

        binding.tabMyPost.tabLabel.setTextColor(unSelectColor)
        binding.tabSavedPost.tabLabel.setTextColor(unSelectColor)

        binding.tabMyPost.LineBottom.backgroundTintList =
            requireContext().resources.getColorStateList(R.color.grey_C4)
        binding.tabSavedPost.LineBottom.backgroundTintList =
            requireContext().resources.getColorStateList(R.color.grey_C4)

        when (pos) {
            0 -> {
                binding.tabMyPost.tabLabel.setTextColor(selectColor)
                binding.tabMyPost.LineBottom.backgroundTintList =
                    requireContext().resources.getColorStateList(R.color.colorOrange)
            }

            1 -> {

                binding.tabSavedPost.tabLabel.setTextColor(selectColor)
                binding.tabSavedPost.LineBottom.backgroundTintList =
                    requireContext().resources.getColorStateList(R.color.colorOrange)
            }
        }
    }

    private fun setupViewPager() {
        /* val pageAdapter = PageAdapter(childFragmentManager)
         pageAdapter.add(myPostFragment, getString(R.string.my_post))
         pageAdapter.add(savedFragment, getString(R.string.saved_post))*/
        viewPagerAdapter = CustomViewPagerAdapter(this)
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.adapter = viewPagerAdapter

        /*      binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                  override fun onPageScrollStateChanged(state: Int) {}
                  override fun onPageScrolled(
                      position: Int, positionOffset: Float, positionOffsetPixels: Int
                  ) {
                  }

                  override fun onPageSelected(position: Int) {
                      selectedTab = position
                  }
              })*/

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                selectedTab = position
            }

        })
    }

    class CustomViewPagerAdapter(fragment: Fragment?) : FragmentStateAdapter(fragment!!) {
        override fun createFragment(position: Int): Fragment {
            when (position) {
                1 -> {
                    savedFragment = SavedFragment()
                    return savedFragment
                }
                else -> {
                    myPostFragment = MyPostFragment()
                    return myPostFragment
                }
            }
        }

        override fun getItemCount(): Int {
            return NUM_PAGES
        }
    }

    override fun onResume() {
        super.onResume()
        onTabSelect(selectedTab)
        binding.viewPager.setCurrentItem(selectedTab, true)
    }
}