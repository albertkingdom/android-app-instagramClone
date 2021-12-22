package com.albertkingdom.loginsignuptest

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.albertkingdom.loginsignuptest.util.AlertDialogUtil
import com.albertkingdom.loginsignuptest.util.AlertReason
import com.albertkingdom.loginsignuptest.viewModel.MyViewModel

class NewArticleFragment: Fragment(R.layout.new_article_fragment) {
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private  val viewModel: MyViewModel by activityViewModels()
    private var imageRelativePath: Uri? = null
  val TAG = "NewArticleFragment"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
//        auth = (activity as MainActivity).auth
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView = view.findViewById(R.id.image)
        textView = view.findViewById(R.id.text_input)
        setPickImageListener()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.new_article_fragment_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.share_button -> {
                Log.d("newarticlefragment","share button click")
                val postContent = textView.text.toString()

                if (imageRelativePath != null && viewModel.checkIsLogIn()) {
                    val userEmail = viewModel.auth.currentUser?.email.toString()
                    viewModel.uploadToFirebase(
                        postContent,
                        imageRelativePath!!,
                        requireContext(),
                        userEmail
                    )
                    navigateToList()
                } else if (imageRelativePath == null && viewModel.checkIsLogIn()){
                    AlertDialogUtil.showAlertDialog(AlertReason.EMPTY_IMAGE, requireContext())
                } else {
                    AlertDialogUtil.showAlertDialog(AlertReason.NOT_LOGIN, requireContext())
                }

                return true
            }
        }


        return super.onOptionsItemSelected(item)
    }

    // alternative image picker
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){ result: Uri? ->
        if(result != null) {
            imageView.setImageURI(result)
           imageRelativePath = result
            Log.d(TAG, imageRelativePath.toString())
        }
    }

    private fun setPickImageListener() {
        imageView.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun navigateToList() {
        findNavController().navigate(R.id.postListFragment)

    }


}