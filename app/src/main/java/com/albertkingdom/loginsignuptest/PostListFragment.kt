package com.albertkingdom.loginsignuptest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.albertkingdom.loginsignuptest.adapter.*
import com.albertkingdom.loginsignuptest.util.OnPostItemClickListener
import com.albertkingdom.loginsignuptest.viewModel.MyViewModel

class PostListFragment : Fragment(R.layout.list_fragment_new) {
    private val viewModel: MyViewModel by activityViewModels()
    private lateinit var postRecyclerView: RecyclerView
    private lateinit var postAdapter: ParentPostAdapter



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!viewModel.checkIsLogIn()) {
            findNavController().navigate(R.id.loginFragment)
        }
        viewModel.getPost()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postRecyclerView = view.findViewById(R.id.recyclerview_list_test)

        postRecyclerView.setHasFixedSize(true)

        setupPostAdapter()

        viewModel.auth.currentUser?.email.let {
            if (it != null){
                postAdapter.setCurrentLoginUserEmail(it)
            }
        }


        viewModel.postList.observe(viewLifecycleOwner) { list ->
            println("observe postlist $list")

            postAdapter.data = list
            postAdapter.notifyDataSetChanged()
        }


    }

    fun setupPostAdapter() {
        postAdapter = ParentPostAdapter()

        postAdapter.setCurrentLoginUserEmail(viewModel.auth.currentUser?.email!!)
        postRecyclerView.adapter = postAdapter

        postAdapter.setOnItemClickListener(object : OnPostItemClickListener {
            override fun onClickCommentButton(position: Int) {
                //println("click position...$position")
                viewModel.clickedPostPosition.value = position

                findNavController().navigate(R.id.action_postListFragment2_to_commentListFragment2)
            }

            override fun onClickEmail(email: String) {
                //println("click email $email")
                viewModel.userEmailToBeShowInProfile = email
                findNavController().navigate(R.id.action_postListScreen_to_othersProfileFragment)
            }

            override fun onAddLike(position: Int) {
                viewModel.addToLike(position)
            }

            override fun onRemoveLike(position: Int) {
                viewModel.removeLike(position)
            }
        })

        val storyClickListener =  object : onStoryClickListener {
            override fun clickImage(position: Int) {
                Log.d("storyAdapter","click story...$position")
                val storyDetailFragment = StoryDetailFragment()
                if (viewModel.postList.value != null) {

                    //storyDetailFragment.postList = viewModel.postList.value!!
                    storyDetailFragment.currentImageIndex = position
                }
//                println("viewmodel postlist...${viewModel.postList.value}")
                storyDetailFragment.show(parentFragmentManager,"")
            }

        }
        postAdapter.setClickStoryItemListener(storyClickListener)
    }
}

