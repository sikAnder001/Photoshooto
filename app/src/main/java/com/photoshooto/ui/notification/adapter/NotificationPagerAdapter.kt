package com.photoshooto.ui.notification.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.photoshooto.ui.notification.CommonNotificationFragment
import com.photoshooto.ui.notification.extra.Constant
import com.photoshooto.ui.notification.extra.NOTI_STATUS
import com.photoshooto.ui.notification.model.NotificationItem

class NotificationPagerAdapter(
    fm: FragmentManager,
    var allList: List<NotificationItem>
) : FragmentPagerAdapter(fm) {

    private var readList: List<NotificationItem>
    private var unReadList: List<NotificationItem>

    init {
        readList = allList.filter { it.status == NOTI_STATUS.READ }
        unReadList = allList.filter { it.status == NOTI_STATUS.UNREAD }

    }

    companion object {
        public const val TAG = "NotificationPagerAdapt"
    }

    var allFragment: CommonNotificationFragment? = null
    var readFragment: CommonNotificationFragment? = null
    var unReadFragment: CommonNotificationFragment? = null

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        val fragment: Fragment? = null

        when (position) {
            0 -> {
                if (allFragment == null) {
                    allFragment = CommonNotificationFragment.newInstance(allList, Constant.ALL)
                }

                return allFragment as Fragment
            }
            1 -> {
                if (readFragment == null) {
                    readFragment = CommonNotificationFragment.newInstance(readList, Constant.READ)
                }
                return readFragment as Fragment
            }
            2 -> {
                if (unReadFragment == null) {
                    unReadFragment =
                        CommonNotificationFragment.newInstance(unReadList, Constant.UNREAD)
                }
                return unReadFragment as Fragment
            }
            else ->
                return fragment!!
        }
    }


    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getPageTitle(position: Int): CharSequence {
        var title: String? = null
        when (position) {
            0 -> {
                title = Constant.ALL
                return title
            }
            1 -> {
                title = Constant.READ
                return title
            }
            2 -> {
                title = Constant.UNREAD
                return title
            }
            else ->
                return title!!
        }
    }

}
