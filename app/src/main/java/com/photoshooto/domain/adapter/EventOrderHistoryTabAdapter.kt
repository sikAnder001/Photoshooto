import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.tabs.TabLayout
import com.photoshooto.ui.qrorderhistory.AllEventFragment
import com.photoshooto.ui.qrorderhistory.CompletedEventFragment
import com.photoshooto.ui.qrorderhistory.OngoingEventFragment
import com.photoshooto.ui.qrorderhistory.UpcomingEventFragment

class EventOrderHistoryTabAdapter(fm: FragmentManager?, var tabLayout: TabLayout) :
    FragmentStatePagerAdapter(fm!!) {
    var context: Context? = null

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        if (position == 0) {
            fragment = AllEventFragment()
        } else if (position == 1) {
            fragment = OngoingEventFragment()
        } else if (position == 2) {
            fragment = UpcomingEventFragment()
        } else if (position == 3) {
            fragment = CompletedEventFragment()
        }
        return fragment!!
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null

        if (position == 0) {
            title = "All"
        } else if (position == 1) {
            title = "Ongoing"
        } else if (position == 2) {
            title = "Upcoming"
        } else if (position == 3) {
            title = "Completed"
        }
        return title
    }
}