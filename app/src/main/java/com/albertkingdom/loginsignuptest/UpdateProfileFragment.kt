package com.albertkingdom.loginsignuptest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.albertkingdom.loginsignuptest.viewModel.MyViewModel
import com.facebook.drawee.view.SimpleDraweeView



class UpdateProfileFragment : Fragment(R.layout.update_profile_fragment) {

    val TAG = "UpdateProfileFragment"
    private val viewModel: MyViewModel by activityViewModels()
    private lateinit var userImage: SimpleDraweeView
    private lateinit var userImageDefault: ImageView
    private var imageRelativePath: Uri? = null
    private lateinit var imageImgurUrl: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userImage = view.findViewById(R.id.user_image)
        userImageDefault = view.findViewById(R.id.user_image_new_default)
        val btnCompleted = view.findViewById<Button>(R.id.btn_finish)

        // setup user image
        userImageDefault.visibility = View.VISIBLE
        userImage.visibility = View.INVISIBLE

        if (viewModel.checkIsLogIn() && viewModel.auth.currentUser?.photoUrl != null) {
            userImage.visibility = View.VISIBLE
            userImageDefault.visibility = View.INVISIBLE
            userImage.setImageURI(Uri.parse(viewModel.auth.currentUser?.photoUrl.toString()), null)
        }

        userImage.setOnClickListener {

            openGallery()
        }
        btnCompleted.setOnClickListener {
            val newName = (view.findViewById(R.id.displayName) as TextInputLayout).editText?.text.toString()

            val stream = imageRelativePath?.let { uri ->
                activity?.contentResolver?.openInputStream(
                    uri
                )
            }
            viewModel.updateProfile(newName, stream)
            navigateToProfile()
        }
    }



    private fun navigateToProfile() {
        findNavController().popBackStack()
    }

    private fun openGallery() {

        pickImageLauncher.launch("image/*")
    }

    // alternative image picker
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){
        if(it != null) {
            userImage.setImageURI(it)

            imageRelativePath = it

            Log.d(TAG, imageRelativePath.toString())
        }
    }



}