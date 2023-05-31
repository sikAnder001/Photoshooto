package com.photoshooto

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.photoshooto.databinding.ActivityInviteFriendsBinding
import com.photoshooto.domain.usecase.profile.GetUserProfileViewModel
import com.photoshooto.ui.invite.adapters.InviteAdapter
import com.photoshooto.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.ParseException
import java.util.*


class InviteFriendsActivity : AppCompatActivity() {

    private val profileViewModel: GetUserProfileViewModel by viewModel()
    lateinit var binding: ActivityInviteFriendsBinding

    private var referralCode = ""
    private var referralUrl = ""

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_invite_friends)

        if ((profileViewModel.getUserData.value?.data?.data?.referral_code?.length ?: 0) < 1) {
            loadUserData()
        }

        // SETUP UI ELEMENTS
        var handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                updateTimerUI()
                handler.postDelayed(this, 1000)
            }
        }, 1000)

        setListeners()
        setObservers()
    }

    private fun setObservers() {
        with(profileViewModel) {
            getReferralCode.observe(this@InviteFriendsActivity) { response ->
                response.log()
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {
                            response.data.log()
                            referralCode = response.data?.data?.code ?: ""
                            referralUrl =
                                "https://play.google.com/store/apps/details?id=${packageName}" //"https://photoshooto.com?referal=$referralCode"
                            getReferredUsers(referralCode)
                        }
                        Status.ERROR -> {
                            response.data?.message?.let { it1 -> showToast(it1) }
                        }
                        else -> {}
                    }
                }
            }

            userReferredResponse.observe(this@InviteFriendsActivity) { response ->
                if (response != null) {
                    if (response.success) {
                        var inviteList = MutableList(5) { "" }
                        inviteList = inviteList.mapIndexed { index, s ->
                            if (response.data.size > index) {
                                return@mapIndexed response.data[index].profile_details.first_name
                            } else {
                                return@mapIndexed ""
                            }
                        }.toMutableList()
                        bindInviteRecycler(inviteList)
                    } else {

                    }
                }
            }
        }
    }

    private fun setListeners() {
        binding.btnInviteFriend.setOnClickListener {
            shareInvite()
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun bindInviteRecycler(data: List<String>) {
        val inviteAdapter = InviteAdapter(
            data,
            object : InviteAdapter.OnItemClickListener {
                override fun onClickInvite() {
                    shareInvite()
                }
            }
        )

        binding.rvInviteGroup.adapter = inviteAdapter
        binding.rvInviteGroup.visibility = View.VISIBLE
    }

    fun shareInvite() {
        val intent = Intent(Intent.ACTION_SEND)
        val shareBody =
            "Here's an exclusive invite for you to join Photoshooto. You can get your potential customers, build your professional profile to unlock amazing photography jobs. Install the app and enter the invite code: $referralCode to move up the waitlist.\n" +
                    "$referralUrl"
//        val shareBody = "Code: $referralCode, $referralUrl"
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_SUBJECT,
            getString(R.string.invite_friend)
        )
        intent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(Intent.createChooser(intent, getString(R.string.invite_friend)))
    }

    private fun loadUserData() {
        profileViewModel.getReferralCode(
            SharedPrefsHelper.read(SharedPrefConstant.USER_ID),
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun updateTimerUI() {
        val format = "yyyy-MM-dd'T'HH:mm:ss"
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        val date = SharedPrefsHelper.read(SharedPrefConstant.USER_REGISTERED_ON)
        val registerDate = uTCToLocal(format, format, date)
        val userDor: Date = dateFormat.parse(registerDate)

        userDor.time = userDor.time + (24 * 60 * 60 * 1000)

        val today = Date()
        val diff: Long = userDor.time - today.time
        val secs = (diff / (1000) % 60).toInt()
        val min = ((diff / (1000 * 60)) % 60).toInt()
        val hours = (diff / (1000 * 60 * 60)).toInt()

        binding.progressBar.progress = (diff / (24 * 60 * 60 * 10)).toInt()

        binding.tvTimer.text = "$hours:$min:$secs"
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
}