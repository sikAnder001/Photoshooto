package com.photoshooto.ui.admin_screen.manage_admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.photoshooto.R
import com.photoshooto.databinding.FragmentCreateAdminBinding
import com.photoshooto.domain.model.CommonMultiSelectItem
import com.photoshooto.domain.usecase.manage_admin.ManageAdminViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditAdminFragment : Fragment() {

    private lateinit var binding: FragmentCreateAdminBinding
    private val viewModel: ManageAdminViewModel by viewModel()
    private var empType: String? = null
    private var languageKnown: String? = null
    private var gender: String? = null
    private var location: String? = null

    private val modulesList = ArrayList<CommonMultiSelectItem>()
    private val cityList = ArrayList<CommonMultiSelectItem>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_admin, container, false)
    }

}