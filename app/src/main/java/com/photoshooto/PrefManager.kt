package com.photoshooto
//
//import android.R
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import android.text.Html
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.view.WindowManager
//import android.widget.LinearLayout
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.viewpager.widget.PagerAdapter
//import androidx.viewpager.widget.ViewPager
//import androidx.viewpager.widget.ViewPager.OnPageChangeListener
//import com.photoshooto.ui.dashboard.MainActivity
//
//
//class WelcomeActivity : AppCompatActivity() {
//    private var viewPager: ViewPager? = null
//    private var myViewPagerAdapter: MyViewPagerAdapter? = null
//    private var dotsLayout: LinearLayout? = null
//    private var dots: Array<TextView?>
//    private var layouts: IntArray
//    private var btnSkip: Button? = null
//    private var btnNext: Button? = null
//    private var prefManager: PrefManager? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//
//        private var prefManager: PrefManager? = null  // Checking for first time launch - before calling setContentView()
//        prefManager = PrefManager(this)
//        if (!prefManager.isFirstTimeLaunch()) {
//            launchHomeScreen()
//            finish()
//        }
//
//        // Making notification bar transparent
//        if (Build.VERSION.SDK_INT >= 21) {
//            window.decorView.systemUiVisibility =
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//        }
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
//        setContentView(R.layout.activity_welcome)
//        viewPager = findViewById<View>(R.id.view_pager) as ViewPager
//        dotsLayout = findViewById<View>(R.id.layoutDots) as LinearLayout
//        btnSkip = findViewById<View>(R.id.btn_skip) as Button
//        btnNext = findViewById<View>(R.id.btn_next) as Button
//
//
//        // layouts of all welcome sliders
//        // add few more layouts if you want
//        layouts = intArrayOf(
//            R.layout.welcome_slide1,
//            R.layout.welcome_slide2,
//            R.layout.welcome_slide3,
//            R.layout.welcome_slide4
//        )
//
//        // adding bottom dots
//        addBottomDots(0)
//
//        // making notification bar transparent
//        changeStatusBarColor()
//        myViewPagerAdapter = MyViewPagerAdapter()
//        viewPager!!.adapter = myViewPagerAdapter
//        viewPager!!.addOnPageChangeListener(viewPagerPageChangeListener)
//        btnSkip.setOnClickListener(object : OnClickListener() {
//            fun onClick(v: View?) {
//                launchHomeScreen()
//            }
//        })
//        btnNext.setOnClickListener(object : OnClickListener() {
//            fun onClick(v: View?) {
//                // checking for last page
//                // if last page home screen will be launched
//                val current = getItem(+1)
//                if (current < layouts.size) {
//                    // move to next screen
//                    viewPager!!.currentItem = current
//                } else {
//                    launchHomeScreen()
//                }
//            }
//        })
//    }
//
//    private fun addBottomDots(currentPage: Int) {
//        dots = arrayOfNulls(layouts.size)
//        val colorsActive = resources.getIntArray(R.array.array_dot_active)
//        val colorsInactive = resources.getIntArray(R.array.array_dot_inactive)
//        dotsLayout!!.removeAllViews()
//        for (i in dots.indices) {
//            dots[i] = TextView(this)
//            dots[i]!!.text = Html.fromHtml("&#8226;")
//            dots[i]!!.textSize = 35f
//            dots[i]!!.setTextColor(colorsInactive[currentPage])
//            dotsLayout!!.addView(dots[i])
//        }
//        if (dots.size > 0) dots[currentPage]!!.setTextColor(colorsActive[currentPage])
//    }
//
//    private fun getItem(i: Int): Int {
//        return viewPager!!.currentItem + i
//    }
//i
//    private fun launchHomeScreen() {
//        prefManager.setFirstTimeLaunch(false)
//        startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
//        finish()
//    }
//
//    //  viewpager change listener
//    var viewPagerPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
//        override fun onPageSelected(position: Int) {
//            addBottomDots(position)
//
//            // changing the next button text 'NEXT' / 'GOT IT'
//            if (position == layouts.size - 1) {
//                // last page. make button text to GOT IT
//                btnNext.setText(getString(R.string.start))
//                btnSkip.setVisibility(View.GONE)
//            } else {
//                // still pages are left
//                btnNext.setText(getString(R.string.next))
//                btnSkip.setVisibility(View.VISIBLE)
//            }
//        }
//
//        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
//        override fun onPageScrollStateChanged(arg0: Int) {}
//    }
//
//    /**
//     * Making notification bar transparent
//     */
//    private fun changeStatusBarColor() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            val window: Window = window
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            window.setStatusBarColor(Color.TRANSPARENT)
//        }
//    }
//
//    /**
//     * View pager adapter
//     */
//    inner class MyViewPagerAdapter : PagerAdapter() {
//        private var layoutInflater: LayoutInflater? = null
//        override fun instantiateItem(container: ViewGroup, position: Int): Any {
//            layoutInflater =
//                getSystemService<Any>(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//            val view: View = layoutInflater!!.inflate(layouts[position], container, false)
//            container.addView(view)
//            return view
//        }
//
//        override fun getCount(): Int {
//            return layouts.size
//        }
//
//        override fun isViewFromObject(view: View, obj: Any): Boolean {
//            return view === obj
//        }
//
//        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//            val view: View = `object` as View
//            container.removeView(view)
//        }
//    }
//}