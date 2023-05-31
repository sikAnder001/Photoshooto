package com.photoshooto.ui.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.photoshooto.databinding.FragmentSplashBinding
import com.photoshooto.ui.admin_screen.AdminDashboardActivity
import com.photoshooto.ui.dashboard.DashBoardScreenActivity
import com.photoshooto.ui.on_boarding.OnBoardingActivity
import com.photoshooto.ui.photographersScreens.photographerAuth.AuthActivity
import com.photoshooto.ui.photographersScreens.photographerDashboard.PhotographerDashboardActivity
import com.photoshooto.util.Constant
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import java.util.*

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: FragmentSplashBinding

    var uKey = ""
    var uVal = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSplashBinding.inflate(layoutInflater)

        val data: Uri? = intent?.data
        if (data != null) {
            val parameter: List<String> = data.pathSegments
            uVal = parameter[parameter.size - 1]
            uKey = parameter[parameter.size - 2]
        }

        setContentView(binding.root)
        init()
    }

    private fun init() {
        Log.d("SplashActivity_1", Constant.USER_TYPE.USER.name.lowercase(Locale.getDefault()))
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 2000)
    }

    private fun navigateToNextScreen() {
        val showOnBoardingScreen =
            SharedPrefsHelper.readBoolean(SharedPrefConstant.SHOW_ON_BOARDING)
        val isLogin = SharedPrefsHelper.readBoolean(SharedPrefConstant.IS_LOGIN)
        if (!showOnBoardingScreen && !isLogin) {
            val intent = Intent(this, OnBoardingActivity::class.java)
            intent.apply {
                startActivity(this)
                finish()
            }

        } else if (showOnBoardingScreen && !isLogin) {
            val intent = Intent(this, AuthActivity::class.java)
            intent.apply {
                startActivity(this)
                finish()
            }

        } else {

            if (SharedPrefsHelper.read(SharedPrefConstant.USER_CASE_TYPE) == Constant.USER_CASE_TYPE.USER_SUPER_ADMIN.name ||
                SharedPrefsHelper.read(SharedPrefConstant.USER_CASE_TYPE) == Constant.USER_CASE_TYPE.USER_ADMIN.name
            ) {
                if (uKey == "Approve" || uKey == "Remove") {
                    val intent = Intent(this, AdminDashboardActivity::class.java)
                    intent.putExtra("btnText", uKey)
                    intent.putExtra("id", uVal)
                    intent.apply {
                        startActivity(this)
                        finish()
                    }
                } else {
                    val intent = Intent(this, AdminDashboardActivity::class.java)
                    intent.putExtra("btnText", "")
                    intent.putExtra("id", "")
                    intent.apply {
                        startActivity(this)
                        finish()
                    }
                }
            } else if (SharedPrefsHelper.read(SharedPrefConstant.USER_CASE_TYPE) == Constant.USER_CASE_TYPE.PHOTOGRAPHER_CLIENT.name) {
                if (uKey == "JobListing") {
                    val intent = Intent(this, PhotographerDashboardActivity::class.java)
                    intent.putExtra("id", uVal)

                    intent.apply {
                        startActivity(this)
                        finish()
                    }
                } else {
                    val intent = Intent(this, PhotographerDashboardActivity::class.java)
                    intent.putExtra("id", "")
                    intent.apply {
                        startActivity(this)
                        finish()
                    }
                }
            } else if (SharedPrefsHelper.read(SharedPrefConstant.USER_CASE_TYPE) == Constant.USER_CASE_TYPE.USER_CLIENT.name) {
                val intent = Intent(this, DashBoardScreenActivity::class.java)
                intent.apply {
                    startActivity(this)
                    finish()
                }
            }

            /*val intent: Intent =
                if (SharedPrefsHelper.read(SharedPrefConstant.USER_CASE_TYPE) == Constant.USER_CASE_TYPE.USER_SUPER_ADMIN.name ||
                    SharedPrefsHelper.read(SharedPrefConstant.USER_CASE_TYPE) == Constant.USER_CASE_TYPE.USER_ADMIN.name
                ) {
                    Intent(this, AdminDashboardActivity::class.java)
                } else {
                    Intent(this, PhotographerDashboardActivity::class.java)
                }
            intent.apply {
                startActivity(this)
                finish()
            }*/
        }
    }
}