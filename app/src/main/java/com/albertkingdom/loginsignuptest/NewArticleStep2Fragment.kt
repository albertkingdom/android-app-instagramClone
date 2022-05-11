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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.albertkingdom.loginsignuptest.viewModel.MyViewModel
import com.albertkingdom.loginsignuptest.viewModel.NewArticleViewModel
import com.google.android.material.textfield.TextInputLayout
import java.io.File


class NewArticleStep2Fragment: Fragment(R.layout.new_article_step_2_fragment) {
    val TAG = "NewArticleStep2Fragment"
    private val newArticleViewModel: NewArticleViewModel by activityViewModels()
    private val viewModel: MyViewModel by activityViewModels()
    private lateinit var imageView: ImageView
    private lateinit var textView: TextInputLayout
    private lateinit var button: Button


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view.findViewById(R.id.imageView)
        textView = view.findViewById(R.id.text_input)
        button = view.findViewById(R.id.share_button)


        newArticleViewModel.imageRelativePath.observe(viewLifecycleOwner) { uri ->
            Log.d(TAG, "use galleryphoto $uri")
            imageView.setImageURI(uri)
        }

        newArticleViewModel.cameraPhotoPath.observe(viewLifecycleOwner) { uri ->
            Log.d(TAG, "use cameraPhoto $uri")
            imageView.setImageURI(Uri.parse(uri))

        }
        button.setOnClickListener {
            sharePost()
        }

    }

    private fun sharePost() {
        Log.d(TAG,"share button click")
        val postContent = textView.editText?.text.toString()
        val userEmail = viewModel.auth.currentUser?.email.toString()

        var uploadPhotoUri = newArticleViewModel.imageRelativePath.value
        if(newArticleViewModel.isUseCameraPhoto.value == true) {
            val absoluteUri = newArticleViewModel.cameraPhotoPath.value
            uploadPhotoUri = Uri.fromFile(absoluteUri?.let { File(it) })
        }
        println("uploadPhotoUri $uploadPhotoUri")
        val stream = uploadPhotoUri?.let { activity?.contentResolver?.openInputStream(it) }
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