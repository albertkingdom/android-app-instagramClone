package com.albertkingdom.loginsignuptest

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.albertkingdom.loginsignuptest.adapter.CommentAdapter
import com.albertkingdom.loginsignuptest.util.AlertDialogUtil.Companion.showAlertDialog
import com.albertkingdom.loginsignuptest.util.AlertReason
import com.albertkingdom.loginsignuptest.viewModel.MyViewModel
import com.facebook.drawee.view.SimpleDraweeView

class CommentListFragment:Fragment(R.layout.comment_list_fragment) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CommentAdapter
    private val viewModel: MyViewModel by activityViewModels()
    private lateinit var userImageDefault: ImageView
    private lateinit var userImage: SimpleDraweeView
    private lateinit var editTextView: EditText
    val TAG = "CommentListFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        recyclerView = view.findViewById(R.id.recyclerview_commentList)
        adapter = CommentAdapter()
        recyclerView.adapter = adapter


        viewModel.commentList.observe(viewLifecycleOwner) { listOfComment ->
            adapter.submitList(listOfComment)
        }

        userImageDefault = view.findViewById(R.id.user_image_default)
        userImage = view.findViewById(R.id.user_image)
        editTextView = view.findViewById(R.id.new_comment_edit_text)

        if (viewModel.checkIsLogIn()) {
            userImageDefault.visibility = View.GONE
            userImage.setImageURI(Uri.parse(viewModel.auth.currentUser?.photoUrl.toString()), null)
        } else {
            userImage.visibility = View.GONE
        }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.comment_list_fragment_menu,menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.send_button -> {

                val newCommentContent = editTextView.text.toString()

                if (viewModel.checkIsLogIn() && newCommentContent.isNotEmpty()) {
                    viewModel.addComment(newCommentContent)
                    navigationBack()
                } else if (viewModel.checkIsLogIn() && newCommentContent.isEmpty()){
                    showAlertDialog(AlertReason.EMPTY_COMMENT, requireContext())
                } else {
                    showAlertDialog(AlertReason.NOT_LOGIN, requireContext())
                }


                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    fun navigationBack() {
        findNavController().popBackStack()
    }

}