package com.photoshooto.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.photoshooto.databinding.MainNavigationViewBinding
import com.photoshooto.util.Constant
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import java.util.*

class FragmentSideNavigation : Fragment() {

    private var _binding: MainNavigationViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainNavigationViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * set header Text
         */

        setHeaderText(binding.tvName, SharedPrefsHelper.read(SharedPrefConstant.USER_NAME))
        setHeaderText(binding.tvId, SharedPrefsHelper.read(SharedPrefConstant.USER_ID))
        //setHeaderText(binding.tvExp, SharedPrefsHelper.readInt(SharedPrefConstant.USER_EXP).toString())
        //setHeaderText(binding.tvProfession, SharedPrefsHelper.read(SharedPrefConstant.USER_PROFESSION))

        /**
         * handle click listener
         */

        binding.icClose.setOnClickListener {
            (activity as DashBoardScreenActivity).closeDrawers()
        }

        binding.navDashboard.setOnClickListener {
            (activity as DashBoardScreenActivity).closeDrawers()
            (activity as DashBoardScreenActivity).navigateToDashboard()
        }

        /* IS Client (Photo & User), Super Admin or Admin flow*/

        if (SharedPrefsHelper.read(SharedPrefConstant.USER_ROLE) == Constant.USER_ROLE.CLIENT.name.lowercase(
                Locale.getDefault()
            )
        ) {
            //binding.superadminSideLayout.visibility = View.GONE
            binding.photographerSideLayout.visibility = View.VISIBLE
        } else if (SharedPrefsHelper.read(SharedPrefConstant.USER_ROLE) == Constant.USER_ROLE.SUPERADMIN.name.lowercase(
                Locale.getDefault()
            )
        ) {
            //  binding.superadminSideLayout.visibility = View.VISIBLE
            //binding.layoutPSEmployees.visibility = View.VISIBLE
            //binding.layoutPintoEmployees.visibility = View.VISIBLE
            binding.photographerSideLayout.visibility = View.GONE
        } else if (SharedPrefsHelper.read(SharedPrefConstant.USER_ROLE) == Constant.USER_ROLE.ADMIN.name.lowercase(
                Locale.getDefault()
            )
        ) {
            if (SharedPrefsHelper.read(SharedPrefConstant.EMPLOYEE_BY) == Constant.EmployeeBy.printo.name.lowercase(
                    Locale.getDefault()
                )
            ) {
                //binding.superadminSideLayout.visibility = View.VISIBLE
                //binding.handlePSEmployeesLayout.visibility = View.VISIBLE
                //binding.layoutPSEmployees.visibility = View.GONE
                //binding.layoutPintoEmployees.visibility = View.VISIBLE
                binding.photographerSideLayout.visibility = View.GONE
            } else if (SharedPrefsHelper.read(SharedPrefConstant.EMPLOYEE_BY) == Constant.EmployeeBy.photoshooto.name.lowercase(
                    Locale.getDefault()
                )
            ) {
                //binding.superadminSideLayout.visibility = View.VISIBLE
                //binding.handlePSEmployeesLayout.visibility = View.VISIBLE
                //binding.layoutPSEmployees.visibility = View.VISIBLE
                //binding.layoutPintoEmployees.visibility = View.GONE
                binding.photographerSideLayout.visibility = View.GONE
            }
        }

        /**
         * nav Qr handling
         */

        /*binding.navQrEvents.setOnClickListener {
            if (binding.qrEventSubLayout.isVisible) {
                binding.qrEventSubLayout.visibility = View.GONE
            } else {
                binding.qrEventSubLayout.visibility = View.VISIBLE
            }
        }
*/
        binding.navQrEventStandee.setOnClickListener {
            (activity as DashBoardScreenActivity).closeDrawers()
            //(activity as DashBoardScreenActivity).navigateToQrEventStandee()

        }

        binding.navQrEventSetup.setOnClickListener {
            (activity as DashBoardScreenActivity).closeDrawers()
            //(activity as DashBoardScreenActivity).navigateToQrEventSetup()

        }

        binding.navQrEventOrder.setOnClickListener {
            (activity as DashBoardScreenActivity).closeDrawers()
            //(activity as DashBoardScreenActivity).navigateToQrEventOrder()
        }


        /**
         * nav shop handling
         */

        /*binding.navShop.setOnClickListener {
            if (binding.navShopSubLayout.isVisible) {
                binding.navShopSubLayout.visibility = View.GONE
            } else {
                binding.navShopSubLayout.visibility = View.VISIBLE
            }
        }*/

        binding.navShopEventStandee.setOnClickListener {
            (activity as DashBoardScreenActivity).closeDrawers()
            //(activity as DashBoardScreenActivity).navigateToShopEventStandee()

        }

        binding.navShopProfessionalTshirt.setOnClickListener {
            (activity as DashBoardScreenActivity).closeDrawers()
            //(activity as DashBoardScreenActivity).navigateToShopProfessionalShirt()

        }

        binding.navShopEventAlbumPayment.setOnClickListener {
            (activity as DashBoardScreenActivity).closeDrawers()
            //(activity as DashBoardScreenActivity).navigateToShopEventAlbumPayment()
        }


        binding.navProfile.setOnClickListener {
            (activity as DashBoardScreenActivity).closeDrawers()
            (activity as DashBoardScreenActivity).navigateToProfile()
        }

        binding.navInfo.setOnClickListener {
            (activity as DashBoardScreenActivity).closeDrawers()
            com.photoshooto.util.navigateToRateOnPlayStore(activity as DashBoardScreenActivity)
        }

        binding.navRatePlayStore.setOnClickListener {
            (activity as DashBoardScreenActivity).closeDrawers()
            com.photoshooto.util.navigateToRateOnPlayStore(activity as DashBoardScreenActivity)
        }

        binding.navShareApp.setOnClickListener {
            (activity as DashBoardScreenActivity).closeDrawers()
            com.photoshooto.util.navigateToShareApp(activity as DashBoardScreenActivity)
        }

        binding.navLogout.setOnClickListener {
            (activity as DashBoardScreenActivity).closeDrawers()
            (activity as DashBoardScreenActivity).navigateToLogoutDialog()
        }


        /*Super Admin Handling */

        /*   binding.navDashboardAdmin.setOnClickListener {

               if (binding.handleDashboardLayout.isVisible) {
                   binding.handleDashboardLayout.visibility = View.GONE
               } else {
                   binding.handleDashboardLayout.visibility = View.VISIBLE
               }
           }*/

        /* binding.layoutNewRequest.setOnClickListener {
             (activity as DashBoardScreenActivity).closeDrawers()
             (activity as DashBoardScreenActivity).navigateToNewRequest()
         }

         binding.layoutAcceptedReq.setOnClickListener {
             (activity as DashBoardScreenActivity).closeDrawers()
             (activity as DashBoardScreenActivity).navigateToAcceptedReq()
         }

         binding.layoutRejectedReq.setOnClickListener {
             (activity as DashBoardScreenActivity).closeDrawers()
             (activity as DashBoardScreenActivity).navigateToRejectedReq()
         }

         binding.layoutBlockedReq.setOnClickListener {
             (activity as DashBoardScreenActivity).closeDrawers()
             (activity as DashBoardScreenActivity).navigateToBlockedReq()
         }


         binding.layoutManageAdmins.setOnClickListener {
             if (binding.handlePSEmployeesLayout.isVisible) {
                 binding.handlePSEmployeesLayout.visibility = View.GONE
             } else {
                 binding.handlePSEmployeesLayout.visibility = View.VISIBLE
             }
         }

         binding.layoutPSEmployees.setOnClickListener {
             (activity as DashBoardScreenActivity).closeDrawers()
             (activity as DashBoardScreenActivity).navigateToManageAdmin()
         }

         binding.layoutPintoEmployees.setOnClickListener {
             (activity as DashBoardScreenActivity).closeDrawers()
             (activity as DashBoardScreenActivity).navigateToManageAdmin()
         }

         binding.adminOrderClick.setOnClickListener {
             if (binding.handleAdminOrders.visibility == View.VISIBLE) {
                 binding.handleAdminOrders.visibility = View.GONE
             } else {
                 binding.handleAdminOrders.visibility = View.VISIBLE
             }
         }

         binding.layoutOrderRequests.setOnClickListener {
             (activity as DashBoardScreenActivity).closeDrawers()
             (activity as DashBoardScreenActivity).navigateToOrderRequestAdmin()
         }

         binding.layoutOrderStatus.setOnClickListener {
             (activity as DashBoardScreenActivity).closeDrawers()
             (activity as DashBoardScreenActivity).navigateToOrderStatusAdmin()
         }
         binding.layoutAnalytics.setOnClickListener {
             (activity as DashBoardScreenActivity).closeDrawers()
             (activity as DashBoardScreenActivity).navigateToManageAdmin()
         }

         binding.layoutAdminSettings.setOnClickListener {
             (activity as DashBoardScreenActivity).closeDrawers()
             (activity as DashBoardScreenActivity).navigateToManageAdmin()
         }
 */


//                if (binding.handleAdminOrders.visibility == View.VISIBLE) {
//                    binding.handleAdminOrders.visibility = View.GONE
//                } else {
//                    binding.handleAdminOrders.visibility = View.VISIBLE
//                }
//            }
//
//            R.id.layout_analytics -> {
//                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
//                    resources.getString(
//                        R.string.analytics
//                    )
//                )
//                binding.handleAdminOrders.visibility = View.GONE
//            }
//            R.id.layout_admin_settings -> {
//                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
//                    resources.getString(
//                        R.string.adminsettings
//                    )
//                )
//                binding.handleAdminOrders.visibility = View.GONE
//            }

//        binding.dashboardClick.setOnClickListener(this)
//        binding.QrEventClick.setOnClickListener(this)
//        binding.ShopClick.setOnClickListener(this)
//        binding.layoutArCards.setOnClickListener(this)
//        binding.layoutProfile.setOnClickListener(this)
//        binding.layoutOrders.setOnClickListener(this)
//        binding.layoutSettings.setOnClickListener(this)
//        binding.layoutRecycle.setOnClickListener(this)
//        binding.layoutInfo.setOnClickListener(this)
//        binding.layoutShare.setOnClickListener(this)
//        binding.layoutRate.setOnClickListener(this)
//        binding.layoutLogout.setOnClickListener(this)

        // admin side side navigation
//        binding.layoutNewRequest.setOnClickListener(this)
//        binding.layoutAcceptedReq.setOnClickListener(this)
//        binding.layoutRejectedReq.setOnClickListener(this)
//        binding.layoutBlockedReq.setOnClickListener(this)
//        binding.adminOrderClick.setOnClickListener(this)
//        binding.layoutQrEventStandee.setOnClickListener(this)
//        binding.layoutQrEventSetup.setOnClickListener(this)
//        binding.layoutQrEventOrders.setOnClickListener(this)
//        binding.layoutProfeTshirt.setOnClickListener(this)
//        binding.layoutAnalytics.setOnClickListener(this)
//        binding.layoutAdminSettings.setOnClickListener(this)
//        binding.closeDrawer.setOnClickListener(this)
//        binding.layoutManageAdmins.setOnClickListener(this)
//        binding.layoutOrderRequest.setOnClickListener(this)
//        binding.layoutOrderStatus.setOnClickListener(this)
    }

//    override fun onClick(v: View?) {
//        when (v!!.id) {
//
//            R.id.dashboardClick -> {
////                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(resources.getString(R.string.dashboard))
//                if (binding.handleDashboardLayout.visibility == View.VISIBLE) {
//                    binding.handleDashboardLayout.visibility = View.GONE
//                } else {
//                    binding.handleDashboardLayout.visibility = View.VISIBLE
//                    binding.handleShopLayout.visibility = View.GONE
//                    binding.handleQrEventLayout.visibility = View.GONE
//                }
//            }
//            R.id.QrEventClick -> {
////                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(resources.getString(R.string.qr_events))
//                if (binding.handleQrEventLayout.visibility == View.VISIBLE) {
//                    binding.handleQrEventLayout.visibility = View.GONE
//                } else {
//                    binding.handleQrEventLayout.visibility = View.VISIBLE
//                    binding.handleDashboardLayout.visibility = View.GONE
//                    binding.handleShopLayout.visibility = View.GONE
//                }
//            }
//            R.id.ShopClick -> {
////                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(resources.getString(R.string.shop))
//                if (binding.handleShopLayout.visibility == View.VISIBLE) {
//                    binding.handleShopLayout.visibility = View.GONE
//                } else {
//                    binding.handleQrEventLayout.visibility = View.GONE
//                    binding.handleDashboardLayout.visibility = View.GONE
//                    binding.handleShopLayout.visibility = View.VISIBLE
//                }
//            }
//            R.id.layout_ar_cards -> {
//                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
//                    resources.getString(
//                        R.string.ar_cards
//                    )
//                )
//            }
//            R.id.layout_profile -> {
//                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
//                    resources.getString(
//                        R.string.my_profile
//                    )
//                )
//            }
//            R.id.layout_orders -> {
//                if (PreferenceManager.getLoginType == "User" && PreferenceManager.employeeBy == "photoshooto") {
//                    (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(resources.getString(
//                            R.string.my_orders
//                        ))
//                } else if (PreferenceManager.getLoginType == "User" && PreferenceManager.employeeBy == "printo") {
//                    if (binding.handleOrderEventLayout.visibility == View.VISIBLE) {
//                        binding.handleOrderEventLayout.visibility = View.GONE
//                    } else {
//                        binding.handleOrderEventLayout.visibility = View.VISIBLE
//                    }
//                } else{
//                    (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(resources.getString(
//                        R.string.my_orders
//                    ))
//                }
//            }
//            R.id.layout_order_request -> {
//                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
//                    resources.getString(
//                        R.string.order_requests
//                    )
//                )
//            }
//            R.id.layout_order_status -> {
//                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
//                    resources.getString(
//                        R.string.order_status
//                    )
//                )
//            }
//            R.id.layout_settings -> {
//                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
//                    resources.getString(
//                        R.string.settings
//                    )
//                )
//            }
//            R.id.layout_recycle -> {
//                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
//                    resources.getString(
//                        R.string.recycle_bin
//                    )
//                )
//            }
//            R.id.layout_info -> {
//                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
//                    resources.getString(
//                        R.string.information
//                    )
//                )
//            }
//            R.id.layout_share -> {
//                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
//                    resources.getString(
//                        R.string.share_app
//                    )
//                )
//            }
//            R.id.layout_rate -> {
//                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
//                    resources.getString(
//                        R.string.rate_on_playstore
//                    )
//                )
//            }
//            R.id.layout_logout -> {
//                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
//                    resources.getString(
//                        R.string.logout
//                    )
//                )
//            }
//            R.id.layout_manage_admins -> {
//                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
//                    resources.getString(
//                        R.string.manage_admins
//                    )
//                )
//                binding.handleAdminOrders.visibility = View.GONE
//            }
//
//            R.id.adminOrderClick -> {
////                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(resources.getString(R.string.orders))
//                if (binding.handleAdminOrders.visibility == View.VISIBLE) {
//                    binding.handleAdminOrders.visibility = View.GONE
//                } else {
//                    binding.handleAdminOrders.visibility = View.VISIBLE
//                }
//            }
//
//            R.id.layout_analytics -> {
//                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
//                    resources.getString(
//                        R.string.analytics
//                    )
//                )
//                binding.handleAdminOrders.visibility = View.GONE
//            }
//            R.id.layout_admin_settings -> {
//                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
//                    resources.getString(
//                        R.string.adminsettings
//                    )
//                )
//                binding.handleAdminOrders.visibility = View.GONE
//            }
//            R.id.close_drawer -> {
//                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
//                    resources.getString(
//                        R.string.close_drawer
//                    )
//                )
//            }
//            R.id.layout_qr_event_standee -> {
//                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
//                    resources.getString(R.string.nav_qr_event_standee)
//                )
//            }
//            R.id.layout_qr_event_setup -> {
//                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
//                    resources.getString(R.string.qr_event_setup)
//                )
//            }
//            R.id.layout_qr_event_orders -> {
//                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
//                    resources.getString(R.string.qr_event_orders)
//                )
//            }
//            R.id.layout_profe_tshirt -> {
//                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
//                    resources.getString(R.string.professional_tshirt)
//                )
//            }
//            R.id.layout_new_request -> {
//                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
//                    resources.getString(R.string.new_request)
//                )
//            }
//            R.id.layout_accepted_req -> {
//                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
//                    resources.getString(R.string.accepted_request)
//                )
//            }
//            R.id.layout_rejected_req -> {
//                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
//                    resources.getString(R.string.rejected_request)
//                )
//            }
//            R.id.layout_blocked_req -> {
//                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
//                    resources.getString(R.string.blocked_req)
//                )
//            }
//        }
//    }

    private fun setHeaderText(textView: TextView, value: String?) {
        if (value.isNullOrEmpty()) {
            textView.visibility = View.GONE
        } else {
            textView.visibility = View.VISIBLE
            textView.text = value
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

