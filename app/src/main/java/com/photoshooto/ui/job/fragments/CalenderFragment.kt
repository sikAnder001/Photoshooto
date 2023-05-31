package com.photoshooto.ui.job.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.base.BaseFragment
import com.photoshooto.databinding.FragmentCalendarBinding
import com.photoshooto.domain.model.CalendarEvent
import com.photoshooto.ui.job.JobViewModel
import com.photoshooto.ui.job.adapters.CalendarEventAdapter
import com.photoshooto.ui.job.utility.*
import com.photoshooto.ui.job.utility.gone
import com.photoshooto.util.*
import com.photoshooto.util.visible
import com.pscalendarevent.pscalendarevent.MarkStyle
import com.pscalendarevent.pscalendarevent.listeners.OnDateClickListener
import com.pscalendarevent.pscalendarevent.listeners.OnMonthChangeListener
import com.pscalendarevent.pscalendarevent.vo.DateData
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DateFormatSymbols
import java.util.*


class CalenderFragment : BaseFragment() {

    lateinit var binding: FragmentCalendarBinding
    var eventList = arrayListOf<CalendarEvent>()
    var bookedList = mutableListOf<String>()
    var availableList = mutableListOf<String>()
    private val jobHomeViewModel: JobViewModel by viewModel()
    private lateinit var calendarEventAdapter: CalendarEventAdapter
    private val navArgs: CalenderFragmentArgs by navArgs()
    private var isSelfView = false
    private var fromDash = ""


    var calendarCurrentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
    var calendarCurrentYear = Calendar.getInstance().get(Calendar.YEAR)
    lateinit var toolbarTitleTxt: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val toolbarBinding = binding.toolbarCalendar
        fromDash = arguments?.getString("fromDash", "").toString()

        if (!fromDash.isNullOrEmpty()) {
            backPress()
        }
        toolbarBinding.backBtn.setSafeOnClickListener { requireActivity().onBackPressed() }
        toolbarTitleTxt = toolbarBinding.tvTitle
        binding.progressBar.hide()
        return binding.root
    }

    private fun backPress() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(requireActivity(), object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    view?.findNavController()
                        ?.navigate(R.id.action_calendarFragment_to_photographerLandingPageFragment)
                }
            }
            )


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isSelfView = navArgs.userId.equals(SharedPrefsHelper.read(SharedPrefConstant.USER_ID), true)
        binding.monthName.text =
            "${DateFormatSymbols().months[calendarCurrentMonth - 1]} $calendarCurrentYear"



        binding.profile.userId.text = navArgs.userId
        binding.profile.userName.text = navArgs.userName
        binding.profile.city.text = navArgs.userAddress
        binding.profile.check.isVisible = navArgs.userSubscribe
        loadImageUser(binding.profile.imageViewProfile, navArgs.userProfile)

        if (isSelfView) {
            // binding.eventEdit.visible()
            binding.btnEvent.visible()
            binding.btnEvent.visible()

            toolbarTitleTxt.text = getString(R.string.my_calendr)
        } else {
            binding.eventEdit.gone()
            binding.btnEvent.gone()
            toolbarTitleTxt.text = "${navArgs.userName}'s Calendar"
        }
    }

    private fun setAdapter() {
        calendarEventAdapter = CalendarEventAdapter {}
        binding.recycler.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = calendarEventAdapter

            addItemDecoration(
                SpacesItemDecorationWithButton(
                    space = resources.getDimension(R.dimen.dp10).toInt(),
                    isGridLayoutManager = false,
                )
            )
        }
    }

    private fun attachObserver() {

        jobHomeViewModel.photographerCalendar.observe(viewLifecycleOwner) {
            hideProgress()
            if (it.success) {

                eventList.clear()
                availableList.clear()
                bookedList.clear()

                it.calendar.list.forEachIndexed { index, calendarEvent -> }

                eventList.addAll(it.calendar.list)


                availableList.addAll(it.calendar.availableCalendar)

                it.calendar.availableCalendar.forEach {
                    if (it.split("-").size == 3) {
                        availableList.add(it)
                    }
                }

                eventList.forEach {
                    if (availableList.contains(
                            it.startDate.getFormattedDatetime(
                                inputFormat = "MM/dd/yyy", outputFormat = "yyyy-MM-dd"
                            )
                        )
                    ) {
                        it.evType = "Available"
                    } else {
                        it.evType = "Booked"
                    }
                }

                it.calendar.bookedCalendar.forEach {
                    if (it.toString().split("-").size == 3) {
                        bookedList.add(it.toString())
                    }
                }

                updateCalendar()
                calendarEventAdapter.submitList(eventList)
            } else {
                findNavController().popBackStack()
                requireContext().toastError("Please try again later")
            }
        }
    }

    private fun updateCalendar() {

        availableList.forEach { date ->
            binding.calendarView.apply {
                markDate(
                    date.getCalendarDate().setMarkStyle(
                        MarkStyle(
                            MarkStyle.BACKGROUND,
                            requireContext().resources.getColor(R.color.brightGreen)
                        )
                    )
                )
            }
        }

        bookedList.forEach { date ->
            binding.calendarView.apply {
                markDate(
                    date.getCalendarDate().setMarkStyle(
                        MarkStyle(
                            MarkStyle.BACKGROUND,
                            requireContext().resources.getColor(R.color.colorOrange)
                        )
                    )
                )
            }
        }

        //DO NOT REMOVE else it will give ripple effect on calendar
        binding.calendarView.setOnDateClickListener(object : OnDateClickListener() {
            override fun onDateClick(view: View?, date: DateData?) {

            }
        })

        binding.right.setOnClickListener {
            binding.calendarView.nextMonth(calendarCurrentYear, calendarCurrentMonth)
        }

        binding.left.setOnClickListener {
            binding.calendarView.prevMonth(calendarCurrentYear, calendarCurrentMonth)
        }

        binding.calendarView.setOnMonthChangeListener(object : OnMonthChangeListener() {
            override fun onMonthChange(year: Int, month: Int) {
                DateFormatSymbols().months[month - 1]
                binding.monthName.text = "${DateFormatSymbols().months[month - 1]} $year"
                calendarCurrentMonth = month
                calendarCurrentYear = year
            }
        })
    }


    private fun initData() {
        showProgress()
        jobHomeViewModel.getPhotographerCalendar(navArgs.userId)
        binding.btnEvent.setOnClickListener {
            val args = Bundle().apply {
                putString("fromDash", "yes")
                putString("jobType", JobCreateFragment.PostType.POST_JOB.toString())
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
            findNavController().navigate(
                R.id.jobCreate, args
            )
        }

    }


/*
    override fun onStart() {
        super.onStart()

        isSelfView = navArgs.userId.equals(SharedPrefsHelper.read(SharedPrefConstant.USER_ID), true)

        if (isSelfView) {
            // binding.eventEdit.visible()
            binding.btnEvent.visible()
            binding.btnEvent.visible()

            toolbarTitleTxt.text = getString(R.string.my_calendr)
        } else {
            binding.eventEdit.gone()
            binding.btnEvent.gone()
            toolbarTitleTxt.text = "${navArgs.userName}'s Calendar"
        }

        setAdapter()
        attachObserver()

        updateCalendar()
        initData()


    }
*/


    override fun onResume() {
        super.onResume()

        //  binding.progressBar.show()


        isSelfView =
            navArgs.userId.equals(SharedPrefsHelper.read(SharedPrefConstant.USER_ID), true)
        binding.monthName.text =
            "${DateFormatSymbols().months[calendarCurrentMonth - 1]} $calendarCurrentYear"

        if (isSelfView) {
            // binding.eventEdit.visible()
            binding.btnEvent.visible()
            binding.btnEvent.visible()

            toolbarTitleTxt.text = getString(R.string.my_calendr)
        } else {
            binding.eventEdit.gone()
            binding.btnEvent.gone()
            toolbarTitleTxt.text = "${navArgs.userName}'s Calendar"
        }

        setAdapter()
        attachObserver()

        updateCalendar()
        initData()


    }

    /* override fun onDestroy() {
         super.onDestroy()
         hideProgress()
     }*/
}