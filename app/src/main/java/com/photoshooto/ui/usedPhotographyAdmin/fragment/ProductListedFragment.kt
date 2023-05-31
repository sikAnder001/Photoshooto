package com.photoshooto.ui.usedPhotographyAdmin.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.photoshooto.R
import com.photoshooto.api_call_package.ProductListVMFactory
import com.photoshooto.api_call_package.Repository2
import com.photoshooto.api_call_package.view_model.ProductListViewModel
import com.photoshooto.databinding.FragmentProductListedBinding
import com.photoshooto.domain.model.ApproveRejectBody
import com.photoshooto.domain.model.UpdateProductStatusBody
import com.photoshooto.domain.model.User
import com.photoshooto.ui.admin_screen.manage_job_posting.JobPostedAdapter
import com.photoshooto.ui.job.utility.hideKeyboardFrom
import com.photoshooto.ui.usedPhotographyAdmin.adapter.ProductListAdapter
import com.photoshooto.util.isInternetAvailable
import com.photoshooto.util.showKeyboard

class ProductListedFragment(val param: String) : Fragment() {

    lateinit var binding: FragmentProductListedBinding

    private lateinit var repository: Repository2
    private lateinit var viewModel: ProductListViewModel
    private lateinit var factory: ProductListVMFactory
    lateinit var products: ArrayList<String>

    var user: User? = null
    var idForToast = ""
    var approveOrReject = ""


    private lateinit var productListAdapter: ProductListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProductListedBinding.inflate(inflater, container, false)

        repository = Repository2()

        factory = ProductListVMFactory(repository)

        viewModel = ViewModelProvider(this, factory)[ProductListViewModel::class.java]
        showProductList()

        initObserver()


        return binding.root
    }


    private fun initObserver() {
        with(viewModel) {
            getProductListResponse.observe(viewLifecycleOwner) {
                if (it.isSuccessful && it.code() == 200) {
                    val data = it.body()!!.message
                    productListAdapter.setList(it.body()!!.data!!.list)


                } else {
                    Toast.makeText(context, it.message().toString(), Toast.LENGTH_SHORT).show()
                }
            }

            approveDeclineResponse.observe(viewLifecycleOwner) {
                if (it.isSuccessful && it.code() == 200) {
                    val data = it.body()!!.message

                    val inflater = layoutInflater
                    val layout: View = inflater.inflate(
                        R.layout.custom_toast,
                        view!!.findViewById(R.id.toast_layout_root) as ViewGroup?
                    )


                    val approveDeclineTv =
                        layout.findViewById<View>(R.id.approveOrDeclineTv) as TextView
                    val toastId = layout.findViewById<View>(R.id.toastId) as TextView
                    val imgView = layout.findViewById<View>(R.id.image) as ImageView
                    approveDeclineTv.text = approveOrReject
                    toastId.text = idForToast

                    if (approveOrReject == "Product Decline Id") {
                        layout.setBackgroundResource(R.drawable.orange_text_bg)
                        imgView.setImageResource(R.drawable.orange_tick)
                        toastId.setTextColor(ContextCompat.getColor(context!!, R.color.orange_clr))
                    } else {
                        layout.setBackgroundResource(R.drawable.light_green_back)
                        imgView.setImageResource(R.drawable.ic_green_tick_circle)
                        toastId.setTextColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.green_status
                            )
                        )
                    }

                    val toast = Toast(requireActivity().applicationContext)
                    toast.setGravity(Gravity.BOTTOM, 0, 0)
                    toast.duration = Toast.LENGTH_LONG
                    toast.setView(layout)
                    toast.show()



                    when (param) {

                        "Recently Updated" -> viewModel.getProductList(10, 0, -1, "")
                        /* "Price Range: Low to High" -> viewModel.getProductList(10, 0, 0, "price","min_price")*/
                        /* "Price Range: High to Low" -> viewModel.getProductList(10, 0, 0, "","")*/
                        "Most Reported" -> viewModel.getProductList(10, 0, 0, "created_at")
                        /*  "Approved Product" -> viewModel.getProductList(10, 0, null, "","approved")
                          "Declined Product" -> viewModel.getProductList(10, 0, null, "","rejected")*/
                        else ->viewModel.getProductList(15, 0, null, "")


                        /* "Recently Updated" -> viewModel.getProductList(10, 0, -1, "","")
                         "Price Range: Low to High" -> viewModel.getProductList(10, 0, 1, "price","")
                         "Price Range: High to Low" -> viewModel.getProductList(10, 0, 0, "price","")
                         "Most Reported" -> viewModel.getProductList(10, 0, 0, "created_at","")
                         "Approved Product" -> viewModel.getProductList(10, 0, null, "","approved")
                         "Declined Product" -> viewModel.getProductList(10, 0, null, "","rejected")
                         else ->viewModel.getProductList(10, 0, null, "null","null")*/
                    }
                    productListAdapter.notifyDataSetChanged()

                } else {
                    Toast.makeText(context, it.message().toString(), Toast.LENGTH_SHORT).show()
                }
            }




        }
    }



    /*  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
          super.onViewCreated(view, savedInstanceState)
          user = SharedPrefsHelper.getUserCommon()
          productListedAdapter = ProductListedAdapter(onTab = {
              val args = Bundle().apply {
                  putString("postId", "2")
                  putBoolean("isSeller", true)
                  putString("postTitle", "Camera")
                  putBoolean("isAdmin", true)
                  putBoolean("isReported", false)
              }
              view.findNavController().navigate(R.id.postDetails, args)
          }, onDecline = {
              declinePost()
          }, onRemove = {
          })
          binding.rvProductListed.layoutManager = LinearLayoutManager(requireContext())
          binding.rvProductListed.adapter = productListedAdapter
          setProductListedData()
      }*/



    private fun showProductList() {
        if (requireContext().isInternetAvailable()) {

            when (param) {


                "Recently Updated" -> viewModel.getProductList(10, 0, -1, "")
                /* "Price Range: Low to High" -> viewModel.getProductList(10, 0, 0, "price","min_price")*/
                /* "Price Range: High to Low" -> viewModel.getProductList(10, 0, 0, "","")*/
                "Most Reported" -> viewModel.getProductList(10, 0, 0, "created_at")
                /* "Approved Product" -> viewModel.getProductList(10, 0, null, "")
                 "Declined Product" -> viewModel.getProductList(10, 0, null, "")*/
                else ->viewModel.getProductList(15, 0, null, "")


                /* "Recently Updated" -> viewModel.getProductList(10, 0, -1, "","")
                 "Price Range: Low to High" -> viewModel.getProductList(10, 0, 1, "price","")
                  "Price Range: High to Low" -> viewModel.getProductList(10, 0, 0, "price","")
                 "Most Reported" -> viewModel.getProductList(10, 0, 0, "created_at","")
                   "Approved Product" -> viewModel.getProductList(10, 0, null, "","approved")
                   "Declined Product" -> viewModel.getProductList(10, 0, null, "","rejected")
                 else ->viewModel.getProductList(10, 0, null, "","")*/
            }
        } else {
            Toast.makeText(requireContext(), "Please check your internet", Toast.LENGTH_SHORT)
                .show()
        }




        productListAdapter= ProductListAdapter(context, object :  ProductListAdapter.ViewDetailsInterface {
            override fun onClick(data: String, id: String) {
                /*    var bundle = Bundle()
                  bundle.putString("btnText", data)
                  bundle.putString("id", id)*/



                val args = Bundle().apply {
                    putString("postId", id)
                    putBoolean("isSeller", true)
                    putString("postTitle", "Camera")
                    putBoolean("isAdmin", true)
                    putBoolean("isReported", false)

                }

                view!!.findNavController().navigate(R.id.postDetails,args)
//                 view!!.findNavController().navigate(R.id.postDetails, bundle)
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
                    declineProducts(id)
                }
            }
        )


        binding.rvProductListed.layoutManager = LinearLayoutManager(requireContext())

        binding.rvProductListed.adapter = productListAdapter




    }


    /* private fun declinePost() {
         val dialog = Dialog(requireContext(), R.style.Theme_Dialog)
         dialog.window?.requestFeature(Window.FEATURE_NO_TITLE) // if you have blue line on top of your dialog, you need use this code
         dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         dialog.setCancelable(false)
         dialog.setContentView(R.layout.dialog_decline_post)
         val btnCancel = dialog.findViewById(R.id.btnCancel) as Button
         val rdOther = dialog.findViewById(R.id.rdOther) as RadioButton
         val rgDecline = dialog.findViewById(R.id.rgDecline) as RadioGroup
         val etOther = dialog.findViewById(R.id.etOther) as EditText
         val tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle) as TextView
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
     private fun setProductListedData() {
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
         productListedAdapter.swapList(tempDataArray, false)
     }*/

    private fun declineProducts(id: String) {
        val dialog = Dialog(requireContext(), R.style.Theme_Dialog)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE) // if you have blue line on top of your dialog, you need use this code
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_decline_product)

        val btnCancel = dialog.findViewById(R.id.btnCancel) as Button
        val btnDecline = dialog.findViewById(R.id.btnDecline) as Button
        val rdOther = dialog.findViewById(R.id.rdOther) as RadioButton
/*        val pdName = dialog.findViewById(R.id.pd_name) as TextView*/
        val rgDecline = dialog.findViewById(R.id.rgDecline) as RadioGroup
        val etOther = dialog.findViewById(R.id.etOther) as EditText
        val tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle) as TextView

        products = ArrayList()
        products.clear()
        products.add(id)

        btnCancel.setOnClickListener {

            dialog.dismiss()
        }

        // pdName.text

        rgDecline.setOnCheckedChangeListener { group, checkedId ->
            requireContext().hideKeyboardFrom(etOther)
            etOther.isEnabled = false
            etOther.setText("")
            etOther.hint = getString(R.string.others)
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

        btnDecline.setOnClickListener {
            var id: Int = rgDecline.checkedRadioButtonId
            if (rdOther.isChecked) {
                if (etOther.text.isEmpty())
                    Toast.makeText(
                        context, "please provide reason",
                        Toast.LENGTH_SHORT
                    ).show()
                else {
                    viewModel.updateStatus(
                        "rejected", UpdateProductStatusBody(
                            products = products,
                            // etOther.text as String?
                        )
                    )
                    dialog.dismiss()
                }
            } else if (id != -1) {
                val radio: RadioButton = dialog.findViewById(id)
                viewModel.updateStatus(
                    "rejected", UpdateProductStatusBody(
                        products = products,
                        //  radio.text as String?
                    )
                )
                dialog.dismiss()
            }
        }
        dialog.show()
    }

}