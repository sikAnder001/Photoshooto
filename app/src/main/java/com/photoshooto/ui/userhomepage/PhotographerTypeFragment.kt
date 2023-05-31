package com.photoshooto.ui.userhomepage

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.photoshooto.R
import com.photoshooto.databinding.BottomsheetSelectFolderBinding
import com.photoshooto.databinding.EventFilterDialogBinding
import com.photoshooto.databinding.FragmentPhotographerTypeBinding
import com.photoshooto.databinding.PhotographerSortByViewBinding
import com.photoshooto.domain.adapter.CommonSingleSelectAdapter
import com.photoshooto.domain.model.CommonMultiSelectItem
import com.photoshooto.domain.model.EventOrderHistoryElement
import com.photoshooto.domain.model.GetEventTypesResponse
import com.photoshooto.domain.model.UserElement
import com.photoshooto.domain.usecase.profile.GetUserProfileViewModel
import com.photoshooto.ui.qrcodesetup.createEvent.CreateEventViewModel
import com.photoshooto.ui.qrcodesetup.createEvent.adapter.SelectFolderAndTypeAdapter
import com.photoshooto.ui.qrorderhistory.OngoingEventViewModel
import com.photoshooto.ui.userhomepage.adapters.PhotographerListItemAdapter
import com.photoshooto.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class PhotographerTypeFragment : Fragment(), PhotographerListItemAdapter.OnUserItemClickListener {

    private lateinit var binding: FragmentPhotographerTypeBinding
    private val viewModel: GetUserProfileViewModel by viewModel()
    private val mViewModel: CreateEventViewModel by viewModel()

    private var mListEventPhotographyMain =
        arrayListOf<GetEventTypesResponse.GetEventTypesData.GetEventTypesModel>()
    private var mListEventTypeMain: List<EventOrderHistoryElement> = ArrayList()

    private var mSelectedEventType: GetEventTypesResponse.GetEventTypesData.GetEventTypesModel? =
        null
    private var mSelectedEventTypeAdapter: SelectFolderAndTypeAdapter? = null

    private val eventViewModel: OngoingEventViewModel by viewModel()

    var number: String = ""

    private var sortBy = SORT_BY.popularity
    private var sortByOrder: String? = SORT_BY.popularity

    var userList: List<UserElement>? = null

    object SORT_BY {
        val popularity = "popularity"
        val topRated = "toprated"
        val priceLowToHigh = "price_high_to_low"
        val priceHighToLow = "price_low_to_high"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotographerTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val type = requireArguments().getString("type")
        val toolbar = binding.toolbarWithBack
        val backBtn = toolbar.backBtn
        val tvHeaderTxt = toolbar.tvTitle
        tvHeaderTxt.text = type
        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        callRequiredAPI()

        initObserver()

        clickListener()
    }

    private fun callRequiredAPI() {
        viewModel.getPhotographerUsers()
        mViewModel.getEventTypeList(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))
        val className = FilterPhotographsFragment::class.java.name
        eventViewModel.getEventOrderHistory(0, 0, className)
    }

    private fun clickListener() {

        binding.filterView.tvTypeOfPhotography.setOnClickListener {
            openSelectPhotographerTypeDialog {
                binding.filterView.tvTypeOfPhotography.text = it
            }
        }
        binding.filterView.tvEvent.setOnClickListener {
            openEventTypeFilter()
        }

        binding.filterView.tvSort.setOnClickListener {
            openSortBy()
        }

        binding.filterView.tvBudget.setOnClickListener {
            binding.llBudget.visibility =
                if (binding.llBudget.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        binding.imgBudgetClose.setOnClickListener {
            binding.llBudget.visibility = View.GONE
        }
        binding.llBudget.setOnClickListener {
            binding.llBudget.visibility = View.GONE
        }
    }

    private fun initObserver() {
        viewModel.userResponse.observe(requireActivity(), Observer {
            if (it.success!!) {
                userList = it.data?.list
                binding.recyclerViewPhotographerList.adapter =
                    PhotographerListItemAdapter(requireActivity(), userList, this)
            }
        })

        viewModel.showProgressbar.observe(requireActivity(), Observer {
            binding.progressBar.visibility = if (it!!) View.VISIBLE else View.GONE
        })

        with(mViewModel) {
            getEventTypeListResponse.observe(requireActivity()) {
                if (it.success!!) {
                    mListEventPhotographyMain = arrayListOf()
                    if (it.data != null) {
                        if (!it.data!!.list.isNullOrEmpty()) {
                            mListEventPhotographyMain.addAll(it.data!!.list!!)
                        }
                    }

                } else {
                    it.message?.let { it1 -> onToast(it1, requireActivity()) }
                }
            }
        }
        with(eventViewModel) {
            eventOrderHistoyDetails.observe(
                requireActivity()
            ) { eventOrderHistoyDetails ->
                mListEventTypeMain = eventOrderHistoyDetails?.data?.list!!
            }
        }

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                filter(s.toString())
            }

            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    fun filter(text: String?) {
        val temp: MutableList<UserElement> = ArrayList()
        if (!userList.isNullOrEmpty()) {
            if (!text.isNullOrEmpty()) {
                for (user in userList!!) {
                    if (user.profile_details?.name?.lowercase(Locale.getDefault())!!
                            .contains(text.lowercase(Locale.getDefault()))
                    ) {
                        temp.add(user)
                    }
                }
            } else {
                temp.addAll(userList!!)
            }
        }

        //update recyclerview
        binding.recyclerViewPhotographerList.adapter =
            PhotographerListItemAdapter(requireActivity(), temp, this)
    }

    private fun openEventTypeFilter() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val dialogBinding = EventFilterDialogBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(dialogBinding.root)


        val dataList = ArrayList<CommonMultiSelectItem>()

        for (item in mListEventTypeMain) {
            dataList.add(CommonMultiSelectItem("0", item.event_type))
        }

        dialogBinding.apply {
            tvClear.setOnClickListener {
                (recyclerView.adapter as CommonSingleSelectAdapter).clearSelection()
            }
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = CommonSingleSelectAdapter(dataList)
            (recyclerView.adapter as CommonSingleSelectAdapter).onItemClickListener =
                object : CommonSingleSelectAdapter.OnItemClickListener {
                    override fun onDetailsClick(value: String) {
                        binding.filterView.tvEvent.text = value
                        bottomSheetDialog.dismiss()
                    }
                }
            bottomSheetDialog.show()
        }

        /* dialogBinding.apply {
             tvClear.setOnClickListener {
                 (recyclerView.adapter as NewRequestFilterAdapter).clearSelection()
             }
             btnApply.setOnClickListener {
                 bottomSheetDialog.dismiss()
             }
         }*/
        bottomSheetDialog.show()

    }

    private fun openSelectPhotographerTypeDialog(onDataSelected: (String) -> Unit) {
        val dialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogEvent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val mDialogBinding = BottomsheetSelectFolderBinding.inflate(
            LayoutInflater.from(requireActivity()), null, false
        )
        dialog.setContentView(mDialogBinding.root)

        mDialogBinding.tvBSTitle.text = "Type of Photography"
        val mListEventTypes = arrayListOf<String>()
        mListEventTypes.addAll(mListEventPhotographyMain.map { checkStringReturnValue(it.type) })

        val selectedName = if (mSelectedEventType != null) {
            checkStringReturnValue(mSelectedEventType!!.type)
        } else {
            ""
        }
        mSelectedEventTypeAdapter = SelectFolderAndTypeAdapter(
            requireActivity(),
            mListEventTypes,
            selectedName,
            object : OnItemClick<String> {
                override fun onItemClick(model: String, position: Int) {
                    val eventTypeModel = mListEventPhotographyMain[position]
                    mSelectedEventType = eventTypeModel
                    onDataSelected.invoke(checkStringReturnValue(mSelectedEventType!!.type))
                    dialog.dismiss()
                }
            })
        mDialogBinding.recyclerViewItems.apply {
            adapter = mSelectedEventTypeAdapter
        }

        mDialogBinding.imageBSNewFolderClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun openSortBy() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val dialogBinding = PhotographerSortByViewBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(dialogBinding.root)

        dialogBinding.apply {
            tvClear.setOnClickListener {
                if (!rbPopularity.isChecked) {
                    rbPopularity.isChecked = true
                }
            }
            when (sortBy) {
                SORT_BY.popularity -> {
                    if (sortByOrder == SORT_BY.popularity) {
                        rbPopularity.isChecked = true
                    } else rbPopularity.isChecked = true
                }
                SORT_BY.topRated ->
                    rbTopRated.isChecked = true
                SORT_BY.priceLowToHigh ->
                    rbLowToHigh.isChecked = true
                else -> {
                    rbHighToLow.isChecked = true
                }

            }
            radiogroup.setOnCheckedChangeListener { radioGroup, i ->
                when (i) {
                    R.id.rbPopularity -> {
                        sortBy = SORT_BY.popularity
                        sortByOrder = SORT_BY.popularity
                        //callRequestApi(true)
                    }
                    R.id.rbTopRated -> {
                        sortBy = SORT_BY.topRated
                        sortByOrder = SORT_BY.topRated
                        //callRequestApi(true)
                    }
                    R.id.rbLowToHigh -> {
                        sortBy = SORT_BY.priceLowToHigh
                        sortByOrder = null
                        // callRequestApi(true)
                    }
                    else -> {
                        sortBy = SORT_BY.priceHighToLow
                        sortByOrder = null
                    }
                }
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetDialog.show()
    }

    override fun onUserFavProfileClicked(user: UserElement, isFav: Boolean) {

    }

    override fun onEnquireClicked(user: UserElement) {
        number = user.profile_details?.mobile!!
        if (number.isNotEmpty() && number.isValidMobileNumber()) {
            callPhoneNumber(requireActivity(), number)
        }
    }

    override fun onViewProfileClicked(user: UserElement) {
        val bundle = Bundle()
        bundle.putString("userID", user.id!!)
        findNavController()
            .navigate(
                R.id.action_photographerTypeFragment_to_photographerDetailFragment,
                bundle
            )
    }
}