package com.albertkingdom.loginsignuptest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.albertkingdom.loginsignuptest.adapter.OnItemClickListener
import com.albertkingdom.loginsignuptest.adapter.PostsAdapter
import com.albertkingdom.loginsignuptest.adapter.StoryAdapter
import com.albertkingdom.loginsignuptest.adapter.onStoryClickListener
import com.albertkingdom.loginsignuptest.viewModel.MyViewModel

class PostListFragment : Fragment(R.layout.list_fragment) {
    private val viewModel: MyViewModel by activityViewModels()
    private lateinit var postRecyclerView: RecyclerView
    private lateinit var storyRecyclerView: RecyclerView
    private lateinit var postAdapter: PostsAdapter
    private lateinit var storyAdapter: StoryAdapter


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
        postRecyclerView = view.findViewById(R.id.recyclerview_list)
        storyRecyclerView = view.findViewById(R.id.recyclerview_story_list)
        postAdapter = PostsAdapter()
        storyAdapter = StoryAdapter()
        postAdapter.setCurrentLoginUserEmail(viewModel.auth.currentUser?.email!!)
        postRecyclerView.adapter = postAdapter
        storyRecyclerView.adapter = storyAdapter
        postAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                //println("click position...$position")
                viewModel.clickedPostPosition = position

                findNavController().navigate(R.id.action_postListFragment2_to_commentListFragment2)
            }

            override fun onItemClick(email: String) {
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

        storyAdapter.onClickListener = object : onStoryClickListener {
            override fun clickImage(position: Int) {
                Log.d("storyAdapter","click story...$position")
                val storyDetailFragment = StoryDetailFragment()
                if (viewModel.postList.value != null) {

                    storyDetailFragment.postList = viewModel.postList.value!!
                    storyDetailFragment.currentImageIndex = position
                }
//                println("viewmodel postlist...${viewModel.postList.value}")
                storyDetailFragment.show(parentFragmentManager,"")
            }

        }

        viewModel.postList.observe(
            viewLifecycleOwner,
            { list ->
                postAdapter.submitList(list)
                storyAdapter.submitList(list)
            }
        )


    }


}

