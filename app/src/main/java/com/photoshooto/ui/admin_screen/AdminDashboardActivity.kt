package com.photoshooto.ui.admin_screen

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.photoshooto.R
import com.photoshooto.databinding.ActivityAdminDashboardBinding
import com.photoshooto.domain.usecase.login.UserLoginViewModel
import com.photoshooto.domain.usecase.payment_success.PaymentSuccessViewModel
import com.photoshooto.ui.photographersScreens.photographerAuth.AuthActivity
import com.photoshooto.ui.purchase.PaymentSuccessDialog
import com.photoshooto.util.*
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class AdminDashboardActivity : AppCompatActivity(), PaymentResultListener,
    NavController.OnDestinationChangedListener {
    lateinit var binding: ActivityAdminDashboardBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private val userLoginViewModel: UserLoginViewModel by viewModel()
    private val paymentSuccessViewModel: PaymentSuccessViewModel by viewModel()

    private var mDrawerLayout: DrawerLayout? = null
    private var mDrawerToggle: ActionBarDrawerToggle? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BottomNavWithSideNav)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_dashboard)

        initViews()

        val uVal = intent.extras?.getString("id", "")
        val uKey = intent.extras?.getString("btnText", "")

        if (!uKey.isNullOrEmpty() && !uVal.isNullOrEmpty()) {
            val bundle = Bundle()
            bundle.putString("btnText", uKey)
            bundle.putString("id", uVal)
            navController.navigate(R.id.aboutFragment, bundle)
        }


        /*  showToast(action?:"").toString()
          //
          showToast(data.toString())*/

        Checkout.preload(applicationContext)

        with(userLoginViewModel) {
            userLogoutStatus.observe(this@AdminDashboardActivity) { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {
                            SharedPrefsHelper.clearAllPreferences()
                            SharedPrefsHelper.write(SharedPrefConstant.SHOW_ON_BOARDING, true)
                            navigateToAuthScreen()
                        }
                        Status.ERROR -> {
                            response.data?.message?.let { it1 -> showToast(it1) }
                        }
                        else -> {

                        }
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

        manageSideNavigationClicks()
    }


    private fun initViews() {
        navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_admin_host_fragment) as NavHostFragment?)!!
        navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.nav_admin_dashboard)
        navController.graph = navGraph

        navController.addOnDestinationChangedListener(this)

        binding.ivSideMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun manageSideNavigationClicks() {
        /* binding.navDrawer.icClose.setOnClickListener {
             closeDrawers()
         }*/


        /**
         * nav Dashboard
         */

        binding.navDrawer.navDashboardAdmin.setOnClickListener {
            if (binding.navDrawer.handleDashboardLayout.isVisible) {
                binding.navDrawer.handleDashboardLayout.visibility = View.GONE
                binding.navDrawer.navDashboardAdmin.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_admin_orders_img,
                    0,
                    R.drawable.right_filled_arrow,
                    0
                )
            } else {
                binding.navDrawer.handleDashboardLayout.visibility = View.VISIBLE
                binding.navDrawer.navDashboardAdmin.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_admin_orders_img,
                    0,
                    R.drawable.icon_arrow_down,
                    0
                )
            }
        }

        binding.navDrawer.layoutNewRequest.setOnClickListener {
            closeDrawers()
            navigateToNewRequest()
        }

        binding.navDrawer.layoutAcceptedReq.setOnClickListener {
            closeDrawers()
            navigateToAcceptedReq()

        }

        binding.navDrawer.layoutRejectedReq.setOnClickListener {
            closeDrawers()
            navigateToRejectedReq()

        }

        binding.navDrawer.layoutBlockedReq.setOnClickListener {
            closeDrawers()
            navigateToBlockedReq()
        }


        if (SharedPrefsHelper.read(SharedPrefConstant.EMPLOYEE_BY)
                ?.lowercase(Locale.getDefault()) == Constant.EmployeeBy.photoshooto.name.lowercase(
                Locale.getDefault()
            )
        ) {
            binding.navDrawer.tvOrderRequestOrAllOrders.text = getString(R.string.order_all)

        }

        binding.navDrawer.adminOrderClick.setOnClickListener {

            if (binding.navDrawer.handleAdminOrders.visibility == View.VISIBLE) {
                binding.navDrawer.handleAdminOrders.visibility = View.GONE
                binding.navDrawer.adminOrderClick.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_admin_orders_img,
                    0,
                    R.drawable.right_filled_arrow,
                    0
                )
            } else {
                binding.navDrawer.adminOrderClick.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_admin_orders_img,
                    0,
                    R.drawable.icon_arrow_down,
                    0
                )
                binding.navDrawer.handleAdminOrders.visibility = View.VISIBLE
            }
        }

        binding.navDrawer.layoutManageAdmins.setOnClickListener {
            navController.navigate(R.id.navigation_manage_admin)
            closeDrawers()
        }

        binding.navDrawer.layoutAllEmployees.setOnClickListener {
            navController.navigate(R.id.navigation_new_all_employee)
            closeDrawers()
        }

        binding.navDrawer.layoutUsedProductList.setOnClickListener {
            navController.navigate(R.id.usedProductList)
            closeDrawers()
        }

        binding.navDrawer.layoutOrderRequests.setOnClickListener {
            closeDrawers()
            navigateToOrderRequestAdmin()
        }

        binding.navDrawer.layoutOrderStatus.setOnClickListener {
            closeDrawers()
            navigateToOrderStatusAdmin()
        }

        binding.navDrawer.layoutManageJobPositing.setOnClickListener {
            navController.navigate(R.id.manageJobPostingFragment)
            closeDrawers()
        }
//
//        binding.navDrawer.navShopProfessionalTshirt.setOnClickListener {
//            closeDrawers()
//            navigateToShopProfessionalShirt()
//
//        }
//
//        binding.navDrawer.navShopEventAlbumPayment.setOnClickListener {
//            closeDrawers()
//            navigateToShopEventAlbumPayment()
//        }
//
//
//        binding.navDrawer.navProfile.setOnClickListener {
//            closeDrawers()
//            navigateToProfile()
//        }
//
//        binding.navDrawer.navInfo.setOnClickListener {
//            closeDrawers()
//            navigateToRateOnPlayStore()
//        }
//
//        binding.navDrawer.navRatePlayStore.setOnClickListener {
//            closeDrawers()
//            navigateToRateOnPlayStore()
//        }
//
//        binding.navDrawer.navShareApp.setOnClickListener {
//            closeDrawers()
//            navigateToShareApp()
//        }
//
        binding.navDrawer.navLogout.setOnClickListener {
            closeDrawers()
            navigateToLogoutDialog()
        }

    }


    private fun navigateToPsOrdersView() {
        navController.navigate(R.id.navigation_orders)
    }

    private fun navigateToOrderRequestAdmin() {
        if (SharedPrefsHelper.read(SharedPrefConstant.EMPLOYEE_BY)
                ?.lowercase(Locale.getDefault()) == Constant.EmployeeBy.printo.name.lowercase(
                Locale.getDefault()
            )
        ) {
            navController.navigate(R.id.navigation_orders_request)

        } else if (SharedPrefsHelper.read(SharedPrefConstant.EMPLOYEE_BY)
                ?.lowercase(Locale.getDefault()) == Constant.EmployeeBy.photoshooto.name.lowercase(
                Locale.getDefault()
            )
        ) {
            navigateToPsOrdersView()

        }

    }

    private fun navigateToOrderStatusAdmin() {
        navController.navigate(R.id.navigation_orders_status)

    }

    private fun navigateToNewRequest() {
        navController.navigate(R.id.navigation_new_requests)
    }

    private fun navigateToAcceptedReq() {
        navController.navigate(R.id.navigation_accepted_requests)
    }

    private fun navigateToRejectedReq() {
        navController.navigate(R.id.navigation_rejected_request)
    }

    private fun navigateToBlockedReq() {
        navController.navigate(R.id.navigation_blocked_request)
    }


    private fun handleSideNavigationData() {
        setHeaderText(
            binding.navDrawer.tvName,
            SharedPrefsHelper.read(SharedPrefConstant.USER_NAME)
        )
        setHeaderText(binding.navDrawer.tvId, SharedPrefsHelper.read(SharedPrefConstant.USER_ID))
        setHeaderText(
            binding.navDrawer.tvExp,
            "Experience - " + SharedPrefsHelper.readInt(SharedPrefConstant.USER_EXP)
                .toString() + " Yrs"
        )
        setHeaderText(
            binding.navDrawer.tvProfession,
            SharedPrefsHelper.read(SharedPrefConstant.USER_PROFESSION)
        )
        Glide.with(this)
            .load(urlAddingForPicture(SharedPrefConstant.PROFILE_URL))
            .error(R.drawable.splas_logo_ps)
            .into(binding.navDrawer.profileImage)
    }

    private fun setHeaderText(textView: TextView, value: String?) {
        if (value.isNullOrEmpty()) {
            textView.visibility = View.GONE
        } else {
            textView.visibility = View.VISIBLE
            textView.text = value
        }
    }

    private fun navigateToAuthScreen() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.apply {
            startActivity(this)
            finish()
        }
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

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    /* override fun onSupportNavigateUp(): Boolean {
         return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
     }*/

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
//             val navHostFragment =
//                supportFragmentManager.findFragmentById(R.id.nav_admin_host_fragment) as NavHostFragment
//            val navController = navHostFragment.navController
//
//            if (navController.currentDestination?.id == R.id.photographerLandingPageFragment) {
//                ExitAlertDialog.newInstance("DashBoardScreenActivity", applicationContext)
//                    .show(supportFragmentManager, ExitAlertDialog.TAG)
//            } else {
//                super.onBackPressed()
//            }
            super.onBackPressed()
        }
    }

    private fun logout() {
        MaterialAlertDialogBuilder(
            this, R.style.Theme_BottomNavWithSideNav_Dialog_Alert
        ).setTitle(this.resources.getString(R.string.logout))
            .setMessage(this.resources.getString(R.string.would_you_like_to_logout))
            .setPositiveButton(
                this.resources.getString(R.string.logout_ok)
            ) { dialog, which ->
                binding.drawerLayout.closeDrawers()
                userLoginViewModel.logout(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))
            }.setNegativeButton(
                this.resources.getString(R.string.cancel)
            ) { dialog, which -> dialog.cancel() }.show()
    }

    private fun shareMode() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    fun closeApp() {
        finish()
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

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (mDrawerToggle!!.onOptionsItemSelected(item)) {
            true
        } else item.onNavDestinationSelected(navHostFragment.navController) || super.onOptionsItemSelected(
            item
        )
    }*/


    private fun closeDrawers() {
        binding.drawerLayout.closeDrawers()
    }

    private fun navigateToDashboard() {
        navController.navigate(R.id.photographerLandingPageFragment)
    }

    private fun navigateToQrEventStandee() {
//        if (navController.currentDestination?.id != R.id.photographerLandingPageFragment) {
//            navController.popBackStack()
//        }
        navController.navigate(R.id.action_photographerLandingPageFragment_to_fragmentQRCodeGenerationStandeeIntro)
    }

    private fun navigateToQrEventSetup() {
        //navController.navigate(R.id.fragmentMyQrCodes)
    }

    private fun navigateToQrEventOrder() {
        //navController.navigate(R.id.navigation_qr_code_event_order_history)
    }

    private fun navigateToShopEventStandee() {

    }

    private fun navigateToShopProfessionalShirt() {
//        if (navController.currentDestination?.id != R.id.photographerLandingPageFragment) {
//            navController.navigateUp()
//            navController.popBackStack()
//        }
        navController.navigate(R.id.action_photographerLandingPageFragment_to_introTshirtPurchaseFragment)
    }

    private fun navigateToShopEventAlbumPayment() {

    }


    private fun navigateToProfile() {
        /*   if (SharedPrefsHelper.read(SharedPrefConstant.LOGIN_TYPE) == Constant.USER_TYPE.USER.name.toLowerCase()) {
               navController.navigate(R.id.navigation_profile)
           } else {*/
//        if (navController.currentDestination?.id != R.id.photographerLandingPageFragment) {
//            //navController.navigateUp()
//            navController.popBackStack()
//        }
        navController.navigate(R.id.action_photographerLandingPageFragment_to_fragmentPhotographerProfileDashBoard)

    }

    private fun navigateToRateOnPlayStore() {
        try {
            startActivity(
                Intent(
                    "android.intent.action.VIEW", Uri.parse("market://details?id=$packageName")
                )
            )
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    "android.intent.action.VIEW",
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }

    private fun navigateToShareApp() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun navigateToLogoutDialog() {
        MaterialAlertDialogBuilder(
            this, R.style.Theme_BottomNavWithSideNav_Dialog_Alert
        ).setTitle(this.resources.getString(R.string.logout))
            .setMessage(this.resources.getString(R.string.would_you_like_to_logout))
            .setPositiveButton(
                this.resources.getString(R.string.logout_ok)
            ) { dialog, which ->
                binding.drawerLayout.closeDrawers()
                userLoginViewModel.logout(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))
            }.setNegativeButton(
                this.resources.getString(R.string.cancel)
            ) { dialog, which -> dialog.cancel() }.show()
    }

    override fun onPaymentSuccess(p0: String?) {
        //navController.navigate(R.id.landingPageFragment)
        //Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show()
        showPaymentSuccessDialog()

    }

    fun initiateRazorPay(totalAmount: Double) {


        try {
            val activity: Activity = this
            val checkout = Checkout()
            checkout.setKeyID(Constant.RAZOR_PAY_ID)
            checkout.setPublicKey(Constant.RAZOR_PAY_ID)
            checkout.setImage(R.drawable.splash_logo)
            val options = JSONObject()
            options.put("name", getString(R.string.app_name))
            options.put("currency", "INR")
            //options.put("order_id", data?.id)
            options.put("amount", totalAmount.times(100))

            val jsonObject = JSONObject()
            jsonObject.put("email", SharedPrefsHelper.read(SharedPrefConstant.USER_EMAIL))
            jsonObject.put("contact", SharedPrefsHelper.read(SharedPrefConstant.MOBILE_NUMBER))
            options.put("prefill", jsonObject)

            checkout.open(activity, options)

        } catch (e: Exception) {
            onToast("Error in payment: " + e.message, this)
            e.printStackTrace()
        }
    }

    private fun showPaymentSuccessDialog() {
        Handler(Looper.getMainLooper()).postDelayed({
            PaymentSuccessDialog.newInstance("")
                .show(supportFragmentManager, PaymentSuccessDialog.TAG)
        }, 500)

    }

    override fun onPaymentError(p0: Int, p1: String?) {
        showToast("Payment failed")
    }

    fun drawerOpen() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (navController.currentDestination?.id == R.id.photographerLandingPageFragment) {
            binding.viewActionBar.visibility = View.VISIBLE
        } else {
            binding.viewActionBar.visibility = View.GONE
        }
    }


}