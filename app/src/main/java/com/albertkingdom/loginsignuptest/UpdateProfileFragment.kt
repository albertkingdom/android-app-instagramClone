package com.albertkingdom.loginsignuptest

import android.app.Activity
import android.app.Activity.RESULT_OK
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import androidx.core.app.ActivityCompat.startActivityForResult

import android.provider.MediaStore

import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.albertkingdom.loginsignuptest.api.ImgurApi
import com.albertkingdom.loginsignuptest.util.FileUtil.Companion.getFileFromUri
import com.albertkingdom.loginsignuptest.viewModel.MyViewModel
import com.facebook.drawee.view.SimpleDraweeView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.net.URI


class UpdateProfileFragment : Fragment(R.layout.update_profile_fragment) {

//    private lateinit var auth: FirebaseAuth
    val TAG = "UpdateProfileFragment"
private val viewModel: MyViewModel by activityViewModels()
    private lateinit var imageView: ImageView
    private var imageRelativePath: Uri? = null
    private lateinit var imageImgurUrl: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //auth = (activity as MainActivity).auth

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView = view.findViewById<ImageView>(R.id.user_image)
        val btnCompleted = view.findViewById<Button>(R.id.btn_finish)
        val user = viewModel.auth.currentUser

        imageView.setOnClickListener {

            openGallery()
        }
        btnCompleted.setOnClickListener {
            val newName = (view.findViewById(R.id.displayName) as TextInputLayout).editText?.text.toString()
            viewModel.updateProfile(newName, imageRelativePath, requireContext())
            navigateToProfile()
        }
    }

//    private fun updateProfile(user: FirebaseUser?) {
//        if (user == null) {
//            Log.w("update profile fragment", "user is null")
//            return
//        }
//        val newName =
//            (view?.findViewById(R.id.displayName) as TextInputLayout).editText?.text.toString()
//        val profileUpdates = userProfileChangeRequest {
//            displayName = newName
//            photoUri = Uri.parse(imageImgurUrl)
//        }
//
//        user!!.updateProfile(profileUpdates)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Log.d("update profile fragment", "User profile updated.")
//
//                    navigateToProfile()
//                }
//            }
//
//    }

    private fun navigateToProfile() {
        findNavController().popBackStack()
    }

    private fun openGallery() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"

        //deprecated way:
        //startActivityForResult(intent, 1000)

        // original image picker
    // resultLauncher.launch(intent)
        // alternative image picker
        pickImageLauncher.launch("image/*")
    }
    //deprecated way:
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK && requestCode == 1000) {
//            val imageUri = data?.data
//            imageView.setImageURI(imageUri)
//        }
//    }
    // original image picker
    /*
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // the relative path of image picked
                val data = result.data?.data
                imageView.setImageURI(data)
                println("image, ${result.data}")

                if (data != null) {
                    uploadImage(data)
                }
            }
        }
*/
    // alternative image picker
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){
        if(it != null) {
            imageView.setImageURI(it)
            //uploadImage(it)
            imageRelativePath = it

            Log.d(TAG, imageRelativePath.toString())
        }
    }



}