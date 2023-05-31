package com.photoshooto.ui.photographersScreens.photographerDashboard.fragments

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.photoshooto.R
import com.photoshooto.databinding.FragmentPhotographerNewLandingPageBinding
import com.photoshooto.ui.photographersScreens.photographerDashboard.fragments.model.ImageBeans
import com.photoshooto.ui.photographersScreens.photographerDashboard.fragments.model.QuickBeans

class PhotographerNewLandingPageFragment : Fragment() {

    private var newBinding: FragmentPhotographerNewLandingPageBinding? = null
    private val binding get() = newBinding!!
    private lateinit var mimages: Array<Int>
    var currentPage = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        newBinding = FragmentPhotographerNewLandingPageBinding.inflate(inflater, container, false)

        mimages = arrayOf(
            R.drawable.security_img,
            R.drawable.security_img,
            R.drawable.security_img,
            R.drawable.security_img
        )
        initView()
        newBinding!!.quickAccessTitle.setOnClickListener {
            view!!.findNavController()
                .navigate(R.id.action_photographerNewLandingPageFragment_to_liveEventFragment)
        }

        return binding.root

    }

    private fun initView() {

        newBinding?.viewPagerBanner!!.adapter =
            PhotographerViewPagerAdapter(requireActivity(), mimages)
        newBinding!!.dtViewDotsIndicator.setViewPager(newBinding!!.viewPagerBanner!!)


        val wm = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        val width = metrics.widthPixels.toFloat()
        val height = metrics.heightPixels.toFloat()

        fun dpFromPx(context: Context, px: Float): Float {
            return px / context.resources.displayMetrics.density
        }

        var paddingPx: Int = ((width * 11) / 90).toInt()
        var MIN_SCALE: Float = 0.8f;
        var MAX_SCALE: Float = 1f;

        var transformer: ViewPager.PageTransformer = object : ViewPager.PageTransformer {

            override fun transformPage(page: View, position: Float) {
                val pagerWidthPx = (page.parent as ViewPager).width.toFloat()
                val pageWidthPx: Float = pagerWidthPx - 2 * paddingPx
                val maxVisiblePages = pagerWidthPx / pageWidthPx
                val center = maxVisiblePages / 2f
                val scale: Float
                if (position + 0.5f < center - 0.5f || position > center) {
                    scale = MIN_SCALE
                } else {
                    val coef: Float
                    coef = if (position + 0.5f < center) {
                        (position + 1 - center) / 0.5f
                    } else {
                        (center - position) / 0.5f
                    }
                    scale = coef * (MAX_SCALE - MIN_SCALE) + MIN_SCALE
                }
                page.scaleX = scale
                page.scaleY = scale
            }
        }
        newBinding?.viewPagerBanner!!.setClipToPadding(false);
        newBinding?.viewPagerBanner!!.setPadding(paddingPx, 0, paddingPx, 0);
        newBinding?.viewPagerBanner!!.setPageTransformer(false, transformer)


        val array = ArrayList<QuickBeans>()
        array.add(
            QuickBeans(
                R.drawable.photo_image,
                "Photoselection",
                "Select a right plan for you"
            )
        )

        val quickAdapter = QuickAccessItemAdapter(array)
        newBinding!!.quickAccessRecyclerView.layoutManager =
            GridLayoutManager(context, 2)
        newBinding!!.quickAccessRecyclerView.adapter = quickAdapter

        val arrayOnly = ArrayList<ImageBeans>()
        arrayOnly.add(ImageBeans(R.drawable.img_only))

        val photoOnlyAdapter = PhotographerOnlyforyouAdapter(arrayOnly)
        newBinding!!.onlyForYouRecyclerview.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        newBinding!!.onlyForYouRecyclerview.adapter = photoOnlyAdapter

        val arrayAchieve = ArrayList<QuickBeans>()
        arrayAchieve.add(QuickBeans(R.drawable.image_achive, "50L+", "Happy Users"))

        val photoAchiveAdapter = PhotographerOurAchievements(arrayAchieve)
        newBinding!!.ourAchievementsRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        newBinding!!.ourAchievementsRecyclerView.adapter = photoAchiveAdapter


    }


}