package com.photoshooto.ui.usedPhotographyAdmin.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.photoshooto.R
import com.photoshooto.api_call_package.ReportedListVMFactory
import com.photoshooto.api_call_package.Repository2
import com.photoshooto.api_call_package.view_model.ReportedListViewModel
import com.photoshooto.databinding.FragmentReportedProductBinding
import com.photoshooto.domain.model.User
import com.photoshooto.ui.admin_screen.manage_job_posting.ReportedJobAdapter
import com.photoshooto.ui.job.utility.hideKeyboardFrom
import com.photoshooto.ui.usedPhotographyAdmin.adapter.ReportedListAdpater
import com.photoshooto.util.isInternetAvailable
import com.photoshooto.util.showKeyboard

class ReportedProductFragment(val param: String) : Fragment() {

    lateinit var binding: FragmentReportedProductBinding
    private lateinit var repository: Repository2
    private lateinit var viewModel: ReportedListViewModel
    private lateinit var factory: ReportedListVMFactory

    var user: User? = null

    lateinit var products: ArrayList<String>

    var idForToast = ""

    private lateinit var reportedListAdpater: ReportedListAdpater

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentReportedProductBinding.inflate(inflater, container, false)

        repository = Repository2()

        factory = ReportedListVMFactory(repository)

        viewModel = ViewModelProvider(this, factory)[ReportedListViewModel::class.java]

        showReportedList()

        initObserver()


        return binding.root
    }


    private fun initObserver() {
        with(viewModel) {
            getReportedListResponse.observe(viewLifecycleOwner) {
                if (it.isSuccessful && it.code() == 200) {
                    val data = it.body()!!.message
                    it.body()?.data?.list?.let { it1 -> reportedListAdpater.setList(it1) }

                    //  Log.v("reported",it.body()?.data?.list?.size.toString())


                    // Toast.makeText(context, it.body()!!.data.toString(), Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(context, it.message().toString(), Toast.LENGTH_SHORT).show()
                }
            }



            removeListResponse.observe(viewLifecycleOwner) {
                if (it.isSuccessful && it.code() == 200) {
                    val data = it.body()!!.message
                    Toast.makeText(context, data, Toast.LENGTH_SHORT).show()
                    if (requireContext().isInternetAvailable()) {
                        // viewModel.getReportedList(10, 0, "products")
                        when (param) {
                            "Recently Updated" -> viewModel.getReportedList(10, 0, "products", -1, "")
                            /* "Price Range: Low to High" -> viewModel.getReportedList(10, 0, "products", 0, "created_at")
                             "Price Range: High to Low" -> viewModel.getReportedList(10, 0, "products", 0, "created_at")*/
                            "Most Reported" -> viewModel.getReportedList(10, 0, "products", 0, "created_at")
                            /*  "Approved Product" -> viewModel.getReportedList(10, 0, "products", 0, "created_at")
                              "Declined Product" -> viewModel.getReportedList(10, 0, "products", 0, "created_at")*/
                            else -> viewModel.getReportedList(10, 0, "products", null, "")
                        }

                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Please check your internet",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    reportedListAdpater.notifyDataSetChanged()
                } else {
                    Toast.makeText(context, it.message().toString(), Toast.LENGTH_SHORT).show()
                }
            }


        }
    }

    private fun showReportedList() {
        if (requireContext().isInternetAvailable()) {

            when (param) {
                "Recently Updated" -> viewModel.getReportedList(10, 0, "products", -1, "")
                /* "Price Range: Low to High" -> viewModel.getReportedList(10, 0, "products", 0, "created_at")
                 "Price Range: High to Low" -> viewModel.getReportedList(10, 0, "products", 0, "created_at")*/
                "Most Reported" -> viewModel.getReportedList(10, 0, "products", 0, "created_at")
                /*  "Approved Product" -> viewModel.getReportedList(10, 0, "products", 0, "created_at")
                  "Declined Product" -> viewModel.getReportedList(10, 0, "products", 0, "created_at")*/
                else -> viewModel.getReportedList(10, 0, "products", null, "")
            }
        } else {
            Toast.makeText(requireContext(), "Please check your internet", Toast.LENGTH_SHORT)
                .show()
        }

        reportedListAdpater =
            ReportedListAdpater(context, object : ReportedListAdpater.SelectedDeleteInterface {
                override fun onClick(data: String) {
                    var dialog = Dialog(requireContext())
                    dialog.setContentView(R.layout.dialog_cancel_user)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    dialog.show()
                }
            },
                object : ReportedListAdpater.ViewDetailsInterface {
                    override fun onClick(data: String, id: String) {
                        /* var bundle = Bundle()
                         bundle.putString("btnText", data)
                         bundle.putString("id", id)*/

                        val args = Bundle().apply {
                            putString("postId", id)
                            putBoolean("isSeller", true)
                            putString("postTitle", "Camera")
                            putBoolean("isAdmin", true)
                            putBoolean("isReported", true)


                        }
                        view!!.findNavController().navigate(R.id.postDetails, args)
                    }

                },
                object : ReportedListAdpater.RemoveInterface {
                    override fun onClick(data: String, id: String) {
                        removePost(id)
                    }

                }

            )







/*
        productListAdapter= ProductListAdapter(context, object :  ProductListAdapter.ViewDetailsInterface {
            override fun onClick(data: String, id: String) {
                var bundle = Bundle()
                bundle.putString("btnText", data)
                bundle.putString("id", id)
                view!!.findNavController().navigate(R.id.aboutFragment, bundle)
            }
        },
            object :  ProductListAdapter.ApproveInterface {
                override fun onClick(data: String, id: String) {
                    approveOrReject = "Product Approved Id"
                    idForToast = ": $id"
                    products = ArrayList()
                    products.clear()
                    products.add(id)
                    viewModel.updateStatus(
                        "approved",
                        UpdateProductStatusBody(products = products)
                    )
                }
            },
            object : ProductListAdapter.DeclineInterface {
                override fun onClick(data: String, id: String) {
                    approveOrReject = "Product Decline Id"
                    idForToast = ": $id"
                    declinePost(id)
                }
            }
        )
*/


        binding.rvReportProduct.layoutManager = LinearLayoutManager(requireContext())

        binding.rvReportProduct.adapter = reportedListAdpater




    }


    private fun removePost(id: String) {
        val dialog = Dialog(requireContext(), R.style.Theme_Dialog)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE) // if you have blue line on top of your dialog, you need use this code
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_decline_product)

        val btnDecline = dialog.findViewById(R.id.btnDecline) as Button
        val btnCancel = dialog.findViewById(R.id.btnCancel) as Button
        val rdOther = dialog.findViewById(R.id.rdOther) as RadioButton
        val rgDecline = dialog.findViewById(R.id.rgDecline) as RadioGroup
        val etOther = dialog.findViewById(R.id.etOther) as EditText
        val tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle) as TextView
        val tvDesc = dialog.findViewById(R.id.tvDesc) as TextView

        tvDialogTitle.text = getString(R.string.remove_Product)
        tvDesc.text = getString(R.string.select_reason_for_remove_product)
        btnDecline.text = getString(R.string.remove)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnDecline.setOnClickListener {
            dialog.dismiss()
            if (requireContext().isInternetAvailable()) {
                viewModel.removeList(id)
            } else {
                Toast.makeText(requireContext(), "Please check your internet", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        rgDecline.setOnCheckedChangeListener { group, checkedId ->
            requireContext().hideKeyboardFrom(etOther)
            etOther.isEnabled = false
            etOther.setText("")
            etOther.setHint(getString(R.string.others))
            rdOther.isChecked = false
        }

        rdOther.setOnCheckedChangeListener { compoundButton, check ->
            if (check && compoundButton.isPressed) {
                rgDecline.clearCheck()
                etOther.isEnabled = true
                etOther.setText("")
                etOther.requestFocus()
                etOther.showKeyboard()
                rdOther.isChecked = true
            }
        }

        dialog.show()
    }



/*
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = SharedPrefsHelper.getUserCommon()

        productListedAdapter = ProductListedAdapter(onTab = {

            val args = Bundle().apply {
                putString("postId", "2")
                putBoolean("isSeller", true)
                putString("postTitle", "Camera")
                putBoolean("isAdmin", true)
                putBoolean("isReported", true)
            }
            findNavController().navigate(R.id.postDetails, args)

        }, onDecline = {

        }, onRemove = {
            removePost()
        })

        binding.rvReportProduct.layoutManager = LinearLayoutManager(requireContext())

        binding.rvReportProduct.adapter = productListedAdapter

        setReportProductData()
    }
*/

/*
    private fun removePost() {
        val dialog = Dialog(requireContext(), R.style.Theme_Dialog)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE) // if you have blue line on top of your dialog, you need use this code
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_decline_post)

        val btnDecline = dialog.findViewById(R.id.btnDecline) as Button
        val btnCancel = dialog.findViewById(R.id.btnCancel) as Button
        val rdOther = dialog.findViewById(R.id.rdOther) as RadioButton
        val rgDecline = dialog.findViewById(R.id.rgDecline) as RadioGroup
        val etOther = dialog.findViewById(R.id.etOther) as EditText
        val tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle) as TextView
        val tvDesc = dialog.findViewById(R.id.tvDesc) as TextView

        tvDialogTitle.text = getString(R.string.remove_Post)
        tvDesc.text = getString(R.string.select_reason_for_remove_post)
        btnDecline.text = getString(R.string.remove)

        btnCancel.setOnClickListener {

            dialog.dismiss()
        }

        rgDecline.setOnCheckedChangeListener { group, checkedId ->
            requireContext().hideKeyboardFrom(etOther)
            etOther.isEnabled = false
            etOther.setText("")
            etOther.setHint(getString(R.string.others))
            rdOther.isChecked = false
        }

        rdOther.setOnCheckedChangeListener { compoundButton, check ->
            if (check && compoundButton.isPressed) {
                rgDecline.clearCheck()
                etOther.isEnabled = true
                etOther.setText("")
                etOther.requestFocus()
                etOther.showKeyboard()
                rdOther.isChecked = true
            }
        }

        dialog.show()
    }
*/

/*
    private fun setReportProductData() {
        val tempDataArray: ArrayList<TempData> = ArrayList()

        val tempData = TempData()
        tempData.name = "Cameras"
        tempDataArray.add(tempData)

        val tempData2 = TempData()
        tempData2.name = "Recoders"
        tempDataArray.add(tempData2)

        val tempData3 = TempData()
        tempData3.name = "Equipments"
        tempDataArray.add(tempData3)

        val tempData4 = TempData()
        tempData4.name = "Lens"
        tempDataArray.add(tempData4)

        val tempData5 = TempData()
        tempData5.name = "View All"
        tempDataArray.add(tempData5)

        productListedAdapter.swapList(tempDataArray, true)


    }
*/
}