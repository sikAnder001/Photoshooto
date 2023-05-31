package com.photoshooto.ui.job.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.photoshooto.R
import com.photoshooto.databinding.DialogBottomFilterBinding
import com.photoshooto.domain.model.SpinnerModel
import com.photoshooto.ui.dialog.DateTimeDialog
import com.photoshooto.ui.job.JobViewModel
import com.photoshooto.ui.job.adapters.CheckBoxFilterAdapter
import com.photoshooto.ui.job.utility.*
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import org.json.JSONArray
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel

class BottomSheetFilterDialog : BottomSheetDialogFragment() {

    lateinit var binding: DialogBottomFilterBinding
    private val jobHomeViewModel: JobViewModel by viewModel()
    var eventList = mutableListOf<SpinnerModel>()
    var studioList = mutableListOf<SpinnerModel>()
    private val navArgs: BottomSheetFilterDialogArgs by navArgs()
    private lateinit var checkBoxFilterAdapter: CheckBoxFilterAdapter
    var selectedOption = 1
    var page = 0
    var prefillFilterJSON = JSONObject()
    var filterJson = JSONObject()
    var preEvents = JSONArray()
    var preStudio = JSONArray()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DialogBottomFilterBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (navArgs.type.equals("Hire Me", true)) {
            //JOB TYPE  fix only as screen
            binding.filterOpt5.gone()
        } else {
            //JOB TYPE  fix only as screen
            binding.filterOpt5.gone()
        }

        setColorFilter(1)

        binding.filterOpt1.setSafeOnClickListener {
            //Event Type
            setColorFilter(1)
            checkBoxFilterAdapter.submitList(null)
            checkBoxFilterAdapter.submitList(eventList)
        }

        binding.filterOpt2.setSafeOnClickListener {
            //Studio Type
            setColorFilter(2)
            checkBoxFilterAdapter.submitList(null)
            checkBoxFilterAdapter.submitList(studioList)
        }

        binding.filterOpt3.setSafeOnClickListener {
            //Area
            setColorFilter(3)
            checkBoxFilterAdapter.submitList(null)
            //No data as of now
        }

        binding.filterOpt4.setSafeOnClickListener {
            //Date Time
            setColorFilter(4)


        }

        binding.filterOpt5.setSafeOnClickListener {
            //Job Type
            setColorFilter(5)
        }

        binding.tvStDate.setSafeOnClickListener {
            DateTimeDialog.showDateDialog(requireContext()) { date ->
                val tmp = date.getFormattedDatetime(
                    inputFormat = "yyyy-MM-dd", outputFormat = "dd-MM-yyyy"
                )
                binding.tvStDate.text = tmp
            }
        }

        binding.tvEnDate.setSafeOnClickListener {
            DateTimeDialog.showDateDialog(requireContext()) { date ->
                val tmp = date.getFormattedDatetime(
                    inputFormat = "yyyy-MM-dd", outputFormat = "dd-MM-yyyy"
                )
                binding.tvEnDate.text = tmp
            }
        }

        binding.cancel.setSafeOnClickListener {
            findNavController().popBackStack()
        }

        binding.clear.setSafeOnClickListener {
            eventList.forEach {
                it.isSelected = false
            }
            studioList.forEach {
                it.isSelected = false
            }

            binding.filterOpt1.callOnClick()
            binding.tvStDate.text = ""
            binding.tvEnDate.text = ""
            binding.tvSearch.setText("")
        }

        checkBoxFilterAdapter = CheckBoxFilterAdapter { _, _ ->

        }

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), RecyclerView.VERTICAL, false
            )

            addItemDecoration(
                SpacesItemDecoration(
                    space = resources.getDimension(R.dimen.dp1).toInt(),
                    isGridLayoutManager = false,
                )
            )

            itemAnimator = null
            adapter = checkBoxFilterAdapter
        }

        binding.btnApply.setSafeOnClickListener {
            filterJson = JSONObject()

            filterJson.put("event_type", JSONArray().apply {
                eventList.forEach {
                    if (it.isSelected) this.put(it.baseName)
                }
            })

            filterJson.put("studio", JSONArray().apply {
                studioList.forEach {
                    if (it.isSelected) this.put(it.baseName)
                }
            })

            if (binding.tvStDate.text.toString().isNotBlank()) {
                filterJson.put(
                    "startDate", binding.tvStDate.text.toString().getFormattedDatetime(
                        inputFormat = "dd-MM-yyyy",
                        outputFormat = "yyyy-MM-dd"
                    )
                )
            }
            if (binding.tvEnDate.text.toString().isNotBlank()) {
                filterJson.put(
                    "endDate", binding.tvEnDate.text.toString().getFormattedDatetime(
                        inputFormat = "dd-MM-yyyy",
                        outputFormat = "yyyy-MM-dd"
                    )
                )
            }

            if (binding.tvSearch.text.toString().isNotBlank()) {
                filterJson.put(
                    "area", binding.tvSearch.text.toString()
                )
            }

            if (filterJson.getJSONArray("event_type").length() == 0) filterJson.remove("event_type")
            if (filterJson.getJSONArray("studio").length() == 0) filterJson.remove("studio")
//            if (filterJson.getJSONArray("area").length() == 0) filterJson.remove("area")

            requireActivity().supportFragmentManager.setFragmentResult("dialog_filter",
                Bundle().apply {
                    putString("filter_json", filterJson.toString())
                })
            findNavController().navigateUp()
        }

        setPrefilledData()
        attachObserver()
    }

    private fun setPrefilledData() {

        prefillFilterJSON = JSONObject(navArgs.prefillFilterJSON)

        if (prefillFilterJSON.has("startDate")) {
            binding.tvStDate.text = prefillFilterJSON.getString("startDate")
        }

        if (prefillFilterJSON.has("endDate")) {
            binding.tvEnDate.text = prefillFilterJSON.getString("endDate")
        }

        if (prefillFilterJSON.has("event_type"))
            preEvents = prefillFilterJSON.getJSONArray("event_type")

        if (prefillFilterJSON.has("studio"))
            preStudio = prefillFilterJSON.getJSONArray("studio")

        if (prefillFilterJSON.has("area"))
            (binding.tvSearch as TextView).text = filterJson.optString("area", "")
    }

    private fun attachObserver() {
        jobHomeViewModel.eventTypeList.observe(viewLifecycleOwner) {
            if (it.success) {
                eventList.clear()
                it.data.list.forEach { evType ->
                    eventList.add(
                        SpinnerModel(
                            evType.type, preEvents.has(evType.type)
                        )
                    )
                }

                preEvents = JSONArray()
                if (prefillFilterJSON.has("event_type"))
                    preEvents = prefillFilterJSON.getJSONArray("event_type")

                checkBoxFilterAdapter.submitList(null)
                checkBoxFilterAdapter.submitList(eventList)
            }
        }

        jobHomeViewModel.studioList.observe(viewLifecycleOwner) {
            if (it.success) {
                studioList.clear()
                it.data.list.forEach { type ->
                    studioList.add(SpinnerModel(type.name, preStudio.has(type.name)))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initData() {
        jobHomeViewModel.getEventType(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))
        jobHomeViewModel.getStudioList(page)
    }

    private fun setColorFilter(pos: Int) {
        selectedOption = pos
        binding.filterOpt1.setTextColor(requireContext().resources.getColor(R.color.grey_99))
        binding.filterOpt2.setTextColor(requireContext().resources.getColor(R.color.grey_99))
        binding.filterOpt3.setTextColor(requireContext().resources.getColor(R.color.grey_99))
        binding.filterOpt4.setTextColor(requireContext().resources.getColor(R.color.grey_99))
        binding.filterOpt5.setTextColor(requireContext().resources.getColor(R.color.grey_99))
        binding.recycler.visible()
        binding.dateGroup.invisible()
        binding.tvSearch.invisible()

        binding.filterOpt5.setTextColor(requireContext().resources.getColor(R.color.grey_99))

        when (pos) {
            1 -> {
                binding.filterOpt1.setTextColor(requireContext().resources.getColor(R.color.colorBlack))
            }
            2 -> {
                binding.filterOpt2.setTextColor(requireContext().resources.getColor(R.color.colorBlack))
            }
            3 -> {
                binding.filterOpt3.setTextColor(requireContext().resources.getColor(R.color.colorBlack))
                binding.tvSearch.visible()
            }
            4 -> {
                binding.filterOpt4.setTextColor(requireContext().resources.getColor(R.color.colorBlack))
                binding.recycler.invisible()
                binding.dateGroup.visible()
            }
            5 -> {
                binding.filterOpt5.setTextColor(requireContext().resources.getColor(R.color.colorBlack))
            }
        }
    }
}