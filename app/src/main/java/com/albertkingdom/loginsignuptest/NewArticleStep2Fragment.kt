package com.albertkingdom.loginsignuptest

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.albertkingdom.loginsignuptest.viewModel.MyViewModel
import com.google.android.material.textfield.TextInputLayout
import java.io.File


class NewArticleStep2Fragment: Fragment(R.layout.new_article_step_2_fragment) {
    val TAG = "NewArticleStep2Fragment"
    private val viewModel: MyViewModel by activityViewModels()
    private lateinit var imageView: ImageView
    private lateinit var textView: TextInputLayout
    private lateinit var button: Button
    private var cameraPhotoPath: String? = null
    private var imageRelativePath: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val galleryPhoto = arguments?.get("uri")
        val cameraPhoto = arguments?.get("cameraPhoto")
        Log.d(TAG, "argument.cameraphoto..${cameraPhoto}")
        Log.d(TAG, "argument.galleryPhoto..${galleryPhoto}")
        imageView = view.findViewById(R.id.imageView)
        textView = view.findViewById(R.id.text_input)
        button = view.findViewById(R.id.share_button)


        if (cameraPhoto != null) {
            Log.d(TAG, "use cameraPhoto")
            imageView.setImageURI(Uri.parse(cameraPhoto as String))
            cameraPhotoPath = cameraPhoto as String
        } else {
            Log.d(TAG, "use galleryphoto")

            imageView.setImageURI(Uri.parse(galleryPhoto as String))
            imageRelativePath = Uri.parse(galleryPhoto as String)
        }

        button.setOnClickListener {
            sharePost()
        }

    }

    private fun sharePost() {
        Log.d(TAG,"share button click")
        val postContent = textView.editText.toString()
        val userEmail = viewModel.auth.currentUser?.email.toString()

        // if imageRelativePath is null -> use cameraPhotoPath instead
        val uploadPhotoUri = imageRelativePath ?: Uri.fromFile(File(cameraPhotoPath!!))
        val stream = activity?.contentResolver?.openInputStream(uploadPhotoUri)
        if (stream != null) {
            viewModel.uploadToFirebaseWithStream(
                postContent,
                stream,
                userEmail
            )
        }
        navigateToList()

    }

    private fun navigateToList() {
        findNavController().navigate(R.id.postlist)
    }



}