package com.photoshooto.ui.photographersScreens.photographerDashboard

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.photoshooto.R
import com.photoshooto.databinding.ActivityPhotographerNewDashboardBinding
import com.photoshooto.domain.usecase.login.UserLoginViewModel
import com.photoshooto.ui.photographersScreens.photographerAuth.AuthActivity
import com.photoshooto.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class PhotographerNewDashboardActivity : AppCompatActivity(),
    NavController.OnDestinationChangedListener {
    lateinit var binding: ActivityPhotographerNewDashboardBinding
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    private val userLoginViewModel: UserLoginViewModel by viewModel()


    private var mDrawerLayout: DrawerLayout? = null
    private var mDrawerToggle: ActionBarDrawerToggle? = null

    private var isUserVerifiedAfter24Hours = true

    var dialogBuilder: AlertDialog.Builder? = null
    var alertDialog: AlertDialog? = null
    var profileComplete: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BottomNavWithSideNav)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_photographer_new_dashboard)

        initViews()


        mDrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val actionDrawerToggle =
            ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close)
        mDrawerLayout?.addDrawerListener(actionDrawerToggle)
        actionDrawerToggle.syncState()


        mDrawerLayout?.closeDrawer(GravityCompat.START)

        setupDrawer()



        with(userLoginViewModel) {
            userLogoutStatus.observe(this@PhotographerNewDashboardActivity) { response ->
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
        }

        manageSideNavigationClicks()
        Glide.with(this)
            .load(R.drawable.image_india_flag)
            .error(R.drawable.image_india_flag)
            .into(binding.navDrawer.profileImage)
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
            (supportFragmentManager.findFragmentById(R.id.nav_photographer_new_host_fragment) as NavHostFragment)
        navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.nav_photographer_dashboard)
        navController.graph = navGraph

        navController.addOnDestinationChangedListener(this)

        binding.ivSideMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (navController.currentDestination?.id == R.id.photographerNewLandingPageFragment || navController.currentDestination?.id == R.id.allEventsFragment) {
            binding.viewActionBar.visibility = View.VISIBLE
        } else {
            binding.viewActionBar.visibility = View.GONE
        }


    }

    fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
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

    //to handle side bar selection
    private fun selectionMenuItem(item: String) {
        if (item == "dash") {
            binding.navDrawer.navDashboard.setBackgroundResource(R.drawable.checked_uncheked_menu)
            childSelectionMenuItem("")
        } else {
            binding.navDrawer.navDashboard.background = null
//                binding.navDrawer.navDashboard.setBackgroundResource(R.drawable.bluebutton_ripple_rectangle)

        }
        if (item == "event") {
            binding.navDrawer.navQrEvents.setBackgroundResource(R.drawable.checked_uncheked_menu)
            binding.navDrawer.qrEventSubLayout.visibility = View.VISIBLE
            binding.navDrawer.navQrEvents.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.icon_events_img,
                0,
                R.drawable.icon_arrow_down,
                0
            )

        } else {
            binding.navDrawer.navQrEvents.setBackgroundResource(0)
            binding.navDrawer.navQrEvents.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.icon_events_img,
                0,
                R.drawable.right_filled_arrow,
                0
            )
            binding.navDrawer.qrEventSubLayout.visibility = View.GONE
        }
        if (item == "job") {
            binding.navDrawer.navJobs.setBackgroundResource(R.drawable.checked_uncheked_menu)
            binding.navDrawer.navJobs.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.icon_jobs,
                0,
                R.drawable.icon_arrow_down,
                0
            )
            binding.navDrawer.navJobLayout.visibility = View.VISIBLE

        } else {
            binding.navDrawer.navJobs.setBackgroundResource(0)
            binding.navDrawer.navJobs.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.icon_jobs,
                0,
                R.drawable.right_filled_arrow,
                0
            )
            binding.navDrawer.navJobLayout.visibility = View.GONE
        }
        if (item == "profile") {
            binding.navDrawer.navProfile.setBackgroundResource(R.drawable.checked_uncheked_menu)
            binding.navDrawer.navProfile.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.icon_my_profile,
                0,
                R.drawable.icon_arrow_down,
                0
            )
            binding.navDrawer.navProfileLayout.visibility = View.VISIBLE

        } else {
            binding.navDrawer.navProfile.setBackgroundResource(0)
            binding.navDrawer.navProfile.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.icon_my_profile,
                0,
                R.drawable.right_filled_arrow,
                0
            )
            binding.navDrawer.navProfileLayout.visibility = View.GONE

        }
        if (item == "shop") {
            binding.navDrawer.navShop.setBackgroundResource(R.drawable.checked_uncheked_menu)
            binding.navDrawer.navShop.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.icon_shops,
                0,
                R.drawable.icon_arrow_down,
                0
            )
            binding.navDrawer.navShopSubLayout.visibility = View.VISIBLE

        } else {
            binding.navDrawer.navShop.setBackgroundResource(0)
            binding.navDrawer.navShop.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.icon_shops,
                0,
                R.drawable.right_filled_arrow,
                0
            )
            binding.navDrawer.navShopSubLayout.visibility = View.GONE
        }
        if (item == "repair") {
            binding.navDrawer.navRepair.setBackgroundResource(R.drawable.checked_uncheked_menu)
            childSelectionMenuItem("")
        } else {
            binding.navDrawer.navRepair.setBackgroundResource(0)

        }
        if (item == "info") {
            binding.navDrawer.navInfo.setBackgroundResource(R.drawable.checked_uncheked_menu)
            binding.navDrawer.navInfo.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.icon_info_imgs,
                0,
                R.drawable.icon_arrow_down,
                0
            )
            binding.navDrawer.navInformationSubLayout.visibility = View.VISIBLE
        } else {
            binding.navDrawer.navInfo.setBackgroundResource(0)
            binding.navDrawer.navInfo.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.icon_info_imgs,
                0,
                R.drawable.right_filled_arrow,
                0
            )
            binding.navDrawer.navInformationSubLayout.visibility = View.GONE
        }
        if (item == "setting") {
            childSelectionMenuItem("")
            binding.navDrawer.navSetting.setBackgroundResource(R.drawable.checked_uncheked_menu)

        } else {
            binding.navDrawer.navSetting.setBackgroundResource(0)

        }
        if (item == "recycle") {
            childSelectionMenuItem("")
            binding.navDrawer.navRecycleBin.setBackgroundResource(R.drawable.checked_uncheked_menu)

        } else {
            binding.navDrawer.navRecycleBin.setBackgroundResource(0)

        }

        if (item == "logout") {
            childSelectionMenuItem("")
            binding.navDrawer.navLogout.setBackgroundResource(R.drawable.checked_uncheked_menu)

        } else {
            binding.navDrawer.navLogout.setBackgroundResource(0)

        }

    }

    private fun manageSideNavigationClicks() {


        binding.ivNotification.setOnClickListener {
            navController.navigate(R.id.fragmentPhotographerNotifications)
        }


        binding.navDrawer.navDashboard.setOnClickListener {
            selectionMenuItem("dash")
            closeDrawers()
            navigateToDashboard()
        }
        /*binding.navDrawer.icClose.setOnClickListener {
            closeDrawers()
        }*/

        binding.navDrawer.navHeader.setOnClickListener {
            closeDrawers()
//            navigateToProfile()
        }

        /**
         * nav Qr handling
         */
        binding.navDrawer.navQrEvents.setOnClickListener {
            if (binding.navDrawer.qrEventSubLayout.isVisible) {
                binding.navDrawer.qrEventSubLayout.visibility = View.GONE
                binding.navDrawer.navQrEvents.setBackgroundResource(0)
                binding.navDrawer.navQrEvents.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.icon_events_img,
                    0,
                    R.drawable.right_filled_arrow,
                    0
                )

            } else {
                selectionMenuItem("event")
            }
        }

        binding.navDrawer.navJobs.setOnClickListener {
            if (binding.navDrawer.navJobLayout.isVisible) {
                binding.navDrawer.navJobLayout.visibility = View.GONE
                binding.navDrawer.navJobs.setBackgroundResource(0)
                binding.navDrawer.navJobs.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.icon_jobs,
                    0,
                    R.drawable.right_filled_arrow,
                    0
                )

            } else {
                selectionMenuItem("job")
            }
        }

        binding.navDrawer.navInformationView.setOnClickListener {
            if (binding.navDrawer.navInformationSubLayout.isVisible) {
                binding.navDrawer.navInformationSubLayout.visibility = View.GONE

            } else {
                binding.navDrawer.navInformationSubLayout.visibility = View.VISIBLE
            }
        }

        binding.navDrawer.navInfo.setOnClickListener {
            if (binding.navDrawer.navInformationSubLayout.isVisible) {
                binding.navDrawer.navInformationSubLayout.visibility = View.GONE
                binding.navDrawer.navInfo.setBackgroundResource(0)
                binding.navDrawer.navInfo.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.icon_info_imgs,
                    0,
                    R.drawable.right_filled_arrow,
                    0
                )

            } else {
                selectionMenuItem("info")
            }
        }

        binding.navDrawer.navTermsNConditions.setOnClickListener {
            closeDrawers()
            childSelectionMenuItem("terms")
            val bundle = getTermsNConditionBundle()
            navController.navigate(
                R.id.navigation_Terms,
                bundle
            )
        }

        binding.navDrawer.navPrivacyPolicy.setOnClickListener {
            closeDrawers()
            childSelectionMenuItem("privacy")
            val bundle = getPrivacyPolicyBundle()
            navController.navigate(
                R.id.navigation_Terms,
                bundle
            )
        }


        binding.navDrawer.childAllEvents.setOnClickListener {
            if (isUserVerifiedAfter24Hours) {
                navigateToAllEvent()
                childSelectionMenuItem("allEvent")
            }
        }

        binding.navDrawer.navRepair.setOnClickListener {
            if (isUserVerifiedAfter24Hours) {
                closeDrawers()
//                navigateToSurprises()
                selectionMenuItem("repair")
            } else {
                selectionMenuItem("repair")

            }
        }
        binding.navDrawer.navRecycleBin.setOnClickListener {
            if (isUserVerifiedAfter24Hours) {
                selectionMenuItem("recycle")
                closeDrawers()
                navController.navigate(R.id.fragmentRecyclerView)
            } else {

            }
        }

        binding.navDrawer.childQrEventOrder.setOnClickListener {
            if (isUserVerifiedAfter24Hours) {
                closeDrawers()
                childSelectionMenuItem("eventOrder")

//                navigateToMyOrders()
            } else {

            }

        }
        binding.navDrawer.childQrEventSetup.setOnClickListener {
            if (isUserVerifiedAfter24Hours) {
                childSelectionMenuItem("eventSetup")
                navigateToEventSetup()
//                navigateToSurprises()
            } else {

            }
        }

        /**
         * nav shop handling
         */
        binding.navDrawer.navShop.setOnClickListener {
            if (binding.navDrawer.navShopSubLayout.isVisible) {
                binding.navDrawer.navShopSubLayout.visibility = View.GONE
                binding.navDrawer.navShop.setBackgroundResource(0)
                binding.navDrawer.navShop.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.icon_shops,
                    0,
                    R.drawable.right_filled_arrow,
                    0
                )

            } else {
                selectionMenuItem("shop")
            }
        }

        binding.navDrawer.navShopEventStandee.setOnClickListener {
            if (isUserVerifiedAfter24Hours) {
//                closeDrawers()
                //navigateToShopEventStandee()
                //    navigateToSurprises()
            } else {

            }

        }

        binding.navDrawer.navShopProfessionalTshirt.setOnClickListener {
            if (isUserVerifiedAfter24Hours) {
                closeDrawers()
//                navigateToShopProfessionalShirt()
            } else {

            }

        }

        binding.navDrawer.navShopEventAlbumPayment.setOnClickListener {
            if (isUserVerifiedAfter24Hours) {
//                closeDrawers()
//                navigateToSurprises()
                //navigateToShopEventAlbumPayment()
            } else {

            }
        }


        binding.navDrawer.navProfile.setOnClickListener {
            if (binding.navDrawer.navProfileLayout.isVisible) {
                binding.navDrawer.navProfileLayout.visibility = View.GONE
                binding.navDrawer.navProfile.setBackgroundResource(0)
                binding.navDrawer.navProfile.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.icon_my_profile,
                    0,
                    R.drawable.right_filled_arrow,
                    0
                )
            } else {
                selectionMenuItem("profile")
            }
        }

        /* binding.navDrawer.navOrders.setOnClickListener {
             if (isUserVerifiedAfter24Hours) {
                 closeDrawers()
                 navigateToSurprises()
                 //navigateToMyOrders()
             } else {
                 showWaitDialogForVerification()
             }
         }*/

        /* binding.navDrawer.navMyEnquires.setOnClickListener {
             if (isUserVerifiedAfter24Hours) {
                 closeDrawers()
                 navigateToEnquiry()
             } else {
                 showWaitDialogForVerification()
             }
         }*/

        /* binding.navDrawer.navFeedback.setOnClickListener {
             closeDrawers()
             navigateToFeedback()
         }*/

        /* binding.navDrawer.navSurprise.setOnClickListener {
             if (isUserVerifiedAfter24Hours) {
                 closeDrawers()
                 navigateToSurprises()
             } else {
                 showWaitDialogForVerification()
             }
         }*/

        binding.navDrawer.navSetting.setOnClickListener {
            selectionMenuItem("setting")
            /* closeDrawers()
             GetSupportDialog.newInstance("", applicationContext)
                 .show(this.supportFragmentManager, GetSupportDialog.TAG)*/
        }

        /*  binding.navDrawer.navRatePlayStore.setOnClickListener {
              closeDrawers()
              navigateToRateOnPlayStore(this)
          }*/

        /* binding.navDrawer.navShareApp.setOnClickListener {
             closeDrawers()
             navigateToShareApp(this)
         }*/

        binding.navDrawer.navLogout.setOnClickListener {
            closeDrawers()
            selectionMenuItem("logout")
            navigateToLogoutDialog()
        }
    }


    //to handle side bar child item selection
    private fun childSelectionMenuItem(item: String) {
        if (item == "allEvent") {
            binding.navDrawer.childAllEvents.setBackgroundResource(R.drawable.checked_uncheked_menu)
        } else {
            binding.navDrawer.childAllEvents.setBackgroundResource(0)
        }
        if (item == "eventOrder") {
            binding.navDrawer.childQrEventOrder.setBackgroundResource(R.drawable.checked_uncheked_menu)
        } else {
            binding.navDrawer.childQrEventOrder.setBackgroundResource(0)
        }
        if (item == "eventSetup") {
            binding.navDrawer.childQrEventSetup.setBackgroundResource(R.drawable.checked_uncheked_menu)
        } else {
            binding.navDrawer.childQrEventSetup.setBackgroundResource(0)
        }
        if (item == "terms") {
            binding.navDrawer.navTermsNConditions.setBackgroundResource(R.drawable.checked_uncheked_menu)
        } else {
            binding.navDrawer.navTermsNConditions.setBackgroundResource(0)
        }
        if (item == "privacy") {
            binding.navDrawer.navPrivacyPolicy.setBackgroundResource(R.drawable.checked_uncheked_menu)
        } else {
            binding.navDrawer.navPrivacyPolicy.setBackgroundResource(0)
        }
    }

    private fun navigateToAllEvent() {
        closeDrawers()
        navController.navigate(R.id.action_photographerNewLandingPageFragment_to_allEventsFragment)
        binding.welcomeTitle.visibility = View.GONE
        binding.nameTitle.visibility = View.GONE

    }

    private fun navigateToEventSetup() {
        closeDrawers()
        navController.navigate(R.id.action_photographerNewLandingPageFragment_to_setUpQrEventFragment)
    }

    private fun closeDrawers() {
        binding.drawerLayout.closeDrawers()
    }

    private fun navigateToDashboard() {
        navController.navigate(R.id.photographerLandingPageFragment)
    }

    private fun navigateToQrEventStandee() {
//        navController.navigate(R.id.fragmentQRCodeGenerationStandeeIntro)
    }

    private fun navigateToQrEventSetup() {
//        navController.navigate(R.id.fragmentMyQrCodes2)
    }

    private fun navigateToQrEventOrder() {
        //navController.navigate(R.id.navigation_qr_code_event_order_history)
    }

    private fun navigateToShopEventStandee() {

    }

    private fun navigateToShopProfessionalShirt() {
//        navController.navigate(R.id.introTshirtPurchaseFragment)

    }

    private fun navigateToShopEventAlbumPayment() {

    }


    private fun navigateToProfile() {
//        navController.navigate(R.id.fragmentPhotographerProfileDashBoard)
    }

    private fun navigateToMyOrders() {
//        navController.navigate(R.id.fragmentMyQrCodes2)
    }

    private fun navigateToSurprises() {
        val dialog = Dialog(this, R.style.Theme_Dialog)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE) // if you have blue line on top of your dialog, you need use this code
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_unlock_features)
        val btnUnlock = dialog.findViewById(R.id.btnUnlockFeatures) as TextView
        val btnCancel = dialog.findViewById(R.id.btnCancel) as TextView
        btnUnlock.setOnClickListener {


        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun navigateToLogoutDialog() {
        logout()
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

}