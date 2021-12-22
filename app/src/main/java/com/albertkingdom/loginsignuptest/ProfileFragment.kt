package com.albertkingdom.loginsignuptest

import android.net.Uri

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.albertkingdom.loginsignuptest.viewModel.MyViewModel
import com.facebook.drawee.view.SimpleDraweeView
import com.google.firebase.auth.FirebaseAuth


class ProfileFragment:Fragment(R.layout.profile_fragment) {

    private lateinit var auth: FirebaseAuth
    private val viewModel: MyViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        auth = (activity as MainActivity).auth

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //val currentUser = auth.currentUser
        val displayName = view.findViewById<TextView>(R.id.user_name)
        val updateProfileButton = view.findViewById<Button>(R.id.btn_update_profile)
        val userImageView = view.findViewById<SimpleDraweeView>(R.id.user_image)
        val defaultUserImageView = view.findViewById<ImageView>(R.id.user_image_default)
        if (viewModel.auth.currentUser == null) {
            updateProfileButton.isEnabled = false
            userImageView.visibility = View.GONE

        } else {
            displayName.text = viewModel.auth.currentUser!!.displayName
            defaultUserImageView.visibility = View.GONE
            Log.d("Profile fragment", viewModel.auth.currentUser!!.email.toString())

            userImageView.setImageURI(Uri.parse(viewModel.auth.currentUser?.photoUrl.toString()), null)

            updateProfileButton.setOnClickListener {
                navigateToUpdateProfile()
            }
        }


    }

    private fun navigateToUpdateProfile() {
        findNavController().navigate(R.id.updateProfileFragment)
    }
}