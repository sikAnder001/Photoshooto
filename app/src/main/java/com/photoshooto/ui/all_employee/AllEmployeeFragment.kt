package com.photoshooto.ui.all_employee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.photoshooto.R
import com.photoshooto.api_call_package.Repository2
import com.photoshooto.api_call_package.view_model.AllEmployeesVMFactory
import com.photoshooto.api_call_package.view_model.AllEmployeesViewModel
import com.photoshooto.databinding.FragmentAllEmployeeBinding
import com.photoshooto.ui.admin_screen.AdminDashboardActivity
import com.photoshooto.util.isInternetAvailable

class AllEmployeeFragment : Fragment() {

    private lateinit var binding: FragmentAllEmployeeBinding
   // private val viewModel: ManageUsersViewModel by viewModel()
    private lateinit var printoEmployeesAdapter: PrintoEmployeesAdapter
    private lateinit var psEmployeesAdpater: PSEmployeesAdpater

    private lateinit var repository: Repository2
    private lateinit var viewModel: AllEmployeesViewModel
    private lateinit var factory: AllEmployeesVMFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllEmployeeBinding.inflate(inflater, container, false)

        repository = Repository2()

        factory = AllEmployeesVMFactory(repository)

        viewModel = ViewModelProvider(this, factory)[AllEmployeesViewModel::class.java]
        showAllEmployees()
        showPrintoEmployees()

        initpsObserver()
        initprintoObserver()
        //initObserver()
        //allAdpaders()
        return binding.root
    }

/*
    private fun initObserver2() {
        viewModel.apply {
            userListResponse.observe(
                requireActivity(),
                Observer { result ->
                    result.success?.let {
                        if (it) {
                            result?.data?.list?.let {
                                binding.apply {
                                    recyclerViewPsEmployee.layoutManager = GridLayoutManager(
                                        requireContext(),
                                        2,
                                        GridLayoutManager.VERTICAL,
                                        false

                                    )
                                }
                            }
                        } else {
                            result.message?.let { msg -> onToast(msg, requireContext()) }
                        }
                    }
                }
            )
        }
    }
*/


    private fun initpsObserver() {
        with(viewModel) {
            getAllEmployeesResponse.observe(viewLifecycleOwner) {
                if (it.isSuccessful && it.code() == 200) {
                    val data = it.body()!!.message
                    psEmployeesAdpater.setList(it.body()!!.data!!.list)

                    Toast.makeText(context, it.body()?.data.toString(), Toast.LENGTH_SHORT).show()


                } else {
                    Toast.makeText(context, it.message().toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun initprintoObserver() {
        with(viewModel) {
            getAllEmployeesResponse.observe(viewLifecycleOwner) {
                if (it.isSuccessful && it.code() == 200) {
                    val data = it.body()!!.message
                    it.body()?.data?.list?.let { it1 -> printoEmployeesAdapter.setList(it1) }


                } else {
                    Toast.makeText(context, it.message().toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()
    }




    private fun clickListener() {
        binding.apply {
            ivBack.setOnClickListener {
                (activity as AdminDashboardActivity).drawerOpen()
            }


            viewall.setOnClickListener {
                view?.findNavController()
                    ?.navigate(
                        R.id.action_navigation_new_all_employee_to_psEmpployeeFragment
                    )


              /*  view?.findNavController()
                    ?.navigate(
                        R.id.action_navigation_new_all_employee_to_employeeDetailsFragment
                    )*/

            }

            viewall2.setOnClickListener {
                view?.findNavController()
                    ?.navigate(
                        R.id.action_navigation_new_all_employee_to_printoEmpployeeFragment
                    )

            }


        }
    }






/*
    private fun allAdpaders() {
        val array = ArrayList<AllEmployeesBeans>()
        array.add(
            AllEmployeesBeans(
                R.drawable.ic_temp_user_profile,
                "Purv Chatterji",
                "Admin 01 (PS Employee)",
                "Employee ID : PS22ADM012"
            )
        )

        val psEmployeesAdpater = PSEmployeesAdpater(array)
       binding.recyclerViewPsEmployee.layoutManager =
            GridLayoutManager(context, 2)
        binding.recyclerViewPsEmployee.adapter = psEmployeesAdpater


        val arrays = ArrayList<AllEmployeesBeans>()
        array.add(
            AllEmployeesBeans(
                R.drawable.ic_temp_user_profile,
                "Purv Chatterji",
                "Admin 01 (PS Employee)",
                "Employee ID : PS22ADM012"
            )
        )

        val printoEmployeesAdpater = PrintoEmployeesAdapter(array)
        binding.recyclerViewPrintoEmployee.layoutManager =
            GridLayoutManager(context, 2)
        binding.recyclerViewPrintoEmployee.adapter = printoEmployeesAdpater



    }
*/

    private fun showAllEmployees() {
        if (requireContext().isInternetAvailable()) {
            viewModel.getAllUsers(10, 0, "created_at",-1,"photographer" )
        }
        else {
            Toast.makeText(requireContext(), "Please check your internet", Toast.LENGTH_SHORT)
                .show()
        }

        psEmployeesAdpater=PSEmployeesAdpater(context, object :  PSEmployeesAdpater.ViewDetailsInterface {
            override fun onClick(id: String) {
                /*    var bundle = Bundle()
                  bundle.putString("btnText", data)
                  bundle.putString("id", id)*/
                // view!!.findNavController().navigate(R.id.postDetails, bundle)
            }

        }
        )

        binding.recyclerViewPsEmployee.layoutManager =
            GridLayoutManager(context, 2)
        binding.recyclerViewPsEmployee.adapter = psEmployeesAdpater
    }


    private fun showPrintoEmployees() {
        if (requireContext().isInternetAvailable()) {
            viewModel.getAllUsers(8, 0, "created_at",-1,"user" )
        }
        else {
            Toast.makeText(requireContext(), "Please check your internet", Toast.LENGTH_SHORT)
                .show()
        }

        printoEmployeesAdapter= PrintoEmployeesAdapter(context, object :  PrintoEmployeesAdapter.ViewDetailsInterface {
            override fun onClick(id: String) {
                /*    var bundle = Bundle()
                  bundle.putString("btnText", data)
                  bundle.putString("id", id)*/
                // view!!.findNavController().navigate(R.id.postDetails, bundle)
            }

        }
        )
        binding.recyclerViewPrintoEmployee.layoutManager =
            GridLayoutManager(context, 2)
        binding.recyclerViewPrintoEmployee.adapter = printoEmployeesAdapter
    }





}
