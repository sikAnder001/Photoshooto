package com.photoshooto.ui.photographersScreens.photographerDashboard

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
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
import com.photoshooto.databinding.ActivityPhotographerDashboardBinding
import com.photoshooto.domain.model.AddressElement
import com.photoshooto.domain.usecase.login.UserLoginViewModel
import com.photoshooto.domain.usecase.orders.OrdersViewModel
import com.photoshooto.domain.usecase.payment_success.PaymentSuccessViewModel
import com.photoshooto.domain.usecase.profile.GetUserProfileViewModel
import com.photoshooto.ui.dashboard.ui.home.ExitAlertDialog
import com.photoshooto.ui.login.GetSupportDialog
import com.photoshooto.ui.login.StayTunedDialog
import com.photoshooto.ui.photographersScreens.photographerAuth.AuthActivity
import com.photoshooto.ui.purchase.PaymentSuccessDialog
import com.photoshooto.ui.qrcodegenration.StandeeDetailsAddedDialog
import com.photoshooto.util.*
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.ParseException
import java.util.*

class PhotographerDashboardActivity : AppCompatActivity(), PaymentResultListener,
    NavController.OnDestinationChangedListener {
    lateinit var binding: ActivityPhotographerDashboardBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private val userLoginViewModel: UserLoginViewModel by viewModel()
    private val profileViewModel: GetUserProfileViewModel by viewModel()
    private val orderViewModel: OrdersViewModel by viewModel()

    private val paymentSuccessViewModel: PaymentSuccessViewModel by viewModel()

    private var mDrawerLayout: DrawerLayout? = null
    private var mDrawerToggle: ActionBarDrawerToggle? = null
    private var isUserVerifiedAfter24Hours = true

    var dialogBuilder: AlertDialog.Builder? = null
    var alertDialog: AlertDialog? = null
    var profileComplete: Int? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BottomNavWithSideNav)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_photographer_dashboard)

        initViews()

        val uVal = intent.extras?.getString("id", "")
        if (!uVal.isNullOrEmpty()) {
            var bundle = Bundle()
            bundle.putString("jobId", uVal)
            navController.navigate(R.id.action_photographerLandingPageFragment_to_jobDetail, bundle)
        }

        profileViewModel.getProfileByIDUseCase(
            SharedPrefsHelper.read(SharedPrefConstant.USER_ID),
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
        )

        Checkout.preload(applicationContext)

        with(userLoginViewModel) {
            userLogoutStatus.observe(this@PhotographerDashboardActivity) { response ->
                if (response != null) {
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

            with(profileViewModel) {
                getUserData.observe(this@PhotographerDashboardActivity) { response ->
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
                                    resetToSplashScreen(this@PhotographerDashboardActivity)
                                } else {
                                    response.data?.message?.let { it1 -> showToast(it1) }
                                }
                            }
                            else -> {}
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

        getDateDiff()
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


    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    fun getDateDiff() {
        val format = "yyyy-MM-dd'T'HH:mm:ss"
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        val date = SharedPrefsHelper.read(SharedPrefConstant.USER_REGISTERED_ON)
        val registerDate = uTCToLocal(format, format, date)
        val userDob: Date = dateFormat.parse(registerDate)

        val today = Date()
        val diff: Long = today.time - userDob.time
        val hours = (diff / (1000 * 60 * 60)).toInt()
        val status = SharedPrefsHelper.read(SharedPrefConstant.USER_STATUS)
        if (hours < 24 && status != "accept") {
            isUserVerifiedAfter24Hours = false
//            binding.thanksForRegistration.thanksForRegistrationRoot.visibility = View.VISIBLE
            navController.navigate(R.id.action_photographerLandingPageFragment_to_fragmentThanksRegister)
            disableUnverifiedDrawerOptions()
//            AccountVerifiedDialog.newInstance(
//            ).show(supportFragmentManager, StandeeDetailsAddedDialog.TAG)
        } else {
            val showSpecialOfferDialog =
                SharedPrefsHelper.readBoolean(SharedPrefConstant.SPECIAL_OFFER_SHOWN)
            if (!showSpecialOfferDialog) {
                Handler(Looper.getMainLooper()).postDelayed({
                    showSpecialOfferDialog()
                }, 4000)

            }
        }

        manageSideNavigationClicks()

    }

    fun disableUnverifiedDrawerOptions() {
        setWaitingView(binding.navDrawer.navDashboard)
        setWaitingView(binding.navDrawer.navQrEvents)
        setWaitingView(binding.navDrawer.navShop)
        setWaitingView(binding.navDrawer.navJobPosting)
        setWaitingView(binding.navDrawer.navOrders)
        setWaitingView(binding.navDrawer.navMyEnquires)
        setWaitingView(binding.navDrawer.navSurprise)
        setWaitingView(binding.navDrawer.navFeedback)
        setWaitingView(binding.navDrawer.navRecycleBin)
    }

    fun setWaitingView(view: View) {
        view.alpha = 0.3f
        view.setOnClickListener {
            StayTunedDialog.newInstance(
            ).show(supportFragmentManager, StandeeDetailsAddedDialog.TAG)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun uTCToLocal(
        dateFormatInPut: String?, dateFomratOutPut: String?, datesToConvert: String?
    ): String? {
        var dateToReturn = datesToConvert
        val sdf = SimpleDateFormat(dateFormatInPut)
        sdf.timeZone = android.icu.util.TimeZone.getTimeZone("UTC")
        var gmt: Date? = null
        val sdfOutPutToSend = SimpleDateFormat(dateFomratOutPut)
        sdfOutPutToSend.timeZone = android.icu.util.TimeZone.getDefault()
        try {
            gmt = sdf.parse(datesToConvert)
            dateToReturn = sdfOutPutToSend.format(gmt)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return dateToReturn
    }

    private fun showSpecialOfferDialog() {
        /* val dialog = Dialog(this, R.style.Theme_Dialog)
         dialog.window?.requestFeature(Window.FEATURE_NO_TITLE) // if you have blue line on top of your dialog, you need use this code
         dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         dialog.setCancelable(true)
         dialog.setContentView(R.layout.special_offer_dialog_view)
         var okayBtn = dialog.findViewById(R.id.tvOrderNow) as TextView
         okayBtn.setOnClickListener {
             dialog.dismiss()
             navController.navigate(R.id.action_photographerLandingPageFragment_to_fragmentQRCodeGenerationStandeeIntro)
         }
         dialog.show()*/

        dialogBuilder = AlertDialog.Builder(this)
        val dialogView: View = layoutInflater.inflate(R.layout.special_offer_dialog_view, null)
        dialogBuilder?.setView(dialogView)
        dialogBuilder?.setCancelable(true)

        val tvOrderNow = dialogView.findViewById<TextView>(R.id.tvOrderNow)
        val imgBack = dialogView.findViewById<ImageView>(R.id.imgBack)

        alertDialog = dialogBuilder?.create()
        alertDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        alertDialog?.show()

        tvOrderNow.text = getString(R.string.coming_soon)
        tvOrderNow.setOnClickListener {
            alertDialog?.dismiss()
            /*lifecycleScope.launchWhenResumed {
                navController.navigate(R.id.fragmentQRCodeGenerationStandeeIntro)
            }*/
        }

        imgBack.setOnClickListener { v: View? -> alertDialog?.dismiss() }
    }


    private fun initViews() {
        navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_photographer_host_fragment) as NavHostFragment)
        navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.nav_photographer_dashboard)
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


    private fun manageSideNavigationClicks() {

        binding.ivCart.setOnClickListener {
            navController.navigate(R.id.fragmentMyCart)
            //navController.navigate(R.id.action_photographerLandingPageFragment_to_fragmentMyCart)
        }

        binding.ivNotification.setOnClickListener {
            navController.navigate(R.id.fragmentPhotographerNotifications)
        }


        binding.navDrawer.navDashboard.setOnClickListener {
            if (isUserVerifiedAfter24Hours) {
                closeDrawers()
                navigateToDashboard()
            } else {
                showWaitDialogForVerification()
            }
        }
        binding.navDrawer.icClose.setOnClickListener {
            closeDrawers()
        }

        binding.navDrawer.navHeader.setOnClickListener {
            navigateToProfile()
        }

        /**
         * nav Qr handling
         */
        binding.navDrawer.navQrEvents.setOnClickListener {
            if (binding.navDrawer.qrEventSubLayout.isVisible) {
                binding.navDrawer.qrEventSubLayout.visibility = View.GONE
            } else {
                binding.navDrawer.qrEventSubLayout.visibility = View.VISIBLE
            }
        }

        binding.navDrawer.navInformationView.setOnClickListener {
            if (binding.navDrawer.navInformationSubLayout.isVisible) {
                binding.navDrawer.navInformationSubLayout.visibility = View.GONE
            } else {
                binding.navDrawer.navInformationSubLayout.visibility = View.VISIBLE
            }
        }

        binding.navDrawer.navJobPosting.setOnClickListener {
            if (binding.navDrawer.navJobPostingSubLayout.isVisible) {
                binding.navDrawer.navJobPostingSubLayout.visibility = View.GONE
            } else {
                binding.navDrawer.navJobPostingSubLayout.visibility = View.VISIBLE
            }
        }

        binding.navDrawer.navInfo.setOnClickListener {
            if (binding.navDrawer.navInformationSubLayout.isVisible) {
                binding.navDrawer.navInformationSubLayout.visibility = View.GONE
            } else {
                binding.navDrawer.navInformationSubLayout.visibility = View.VISIBLE
            }
        }

        binding.navDrawer.navMyJobs.setOnClickListener {
            closeDrawers()
            navController.navigate(
                R.id.jobHome
            )
        }

        binding.navDrawer.navMyCalender.setOnClickListener {
            closeDrawers()
            val args = Bundle().apply {
                putString("fromDash", "yes")
                putString("userId", SharedPrefsHelper.read(SharedPrefConstant.USER_ID))
                putString("userName", SharedPrefsHelper.read(SharedPrefConstant.USER_NAME))
                putString("userAddress", SharedPrefsHelper.getUserCommon()?.location?.city)
                putString(
                    "userProfile",
                    "${DOMAIN}" + SharedPrefsHelper.getUserCommon()?.profile_details?.profile_image
                )
                putBoolean(
                    "userSubscribe", SharedPrefsHelper.getSubscribed()
                )
            }
            navController.navigate(
                R.id.calendarFragment,
                args
            )
        }

        binding.navDrawer.navSavedPosts.setOnClickListener {
            closeDrawers()
            navController.navigate(R.id.savedJobFragment)
        }

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

        binding.navDrawer.navQrEventStandee.setOnClickListener {
            if (isUserVerifiedAfter24Hours) {
                closeDrawers()
                navigateToSurprises()
                //navigateToQrEventStandee()
            } else {
                showWaitDialogForVerification()
            }
        }

        binding.navDrawer.navQrEventStandee.setOnClickListener {
            if (isUserVerifiedAfter24Hours) {
                closeDrawers()
                navigateToSurprises()
                //navigateToQrEventStandee()
            } else {
                showWaitDialogForVerification()
            }
        }

        binding.navDrawer.navAr.setOnClickListener {
            if (isUserVerifiedAfter24Hours) {
                closeDrawers()
                navigateToSurprises()
            } else {
                showWaitDialogForVerification()
            }
        }
        binding.navDrawer.navRecycleBin.setOnClickListener {
            if (isUserVerifiedAfter24Hours) {
                closeDrawers()
                navController.navigate(R.id.fragmentRecyclerView)
            } else {
                showWaitDialogForVerification()
            }
        }

        binding.navDrawer.navQrEventOrder.setOnClickListener {
            if (isUserVerifiedAfter24Hours) {
                closeDrawers()
                //navigateToMyOrders()
                navigateToSurprises()
            } else {
                showWaitDialogForVerification()
            }

        }
        binding.navDrawer.navQrEventSetup.setOnClickListener {
            if (isUserVerifiedAfter24Hours) {
                closeDrawers()
                //navigateToQrEventSetup()
                navigateToSurprises()
            } else {
                showWaitDialogForVerification()
            }
        }

        /**
         * nav shop handling
         */
        binding.navDrawer.navShop.setOnClickListener {
            if (binding.navDrawer.navShopSubLayout.isVisible) {
                binding.navDrawer.navShopSubLayout.visibility = View.GONE
            } else {
                binding.navDrawer.navShopSubLayout.visibility = View.VISIBLE
            }
        }

        binding.navDrawer.navShopEventStandee.setOnClickListener {
            if (isUserVerifiedAfter24Hours) {
                closeDrawers()
                //navigateToShopEventStandee()
                navigateToSurprises()
            } else {
                showWaitDialogForVerification()
            }

        }

        binding.navDrawer.navShopProfessionalTshirt.setOnClickListener {
            if (isUserVerifiedAfter24Hours) {
                closeDrawers()
                navigateToSurprises()
                //navigateToShopProfessionalShirt()
            } else {
                showWaitDialogForVerification()
            }

        }

        binding.navDrawer.navShopEventAlbumPayment.setOnClickListener {
            if (isUserVerifiedAfter24Hours) {
                closeDrawers()
                navigateToSurprises()
                //navigateToShopEventAlbumPayment()
            } else {
                showWaitDialogForVerification()
            }
        }


        binding.navDrawer.navProfile.setOnClickListener {
            closeDrawers()
            navigateToProfile()
        }

        binding.navDrawer.navOrders.setOnClickListener {
            if (isUserVerifiedAfter24Hours) {
                closeDrawers()
                navigateToSurprises()
                //navigateToMyOrders()
            } else {
                showWaitDialogForVerification()
            }
        }

        binding.navDrawer.navMyEnquires.setOnClickListener {
            if (isUserVerifiedAfter24Hours) {
                closeDrawers()
                navigateToEnquiry()
            } else {
                showWaitDialogForVerification()
            }
        }

        binding.navDrawer.navFeedback.setOnClickListener {
            closeDrawers()
            navigateToFeedback()
        }

        binding.navDrawer.navSurprise.setOnClickListener {
            if (isUserVerifiedAfter24Hours) {
                closeDrawers()
                navigateToSurprises()
            } else {
                showWaitDialogForVerification()
            }
        }

        binding.navDrawer.navSupport.setOnClickListener {
            closeDrawers()
            GetSupportDialog.newInstance("", applicationContext)
                .show(this.supportFragmentManager, GetSupportDialog.TAG)
        }

        binding.navDrawer.navRatePlayStore.setOnClickListener {
            closeDrawers()
            navigateToRateOnPlayStore(this)
        }

        binding.navDrawer.navShareApp.setOnClickListener {
            closeDrawers()
            navigateToShareApp(this)
        }

        binding.navDrawer.navLogout.setOnClickListener {
            closeDrawers()
            navigateToLogoutDialog()
        }

        if (!isUserVerifiedAfter24Hours) {
            disableUnverifiedDrawerOptions()
        }
    }

    private fun navigateToEnquiry() {
        navController.navigate(R.id.fragmentPhotographerEnquiry)
    }

    private fun navigateToFeedback() {
        navController.navigate(R.id.fragmentPhotographerFeedback)
    }

    private fun showWaitDialogForVerification() {
        StayTunedDialog.newInstance().show(supportFragmentManager, StayTunedDialog.TAG)
    }

    private fun handleSideNavigationData() {
        setHeaderText(
            binding.navDrawer.tvName, SharedPrefsHelper.read(SharedPrefConstant.USER_NAME)
        )
        setHeaderText(binding.navDrawer.tvId, SharedPrefsHelper.read(SharedPrefConstant.USER_ID))
        setHeaderText(
            binding.navDrawer.tvExp,
            "Experience - " + SharedPrefsHelper.readInt(SharedPrefConstant.USER_EXP) + " Yrs"
        )
        setHeaderText(
            binding.navDrawer.tvProfession,
            SharedPrefsHelper.read(SharedPrefConstant.USER_PROFESSION)
        )
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

        manageSideNavigationClicks()
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
            this, mDrawerLayout, R.string.open, R.string.close
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


    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            /*val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_photographer_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController*/

            if (navController.currentDestination?.id == R.id.photographerLandingPageFragment || navController.currentDestination?.id == R.id.fragmentThanksRegister) {
                ExitAlertDialog.newInstance("PhotographerDashboardActivity", applicationContext)
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
            ) { _, _ ->
                userLoginViewModel.logout(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))
                binding.drawerLayout.closeDrawers()
            }.setNegativeButton(
                this.resources.getString(R.string.cancel)
            ) { dialog, _ -> dialog.cancel() }.show()
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
        navController.navigate(R.id.fragmentQRCodeGenerationStandeeIntro)
    }

    private fun navigateToQrEventSetup() {
        navController.navigate(R.id.fragmentMyQrCodes2)
    }

    private fun navigateToQrEventOrder() {
        //navController.navigate(R.id.navigation_qr_code_event_order_history)
    }

    private fun navigateToShopEventStandee() {

    }

    private fun navigateToShopProfessionalShirt() {
        navController.navigate(R.id.introTshirtPurchaseFragment)

    }

    private fun navigateToShopEventAlbumPayment() {

    }


    fun navigateToProfile() {
        closeDrawers()
        navController.navigate(R.id.fragmentPhotographerProfileDashBoard)
    }

    private fun navigateToMyOrders() {
        navController.navigate(R.id.fragmentMyQrCodes2)
    }

    private fun navigateToSurprises() {
        val dialog = Dialog(this, R.style.Theme_Dialog)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE) // if you have blue line on top of your dialog, you need use this code
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_unlock_features)
        val btnUnlock = dialog.findViewById(R.id.btnUnlockFeatures) as TextView
        val btnCancel = dialog.findViewById(R.id.btnCancel) as TextView
        btnUnlock.text = getString(R.string.coming_soon)
        btnUnlock.setOnClickListener {
            /*orderViewModel.getOrderRequestList("standee", 10, 1)
            initObserveOrderList(dialog)*/
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun initObserveOrderList(dialog: Dialog) {
        with(orderViewModel) {
            ordersLiveData.observe(this@PhotographerDashboardActivity) { ordersLiveData ->
                ordersLiveData.success.let {
                    if (it) {
                        ordersLiveData.data?.list?.let { orderList ->
                            dialog.dismiss()
                            navigateToQrEventStandee()
                            /* if (orderList.isNullOrEmpty()) {
                                 navigateToQrEventStandee()
                             } else {
                                 navController.navigate(R.id.photographerRedeemGiftFragment)
                             }*/
                        }
                    } else {
                        dialog.dismiss()
                        onToast(ordersLiveData.message, this@PhotographerDashboardActivity)
                    }
                }
            }
        }

    }

    private fun navigateToLogoutDialog() {
        logout()
    }

    override fun onPaymentSuccess(p0: String?) {
        Log.i("transactionId", " $p0  ")
        var transactionID = "$p0"
        showPaymentSuccessDialog(transactionID)

    }

    fun initiateRazorPay(
        totalAmount: Double, addressElement: AddressElement, paymentFor: String, orderID: String
    ) {

        paymentSuccessViewModel.paymentRequest.order_id = orderID
        paymentSuccessViewModel.paymentRequest.amount = "$totalAmount"
        paymentSuccessViewModel.paymentRequest.payment_for = paymentFor
        paymentSuccessViewModel.paymentRequest.billing_info?.name =
            SharedPrefsHelper.read(SharedPrefConstant.USER_NAME).toString()
        paymentSuccessViewModel.paymentRequest.billing_info?.address = addressElement.address!!
        paymentSuccessViewModel.paymentRequest.billing_info?.city = addressElement.city!!
        paymentSuccessViewModel.paymentRequest.billing_info?.state = addressElement.state!!
        paymentSuccessViewModel.paymentRequest.billing_info?.landmark = addressElement.landmark!!
        paymentSuccessViewModel.paymentRequest.billing_info?.pincode = addressElement.pincode!!

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
            options.put("amount", /*100 * 50*/totalAmount.times(100))

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

    private fun showPaymentSuccessDialog(paymentId: String) {
        SharedPrefsHelper.write(SharedPrefConstant.SPECIAL_OFFER_SHOWN, true)
        paymentSuccessViewModel.paymentRequest.transaction_id = paymentId
        paymentSuccessViewModel.paymentRequest.status = SUCCESS
        paymentSuccessViewModel.initiatePaymentSuccess()
        Handler(Looper.getMainLooper()).postDelayed({
            PaymentSuccessDialog.newInstance(paymentSuccessViewModel.paymentRequest.order_id)
                .show(supportFragmentManager, PaymentSuccessDialog.TAG)
            if (paymentSuccessViewModel.paymentRequest.payment_for == STANDEE) {
                navController.navigate(R.id.photographerLandingPageFragment)
            } else {
                //navController.navigate(R.id.action_cartFragment_to_photographerLandingPageFragment)
                navController.navigate(R.id.photographerLandingPageFragment)
            }

        }, 500)

    }

    override fun onPaymentError(p0: Int, p1: String?) {
        showToast("Payment failed")
        paymentSuccessViewModel.paymentRequest.transaction_id = p1!!
        paymentSuccessViewModel.paymentRequest.status = FAIL
        paymentSuccessViewModel.initiatePaymentSuccess()
    }

    override fun onDestinationChanged(
        controller: NavController, destination: NavDestination, arguments: Bundle?
    ) {
        if (navController.currentDestination?.id == R.id.photographerLandingPageFragment || navController.currentDestination?.id == R.id.filterPhotographsFragment || navController.currentDestination?.id == R.id.fragmentQRCodeGenerationStandeeIntro || navController.currentDestination?.id == R.id.introTshirtPurchaseFragment) {
            binding.viewActionBar.visibility = View.VISIBLE
        } else {
            binding.viewActionBar.visibility = View.GONE
        }
    }

    fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }


}