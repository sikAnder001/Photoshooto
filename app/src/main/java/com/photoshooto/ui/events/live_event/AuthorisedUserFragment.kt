package com.photoshooto.ui.events.live_event

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.photoshooto.R
import com.photoshooto.databinding.FragmentAuthorisedUserBinding
import com.photoshooto.ui.events.live_event.adapter.AuthorisedUserAdapter

class AuthorisedUserFragment : Fragment() {

    lateinit var binding: FragmentAuthorisedUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthorisedUserBinding.inflate(inflater, container, false)

        showUser()

        return binding.root
    }

    private fun showUser() {
        var authorizedAdapter =
            AuthorisedUserAdapter(context, object : AuthorisedUserAdapter.SelectedDetailInterface {
                override fun onClick(data: String) {
                    var dialog = Dialog(requireContext())
                    dialog.setContentView(R.layout.dialog_cancel_user)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    dialog.show()
                }
            },
                object : AuthorisedUserAdapter.SelectedDeleteInterface {
                    override fun onClick(data: String) {

                    }

                }
            )

        binding.rvAuthorizedUser.adapter = authorizedAdapter

        authorizedAdapter.setList(dataFun())

    }

    private fun dataFun(): ArrayList<String> {
        var data = ArrayList<String>()
        data.add("Shiv")
        data.add("Shubham")
        data.add("Rohit")
        return data
    }

}