package com.photoshooto.ui.usedPhotographyScreens.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.photoshooto.R
import com.photoshooto.api_call_package.Repository2
import com.photoshooto.api_call_package.VMFactory
import com.photoshooto.api_call_package.view_model.JobsViewModel
import com.photoshooto.api_call_package.view_model.ProductListViewModel
import com.photoshooto.base.BaseFragment
import com.photoshooto.databinding.FragmentPostDetailsBinding
import com.photoshooto.domain.model.ApproveRejectBody
import com.photoshooto.domain.model.UpdateProductStatusBody
import com.photoshooto.domain.model.User
import com.photoshooto.domain.model.get_productlist_model.List
import com.photoshooto.ui.dialog.ReportDialog
import com.photoshooto.ui.feedback.SuccessDialog
import com.photoshooto.ui.feedback.SuccessDialog.Companion.message
import com.photoshooto.ui.job.JobViewModel
import com.photoshooto.ui.job.utility.*
import com.photoshooto.ui.usedPhotographyScreens.adapter.ImageThumpListAdapter
import com.photoshooto.ui.usedPhotographyScreens.adapter.ProductsImageListAdapter
import com.photoshooto.ui.usedPhotographyScreens.adapter.UsedProductsListAdapter
import com.photoshooto.util.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.String.join
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class PostDetailsFragment : BaseFragment() {

    lateinit var binding: FragmentPostDetailsBinding
    var statusad= ""

    private val jobHomeViewModel: JobViewModel by viewModel()

    private lateinit var repository: Repository2
    private lateinit var viewModel: JobsViewModel
    private lateinit var factory: VMFactory

    private lateinit var productsImageListAdapter: ProductsImageListAdapter
    private lateinit var imageThumpListAdapter: ImageThumpListAdapter
    private lateinit var usedProductsListAdapter: UsedProductsListAdapter
    private val navArgs: PostDetailsFragmentArgs by navArgs()


    lateinit var products: ArrayList<String>


    var btnText = ""
    var message = ""
    var productId = ""

    var user: User? = null
    var isAdmin = false
    var isSeller = false
    var isReported = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized) {
            return binding.root
        }
        binding = FragmentPostDetailsBinding.inflate(inflater, container, false)

        repository = Repository2()
        factory = VMFactory(repository)
        viewModel = ViewModelProvider(this, factory)[JobsViewModel::class.java]



        btnText = arguments!!.getString("btnText").toString()
        var id = arguments!!.getString(id.toString())

        binding.imgDetailBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.icWarning.setOnClickListener {
            openReportedDialog()
            // Toast.makeText(requireContext(), "click", Toast.LENGTH_SHORT).show()

        }

        /*ReportDialog.showDialog(
            requireContext()
        )
        { message ->
            jobHomeViewModel.spamReport(navArgs.postId, message, "product")
        }*/

        //  binding.llContactSeller.isVisible = false




        isAdmin = navArgs.isAdmin
        isSeller = navArgs.isSeller
        isReported = navArgs.isReported

        // getProductById(id)


        if (isAdmin) {

            binding.llAdminView.isVisible = true
            binding.llEditPost.isVisible = false
            binding.llSimilarItem.isVisible = false
            binding.icWarning.isVisible = false
            binding.icReport.isVisible = false
            binding.icShare.isVisible = false
            binding.btncontact.isVisible = false

            binding.llContactSeller.isVisible = false

            /*  if(statusad=="approved"){
                  viewModel.updateStatus(
                      "approved",
                      UpdateProductStatusBody(products = products)

                  )
                  binding.llRemove.isVisible = true

              }
  */

            /*if(statusad=="rejected"){
                *//*viewModel.updateStatus(
                    "rejected",
                    UpdateProductStatusBody(products = products)

                )*//*
                binding.llApprove.isVisible = true

            }*/






            if (isReported) {
                /* if(statusad  == "rejected")
                 {
                     binding.llRemove.isVisible = true
                     binding.llApprove.isVisible = true

                 }*/
                //  attachObserver()



                binding.llRemove.isVisible = true

                /*  binding.tvJobTitle.text = getString(R.string.post_removed)
                  binding.tvJobId.isVisible = false*/

            } else {

                /* if(statusad == "approved")
                 {
                     binding.llApprove.isVisible = true
                     binding.llRemove.isVisible = true
                 }*/


                binding.llApprove.isVisible = true
/*                binding.tvJobTitle.text = getString(R.string.job_approved)
                binding.tvJobId.text = ": AD045664"*/
            }
        }

        if (isSeller) {
            // binding.btncontact.isVisible = true
            // binding.llContactSeller.isVisible = true

        }

        productsImageListAdapter = ProductsImageListAdapter(onClick = {

            findNavController().navigate(
                PostDetailsFragmentDirections.actionDetailsToFullscreen(it)
            )

        })

        imageThumpListAdapter = ImageThumpListAdapter(onClick = {
            binding.vpProductImage.currentItem = it
        })

        usedProductsListAdapter = UsedProductsListAdapter(onClick = { d ->

            jobHomeViewModel.productsDetail(d.id)

            jobHomeViewModel.similarProductsListing(d.id)

            binding.nesScroll.scrollTo(0, 0)

            // binding.btncontact.isVisible = true
            binding.llContactSeller.isVisible = true


        })

        binding.rvImageThump.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvUsedProducts.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.rvImageThump.adapter = imageThumpListAdapter
        binding.rvUsedProducts.adapter = usedProductsListAdapter

        binding.vpProductImage.adapter = productsImageListAdapter

        binding.vpProductImage.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                imageThumpListAdapter.swapItem(position)
                binding.rvImageThump.smoothScrollToPosition(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })

        binding.llEditPost.setOnClickListener {
            findNavController().navigate(
                PostDetailsFragmentDirections.actionPostDetailsToPost1()
                    .setPostId(navArgs.postId)
            )
        }



        attachObserver()


        showProgress()

        jobHomeViewModel.productsDetail(navArgs.postId)


        jobHomeViewModel.similarProductsListing(navArgs.postId)



        return binding.root
    }






    /* private fun getProductById(id: String?) {
         if (requireContext().isInternetAvailable()) {
             viewModel.getProductById(id)
         } else {
             Toast.makeText(requireContext(), "Please check your internet", Toast.LENGTH_SHORT)
                 .show()
         }
     }*/


/*
    @RequiresApi(Build.VERSION_CODES.N)
    private fun initObserver() {
        try {
            viewModel.getProductByIdResponse.observe(this) {
                if (it.isSuccessful && it.code() == 200) {
                    val data = it.body()!!.data!!
                    binding.apply {
                        if (data==null) {
                            Toast.makeText(context, "No Data Found!", Toast.LENGTH_SHORT).show()
                        } else {
                            if (data.images[0]== null) {
                                Glide.with(context!!)
                                    .load(urlAddingForPicture(data.images[0]))
                                    .error(R.drawable.ic_profile_placeholder).into(imageView)
                            }

                            */
/*var sDate = ""
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                sDate =
                                    Instant                           // Represents a point on the time line.
                                        .parse(data[0].startDate)  // Returns an `Instant` object. By default, parses text in standard ISO 8601 for at where `Z` means offset of zero.
                                        .atOffset(ZoneOffset.UTC)       // Returns an `OffsetDateTime` object.
                                        .format(
                                            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                                        )
                            }

                            eventStartTimeTv.text = "${sDate}, ${data[0].startTime}"

                            var lDate = ""
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                lDate =
                                    Instant                           // Represents a point on the time line.
                                        .parse(data[0].endDate)  // Returns an `Instant` object. By default, parses text in standard ISO 8601 for at where `Z` means offset of zero.
                                        .atOffset(ZoneOffset.UTC)       // Returns an `OffsetDateTime` object.
                                        .format(
                                            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                                        )
                            }
*//*


//                            Toast.makeText(context, "$sDate--$lDate", Toast.LENGTH_SHORT).show()
                            eventEndTimeTv.text = "${lDate}, ${data[0].endTime}"


                            if (data[0].status == "approved" || data[0].status == "rejected") {
                                layoutBtnAppDec.visibility = View.GONE
                            }

                            jobTitle.text = data[0].userProfile!!.studioName
                            jobId.text = data[0].id
                            addressTv.text = data[0].address?.location ?: ""
                            ratingStar.text = data[0].userProfile!!.rating.toString()
                            ratingNum.text = "${data[0].feedback.size} ratings"
                            eventNameTv.text = data[0].title
                            eventTypeTv.text = data[0].eventType
                            eventLocationTv.text = data[0].address?.location ?: ""
                            */
/* eventStartTimeTv.text=data[0].startDate
                             eventEndTimeTv.text=data[0].startDate*//*

                            photographyTypeTv.text = data[0].photographyType
                            var cam = StringJoiner("\n")

                            for (element in data[0].photoCameraUse) {
                                if (!element.isNullOrEmpty()) {
                                    cam.add(element)
                                }
                            }
                            cameraRequiredTv.text = cam.toString()

                            var vid = StringJoiner("\n")

                            for (element in data[0].videoCameraUse) {
                                if (!element.isNullOrEmpty()) {
                                    vid.add(element)
                                }
                            }

                        }
                    }
                } else {
                    Toast.makeText(context, it.message().toString(), Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }

    }
*/




    private fun viewImage(image: String) {

        findNavController().navigate(
            PostDetailsFragmentDirections.actionDetailsToFullscreen(image)
        )
    }

    private fun openReportedDialog() {
        var dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_report_popup)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val rgReported = dialog.findViewById(R.id.rgDecline) as RadioGroup
        val btnCancel = dialog.findViewById(R.id.btnCancel) as Button
        val btnReport = dialog.findViewById(R.id.btnReport) as Button
        val etOther = dialog.findViewById(R.id.etOther) as EditText
        val rdOther = dialog.findViewById(R.id.rdOther) as RadioButton





        rgReported.setOnClickListener {
            requireContext().hideKeyboardFrom(etOther)
            etOther.isEnabled = false
            etOther.setText("")
            etOther.hint = getString(R.string.others)
            rdOther.isChecked = false
        }

        rdOther.setOnCheckedChangeListener { compoundButton, check ->
            if (check && compoundButton.isPressed) {
                rgReported.clearCheck()
                etOther.isEnabled = true
                etOther.setText("")
                etOther.requestFocus()
                etOther.showKeyboard()
                rdOther.isChecked = true
            }
        }



        btnReport.setOnClickListener {

            var id: Int = rgReported.checkedRadioButtonId
            if (rdOther.isChecked) {
                if (etOther.text.isEmpty())
                    Toast.makeText(
                        context, "please provide reason",
                        Toast.LENGTH_SHORT
                    ).show()
                else {
                    viewModel.saveSpamReports(navArgs.postId,etOther.text as String)


                    dialog.dismiss()
                }
            } else if (id != -1) {
                var id: Int = rgReported.checkedRadioButtonId
                if (id != -1) {
                    val radio: RadioButton = dialog.findViewById(id)


                    viewModel.saveSpamReports(navArgs.postId, radio.text as String)



                    dialog.dismiss()
                }

                dialog.dismiss()
            }
        }

        btnCancel.setOnClickListener {
            /* var id: Int = rgGroup.checkedRadioButtonId
             if (id != -1) {
                 val radio: RadioButton = dialog.findViewById(id)
                 openTabs(radio.text as String)
                 dialog.dismiss()
             }*/

            dialog.dismiss()
        }



        dialog.show()
    }

    private fun attachObserver() {
        jobHomeViewModel.similarProductsList.observe(viewLifecycleOwner) { data ->

            if (data.success) {

                if (data.list.isNotEmpty()) {

                    usedProductsListAdapter.swapList(data.list, false)

                    binding.llSimilarItem.isVisible = true

                } else {
                    binding.llSimilarItem.isVisible = false
                }

            } else {
                binding.llSimilarItem.isVisible = false

                requireContext().toastError(data.message.toString())
            }
        }

        jobHomeViewModel.ProductsModel.observe(viewLifecycleOwner) { data ->
            hideProgress()

            if (data.success) {
                data.data.let { data ->

                    Log.e("_id", "" + data?._id)
                    binding.title.text = data?.brand
                    binding.tvBrand.text = data?.brand
                    binding.tvName.text = data?.name
                    binding.tvPrice.text = "₹ ${data?.price}/-"

                    binding.tvUserName.text = data?.user_profile?.name
                    binding.tvUserId.text = data?.userId
                    binding.tvLocation.text = data?.user_profile?.address
                    data?.user_profile?.profile_image?.let {
                        loadImageUser(
                            binding.imageViewProfile,
                            it
                        )
                    }

                    binding.tvDes.text = data?.description
                    binding.tvTime.text = "Posted ${data?.createdAt?.getDateTimeDiffSingle()}"
                    binding.tvAdId.text = data?.id
                    binding.tvMainProduct.text = data?.name
                    binding.tvEquipments.text = data?.equipmentType
                    binding.tvCondition.text = data?.condition
                    binding.tvPurchaseDate.text = data?.purchaseDate
                    binding.tvShutter.text = data?.shutterCount
                    binding.tvShutter.text = data?.shutterCount
                    binding.tvSellingAddress.text = data?.sellingLocation
                    // statusad= data?.status.toString()

                    if (data?.status == "approved") {
                        binding.btnApproval.isVisible = true
                        binding.btnDecline.isVisible = false
                    }
                    else if(data?.status == "rejected"){
                        binding.btnApproval.isVisible = false
                        binding.btnDecline.isVisible = true
                    }
                    else{
                        binding.btnApproval.isVisible = true
                        binding.btnDecline.isVisible = true
                    }

                    /* if (data?.status == "rejected") {

                     }*/

                    binding.tvOriginalBill.text =
                        if (data?.isBillAvailable == true) getString(R.string.yes) else getString(R.string.no)
                    binding.tvOriginalBattery.text =
                        if (data?.isOriginalBattery == true) getString(R.string.yes) else getString(
                            R.string.no
                        )
                    binding.tvOriginalCharger.text =
                        if (data?.isOriginalCharger == true) getString(R.string.yes) else getString(
                            R.string.no
                        )
                    binding.tvOriginalBag.text =
                        if (data?.isOriginalBagCover == true) getString(R.string.yes) else getString(
                            R.string.no
                        )
                    binding.tvService.text =
                        if (data?.serviceDone == true) getString(R.string.yes) else getString(R.string.no)
                    binding.tvWarranty.text =
                        if (data?.isInWarranty == true) getString(R.string.yes) else getString(R.string.no)

                    var listString = ""
                    if (data?.additionalEquipment != null) {
                        for (element in data?.additionalEquipment!!) {
                            listString = join(",", element)
                        }
                    }

                    binding.tvAdditional.text = listString

                    if (data?.isBillAvailable == true) {
                        binding.rlBill.isVisible = true
                        binding.tvWarrantytitle.isVisible = true

                        Glide.with(requireActivity())
                            .load(data.bill_file)
                            .into(binding.imgBill)

                        binding.rlBill.setOnClickListener {

                            viewImage(data.bill_file)
                        }

                    } else {
                        binding.rlBill.isVisible = false
                    }

                    if (data?.isInWarranty == true) {
                        binding.rlWarranty.isVisible = true
                        binding.tvWarrantytitle.isVisible = true

                        Glide.with(requireActivity())
                            .load(data.warrantyCardFile)
                            .into(binding.imgWarranty)

                        binding.rlWarranty.setOnClickListener {

                            viewImage(data.warrantyCardFile)
                        }

                    } else {
                        binding.rlWarranty.isVisible = false
                    }

                    binding.llContactSeller.setOnClickListener {
                        callPhoneNumber(requireActivity(), data?.user_profile?.mobile.toString())
                        /* findNavController().navigate(PostDetailsFragmentDirections.actionPostDetailsToPost1()
                             .setPostId(navArgs.postId))*/
                    }


                    /* binding.btncontact.setOnClickListener {
                         callPhoneNumber(requireActivity(), data?.user_profile?.mobile.toString())
                     }
 */
                    data?.images?.let { productsImageListAdapter.swapList(it) }
                    data?.images?.let { imageThumpListAdapter.swapList(it) }

                }
            } else {
                requireContext().toastError(data.message.toString())
            }

            jobHomeViewModel.spamReport.observe(viewLifecycleOwner) {
                hideProgress()
                if (it.success) {
                    requireContext().toastSuccess(it.message.toString())
                } else {
                    requireContext().toastError(it.message.toString())



                }
            }
        }
    }


}