package com.photoshooto.ui.notification

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.photoshooto.R
import com.photoshooto.databinding.ActivityNotificationBinding
import com.photoshooto.ui.notification.adapter.NotificationPagerAdapter
import com.photoshooto.ui.notification.extra.Constant
import com.photoshooto.ui.notification.model.NotificationItem
import com.photoshooto.util.onToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class NotificationActivity : AppCompatActivity() {


    companion object {

        fun getDate(value: String): String {

            val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = utcFormat.parse(value)
            val pstFormat = SimpleDateFormat("MMM dd")
            pstFormat.timeZone = TimeZone.getTimeZone("UTC")
//			System.out.println(pstFormat.format(date))
            return pstFormat.format(date)
        }

        fun getTime(value: String): String {

            val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = utcFormat.parse(value)
            val pstFormat = SimpleDateFormat("MMM dd")
            pstFormat.timeZone = TimeZone.getTimeZone("UTC")
//			System.out.println(pstFormat.format(date))
            return pstFormat.format(date)
        }
    }

    private val viewModel: GetNotificationViewModel by viewModel()
    private lateinit var binding: ActivityNotificationBinding
    var viewPagerAdapter: NotificationPagerAdapter? = null
    var viewpagerAdapterList = mutableListOf<NotificationItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BottomNavWithSideNav)
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_notification)/
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialize()
        viewModel.getNotificationList(10, 1)
        with(viewModel) {
            notiLiveData.observe(this@NotificationActivity, Observer {
                if (it.success!!) {
                    onToast(it.message!!, this@NotificationActivity)
                    setAdapter(it.data.list)
                } else
                    it.message?.let { it1 ->
                        onToast(it1, this@NotificationActivity)
                    }
            })
            showProgressbar.observe(this@NotificationActivity, Observer { isVisible ->
                binding.progressBar.visibility =
                    if (isVisible) android.view.View.VISIBLE else android.view.View.GONE
            })
        }
    }

    private fun initialize() {

        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(Constant.ALL)
                .setCustomView(R.layout.custom_tab_layout), true
        )
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(Constant.READ)
                .setCustomView(R.layout.custom_tab_layout)
        )
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(Constant.UNREAD)
                .setCustomView(R.layout.custom_tab_layout)
        )

        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        //Get first tab view
        val pendingTab: TabLayout.Tab = binding.tabLayout!!.getTabAt(0)!!
        val pendingTabView: View = pendingTab.customView!!

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewpager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding.viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(p0: Int) {}

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

            override fun onPageSelected(p0: Int) {
                binding.tabLayout.getTabAt(p0)?.select()

            }
        })

    }

    private fun setAdapter(list: List<NotificationItem>) {

        viewpagerAdapterList.clear()
        viewpagerAdapterList.addAll(list)
        if (viewPagerAdapter == null) {
            viewPagerAdapter = NotificationPagerAdapter(supportFragmentManager, list)
            binding.viewpager.adapter = viewPagerAdapter

        } else {
            binding.viewpager.adapter?.notifyDataSetChanged()
        }

    }
}