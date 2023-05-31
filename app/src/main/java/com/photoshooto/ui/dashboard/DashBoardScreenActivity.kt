package com.photoshooto.ui.dashboard

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.photoshooto.R
import com.photoshooto.databinding.ActivityDashboardBinding
import com.photoshooto.domain.usecase.login.UserLoginViewModel
import com.photoshooto.domain.usecase.profile.GetUserProfileViewModel
import com.photoshooto.firebase.FirebaseAnalytics_Event_ScreenName
import com.photoshooto.ui.dashboard.ui.home.ExitAlertDialog
import com.photoshooto.ui.login.GetSupportDialog
import com.photoshooto.ui.photographersScreens.photographerAuth.AuthActivity
import com.photoshooto.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class DashBoardScreenActivity : AppCompatActivity(),
    NavController.OnDestinationChangedListener {
    lateinit var binding: ActivityDashboardBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private val userLoginViewModel: UserLoginViewModel by viewModel()
    private val profileViewModel: GetUserProfileViewModel by viewModel()
    private var mDrawerLayout: DrawerLayout? = null
    private var mDrawerToggle: ActionBarDrawerToggle? = null
    var profileComplete: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BottomNavWithSideNav)
        super.onCreate(savedInstanceState)
        recordScreenView(
            this@DashBoardScreenActivity,
            "DashBoardActivity",
            FirebaseAnalytics_Event_ScreenName.screenDashboard
        )

        binding = ActivityDashboardBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initViews()

        profileViewModel.getProfileByIDUseCase(
            SharedPrefsHelper.read(SharedPrefConstant.USER_ID),
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
        )

        with(userLoginViewModel) {
            userLogoutStatus.observe(this@DashBoardScreenActivity) { response ->
                if (response != null) {
                    Log.e("logout response", "" + response.toString())
                    when (response.status) {
                        Status.SUCCESS -> {
                            logoutAndRedirect()
                        }
                        Status.ERROR -> {
                            logoutAndRedirect()
                            response.data?.message?.let { it1 -> showToast(it1) }
                        }
                        else -> {
                            logoutAndRedirect()
                        }
                    }
                }
            }
        }

        with(profileViewModel) {
            getUserData.observe(this@DashBoardScreenActivity) { response ->
                if (response != null) {
                    Log.d("response", "" + response)
                    when (response.status) {
                        Status.SUCCESS -> {
                            val profileData = response.data?.data!!
                            val userProfileModel = profileData.profile_details!!

                            val profileImage = userProfileModel.profile_image!!

                            SharedPrefsHelper.write(
                                SharedPrefConstant.PROFILE_URL,
                                profileImage
                            )

                            val profileCompleted = profileData.profile_complete
                            profileCompleted?.toInt()?.let {
                                SharedPrefsHelper.write(
                                    SharedPrefConstant.PROFILE_COMPLETED,
                                    it
                                )
                            }
                            handleSideNavigationData()
                        }
                        Status.ERROR -> {
                            val message = response.hashCode()
                            if (message == 401) {
                                resetToSplashScreen(this@DashBoardScreenActivity)
                            } else {
                                response.data?.message?.let { it1 -> showToast(it1) }
                            }
                        }
                        else -> {}
                    }
                }
            }
        }


        mDrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val actionDrawerToggle =
            ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close)
        mDrawerLayout?.addDrawerListener(actionDrawerToggle)
        actionDrawerToggle.syncState()

        mDrawerLayout?.closeDrawer(GravityCompat.START)

        setupDrawer()

        handleSideNavigationData()
    }

    override fun onResume() {
        super.onResume()
        profileViewModel.getProfileByIDUseCase(
            SharedPrefsHelper.read(SharedPrefConstant.USER_ID),
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
        )
    }

    private fun logoutAndRedirect() {
        SharedPrefsHelper.clearAllPreferences()
        SharedPrefsHelper.write(SharedPrefConstant.SHOW_ON_BOARDING, true)
        clearApplicationData(applicationContext)
        clearPreferences(applicationContext)
        navigateToAuthScreen()
    }

    private fun navigateToAuthScreen() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.apply {
            startActivity(this)
            finish()
        }
    }

    private fun initViews() {
        navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?)!!
        navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.nav_user_dashboard)
        navController.graph = navGraph

        navController.addOnDestinationChangedListener(this)

        binding.ivSideMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.thanksForRegistration.imageViewBack.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.thanksForRegistration.tvEditProfile.setOnClickListener {
            navigateToProfile()
        }

    }

    private fun handleSideNavigationData() {
        setHeaderText(
            binding.navDrawer.tvName,
            SharedPrefsHelper.read(SharedPrefConstant.USER_NAME)
        )

        setHeaderText(binding.navDrawer.tvId, SharedPrefsHelper.read(SharedPrefConstant.USER_ID))

        profileComplete = SharedPrefsHelper.read(SharedPrefConstant.PROFILE_COMPLETED, 0)
        binding.navDrawer.profileProgress.progress = profileComplete ?: 0
        binding.navDrawer.progressText.text = String.format("%s", "$profileComplete%")
        binding.navDrawer.navVersionNo.text = "v ${getVersionName(this)}"

        val profileURL = SharedPrefsHelper.read(SharedPrefConstant.PROFILE_URL)
        if (!profileURL.isNullOrEmpty()) {
            Glide.with(this)
                .load(urlAddingForPicture(profileURL))
                .error(R.drawable.ic_girl_profile)
                .into(binding.navDrawer.profileImage)
        }

        Glide.with(this)
            .load(urlAddingForPicture(SharedPrefConstant.PROFILE_URL))
            .error(R.drawable.ic_girl_profile)
            .into(binding.ivProfileImg)

        manageSideNavigationClicks()

    }

    private fun setHeaderText(textView: TextView, value: String?) {
        if (value.isNullOrEmpty()) {
            textView.gone()
        } else {
            textView.visible()
            textView.text = value
        }
    }

    private fun manageSideNavigationClicks() {

        binding.navDrawer.navCart.setOnClickListener {
            closeDrawers()
            navController.navigate(R.id.fragmentMyCart)
        }
        binding.ivNotification.setOnClickListener {
            navController.navigate(R.id.userNotificationsFragment)
        }

        binding.navDrawer.navHeader.setOnClickListener {
            closeDrawers()
            navigateToProfile()
        }

        binding.ivProfileImg.setOnClickListener {
            closeDrawers()
            navigateToProfile()
        }

        binding.navDrawer.navDashboard.setOnClickListener {
            closeDrawers()
            navigateToDashboard()
        }
        binding.navDrawer.icClose.setOnClickListener {
            closeDrawers()
        }

        binding.navDrawer.navProfile.setOnClickListener {
            closeDrawers()
            navigateToProfile()
        }

        openInformationSection()

        binding.navDrawer.navTermsNConditions.setOnClickListener {
            closeDrawers()
            val bundle = getTermsNConditionBundle()
            navController.navigate(
                R.id.navigation_Terms,
                bundle
            )
        }

        binding.navDrawer.navPrivacyPolicy.setOnClickListener {
            closeDrawers()
            val bundle = getPrivacyPolicyBundle()
            navController.navigate(
                R.id.navigation_Terms,
                bundle
            )
        }

        binding.navDrawer.navFeedback.setOnClickListener {
            closeDrawers()
            navigateToFeedback()
        }

        binding.navDrawer.navRatePlayStore.setOnClickListener {
            closeDrawers()
            navigateToRateOnPlayStore(this)
        }

        binding.navDrawer.navShareApp.setOnClickListener {
            closeDrawers()
            navigateToShareApp(this)
        }

        binding.navDrawer.navRecycleBin.setOnClickListener {
            closeDrawers()
            navController.navigate(R.id.fragmentRecyclerView)
        }

        binding.navDrawer.navSupport.setOnClickListener {
            closeDrawers()
            GetSupportDialog.newInstance("", applicationContext)
                .show(this.supportFragmentManager, GetSupportDialog.TAG)
        }

        binding.navDrawer.navLogout.setOnClickListener {
            closeDrawers()
            navigateToLogoutDialog()
        }


    }

    private fun navigateToFeedback() {
        navController.navigate(R.id.fragmentUserFeedback)
    }

    fun openNavigationDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun showToolbarNavigation() {
//        binding.toolbar.visibility = View.VISIBLE
    }

    private fun hideBothNavigation() {
//        binding.bottomNavView.visibility = View.GONE
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//        binding.toolbar.visibility = View.GONE
    }

    private fun showBothNavigation() {
//        binding.toolbar.visibility = View.VISIBLE
//        binding.bottomNavView.visibility = View.VISIBLE
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    private fun setupNavControl() {
//        binding.navView.setupWithNavController(navController)
//        binding.bottomNavView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            /*val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_photographer_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController*/

            if (navController.currentDestination?.id == R.id.homePageFragment ||
                navController.currentDestination?.id == R.id.fragmentThanksRegister
            ) {
                ExitAlertDialog.newInstance("DashBoardScreenActivity", applicationContext)
                    .show(supportFragmentManager, ExitAlertDialog.TAG)
            } else {
                super.onBackPressed()
            }
        }
    }

    private fun logout() {
        MaterialAlertDialogBuilder(
            this, R.style.Theme_BottomNavWithSideNav_Dialog_Alert
        ).setTitle(this.resources.getString(R.string.logout))
            .setMessage(this.resources.getString(R.string.would_you_like_to_logout))
            .setCancelable(false)
            .setPositiveButton(
                this.resources.getString(R.string.logout_ok)
            ) { dialog, which ->
                binding.drawerLayout.closeDrawers()
                userLoginViewModel.logout(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))
            }.setNegativeButton(
                this.resources.getString(R.string.cancel)
            ) { dialog, which -> dialog.cancel() }.show()
    }

    fun closeApp() {
        finish()
    }

    private fun setupDrawer() {
        mDrawerToggle = object : ActionBarDrawerToggle(
            this,
            mDrawerLayout,
            R.string.open,
            R.string.close
        ) {
            /** Called when a drawer has settled in a completely open state.  */
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
//                supportActionBar.setTitle(R.string.film_genres)
                invalidateOptionsMenu() // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state.  */
            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)
//                supportActionBar.setTitle(mActivityTitle)
                invalidateOptionsMenu() // creates call to onPrepareOptionsMenu()
            }
        }
        mDrawerToggle?.isDrawerIndicatorEnabled = true
        mDrawerLayout!!.setDrawerListener(mDrawerToggle)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle!!.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mDrawerToggle!!.onConfigurationChanged(newConfig)
    }

    // bottom nav
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return item.onNavDestinationSelected(navHostFragment.navController) ||
//                super.onOptionsItemSelected(item)
//    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (mDrawerToggle!!.onOptionsItemSelected(item)) {
            true
        } else item.onNavDestinationSelected(navHostFragment.navController) || super.onOptionsItemSelected(
            item
        )
    }

    fun hanldeSideNavigationClicks(from: String) {
        binding.drawerLayout.closeDrawers()
        val currentDestination = navController.currentDestination?.id
        if (from.equals(resources.getString(R.string.dashboard))) {
        } else if (from.equals(resources.getString(R.string.qr_events))) {
        } else if (from.equals(resources.getString(R.string.shop))) {
        } else if (from.equals(resources.getString(R.string.ar_cards))) {
        } else if (from.equals(resources.getString(R.string.my_profile))) {
            if (PreferenceManager.getLoginType.equals("User")) {
                navController.navigate(R.id.navigation_profile)
            } else {
                navController.navigate(R.id.navigation_photographer_profile)
            }
        } else if (from.equals(resources.getString(R.string.my_orders))) {
            if (PreferenceManager.getLoginType == "User" && PreferenceManager.employeeBy == "photoshooto") {
                navController.navigate(R.id.navigation_orders)
            } else if (PreferenceManager.getLoginType == "User" && PreferenceManager.employeeBy == "printo") {

                navController.navigate(R.id.navigation_orders_status)
            }
        } else if (from.equals(resources.getString(R.string.order_requests))) {
            navController.navigate(R.id.navigation_orders_request)
        } else if (from.equals(resources.getString(R.string.order_status))) {
            navController.navigate(R.id.navigation_orders_status)
        } else if (from.equals(resources.getString(R.string.settings))) {
        } else if (from.equals(resources.getString(R.string.send_feedback))) {
            navigateToFeedback()
        } else if (from.equals(resources.getString(R.string.recycle_bin))) {
        } else if (from.equals(resources.getString(R.string.information))) {
            openInformationSection()
        } else if (from.equals(resources.getString(R.string.share_app))) {
            navigateToShareApp(this)
        } else if (from.equals(resources.getString(R.string.rate_on_playstore))) {
            navigateToRateOnPlayStore(this)
        } else if (from.equals(resources.getString(R.string.logout))) {
            logout()
        }

// Admin side handle
        else if (from.equals(resources.getString(R.string.manage_admins))) {
            navController.navigate(R.id.navigation_manage_admin)
        } else if (from.equals(resources.getString(R.string.orders))) {
        } else if (from.equals(resources.getString(R.string.analytics))) {
        } else if (from.equals(resources.getString(R.string.adminsettings))) {
        } else if (from.equals(resources.getString(R.string.close_drawer))) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else if (from.equals(resources.getString(R.string.open_drawer))) {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        } else if (from == getString(R.string.nav_qr_event_standee) && currentDestination != R.id.navigation_standee_intro) {
            navController.navigate(R.id.navigation_standee_intro)
        } else if (from.equals(resources.getString(R.string.qr_event_setup)) && currentDestination != R.id.fragmentMyQrCodes) {
            navController.navigate(R.id.fragmentMyQrCodes)
        } else if (from == getString(R.string.professional_tshirt) && currentDestination != R.id.navigation_start_purchase) {
            navController.navigate(R.id.navigation_start_purchase)
        } else if (from == getString(R.string.new_request) && currentDestination != R.id.navigation_new_requests) {
            navController.navigate(R.id.navigation_new_requests)
        } else if (from == getString(R.string.accepted_request) && currentDestination != R.id.navigation_accepted_requests) {
            navController.navigate(R.id.navigation_accepted_requests)
        } else if (from == getString(R.string.rejected_request) && currentDestination != R.id.navigation_rejected_request) {
            navController.navigate(R.id.navigation_rejected_request)
        } else if (from.equals(resources.getString(R.string.qr_event_orders)) && currentDestination != R.id.navigation_qr_code_event_order_history) {
            navController.navigate(R.id.navigation_qr_code_event_order_history)
        } else if (from == getString(R.string.blocked_req) && currentDestination != R.id.navigation_blocked_request) {
            navController.navigate(R.id.navigation_blocked_request)
        }
    }

    private fun openInformationSection() {
        binding.navDrawer.navInformationView.setOnClickListener {
            if (binding.navDrawer.navInformationSubLayout.isVisible) {
                binding.navDrawer.navInformationSubLayout.gone()
            } else {
                binding.navDrawer.navInformationSubLayout.visible()
            }
        }

        binding.navDrawer.navInfo.setOnClickListener {
            if (binding.navDrawer.navInformationSubLayout.isVisible) {
                binding.navDrawer.navInformationSubLayout.gone()
            } else {
                binding.navDrawer.navInformationSubLayout.visible()
            }
        }
    }

    fun closeDrawers() {
        binding.drawerLayout.closeDrawers()
    }

    fun navigateToDashboard() {
        navController.navigate(R.id.homePageFragment)
    }


    fun navigateToProfile() {
        if (SharedPrefsHelper.read(SharedPrefConstant.LOGIN_TYPE) == Constant.USER_TYPE.USER.name.lowercase(
                Locale.getDefault()
            )
        ) {
            navController.navigate(R.id.userProfileFragment)
        } else {
            navController.navigate(R.id.navigation_photographer_profile)
        }
    }

    fun navigateToLogoutDialog() {
        MaterialAlertDialogBuilder(
            this, R.style.Theme_BottomNavWithSideNav_Dialog_Alert
        ).setTitle(this.resources.getString(R.string.logout))
            .setMessage(this.resources.getString(R.string.would_you_like_to_logout))
            .setPositiveButton(
                this.resources.getString(R.string.logout_ok)
            ) { dialog, which ->
                userLoginViewModel.logout(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))
                binding.drawerLayout.closeDrawers()
            }.setNegativeButton(
                this.resources.getString(R.string.cancel)
            ) { dialog, which -> dialog.cancel() }.show()
    }


    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (navController.currentDestination?.id == R.id.homePageFragment
            || navController.currentDestination?.id == R.id.topPhotographersFragment
            || navController.currentDestination?.id == R.id.topPhotographerServiceFragment
            || navController.currentDestination?.id == R.id.filterPhotographsFragment
        ) {
            binding.viewActionBar.visible()
        } else {
            binding.viewActionBar.gone()
        }
    }

    fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

}