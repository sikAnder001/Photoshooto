package com.photoshooto.ui.all_employee

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.photoshooto.R
import com.photoshooto.api_call_package.Repository2
import com.photoshooto.api_call_package.view_model.AllEmployeesVMFactory
import com.photoshooto.api_call_package.view_model.AllEmployeesViewModel
import com.photoshooto.databinding.FragmentAllEmployeeBinding
import com.photoshooto.databinding.FragmentPrintoEmployeesBinding
import com.photoshooto.ui.photographersScreens.photographerDashboard.fragments.model.AllEmployeesBeans
import com.photoshooto.util.isInternetAvailable


class PrintoEmployeesFragment : Fragment() {

    private lateinit var binding: FragmentPrintoEmployeesBinding
    private lateinit var printoEmployeesAdapter: PrintoEmployeesAdapter

    private lateinit var repository: Repository2
    private lateinit var viewModel: AllEmployeesViewModel
    private lateinit var factory: AllEmployeesVMFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPrintoEmployeesBinding.inflate(inflater, container, false)

        repository = Repository2()

        factory = AllEmployeesVMFactory(repository)

        viewModel = ViewModelProvider(this, factory)[AllEmployeesViewModel::class.java]
        showPrintoEmployees()


        initprintoObserver()

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
       // printoEmpAdpader()
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun  initprintoObserver() {
        with(viewModel) {
            getAllEmployeesResponse.observe(viewLifecycleOwner) {
                if (it.isSuccessful && it.code() == 200) {
                    val data = it.body()!!.message
                    printoEmployeesAdapter.setList(it.body()!!.data!!.list)


                } else {
                    android.widget.Toast.makeText(context, it.message().toString(), android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }

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



    /*private fun printoEmpAdpader() {
        val array = ArrayList<AllEmployeesBeans>()
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
    }*/



}