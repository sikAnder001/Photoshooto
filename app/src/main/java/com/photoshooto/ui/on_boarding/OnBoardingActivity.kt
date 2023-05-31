package com.photoshooto.ui.on_boarding

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.photoshooto.R
import com.photoshooto.databinding.ActivityOnBoardingBinding
import com.photoshooto.ui.photographersScreens.photographerAuth.AuthActivity
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        binding.btnNext.setOnClickListener {
            if (binding.introSliderViewPager.currentItem + 1 < introSliderAdapter.itemCount) {
                binding.introSliderViewPager.currentItem += 1
            } else {
                navigateToLoginScreen()
            }
        }

        binding.tvSkipIntro.setOnClickListener {
            navigateToLoginScreen()
        }
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.apply {
            startActivity(this)
            finish()
        }
    }

    private fun init() {
        SharedPrefsHelper.write(SharedPrefConstant.SHOW_ON_BOARDING, true)
        binding.introSliderViewPager.adapter = introSliderAdapter
        setupIndicators()
        setCurrentIndicator(0)
        binding.introSliderViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
    }

    private val introSliderAdapter = IntroSliderAdapter(
        listOf(
            IntroSlide(
                "Scan QR Code",
                "Scan & get access to Photos for \nDownloading and Sharing",
                R.drawable.ic_frame
            ),
            IntroSlide(
                "Register",
                "Register to enjoy the Best Experience of \nPhotoshooto",
                R.drawable.ic_frame1
            ),
            IntroSlide(
                "View & Download",
                "View and Download Photos Anytime \nAnywhere  Seamlessly.",
                R.drawable.ic_frame2
            )
        )
    )

    private fun setCurrentIndicator(index: Int) {
        val childCount = binding.indicatorsContainer.childCount
        for (i in 0 until childCount) {
            val imageView2 = binding.indicatorsContainer[i] as ImageView
            if (i == index) {
                imageView2.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView2.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(introSliderAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(this)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@OnBoardingActivity,
                        R.drawable.indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            binding.indicatorsContainer.addView(indicators[i])
        }
    }
}