package com.albertkingdom.loginsignuptest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.albertkingdom.loginsignuptest.adapter.OnItemClickListener
import com.albertkingdom.loginsignuptest.adapter.PostsAdapter
import com.albertkingdom.loginsignuptest.viewModel.MyViewModel

class PostListFragment:Fragment(R.layout.list_fragment) {
    private  val viewModel: MyViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostsAdapter



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(!viewModel.checkIsLogIn()) {
            findNavController().navigate(R.id.loginFragment)
        }
        viewModel.getPost()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerview_list)
        adapter = PostsAdapter()

        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                //println("click position...$position")
                viewModel.clickedPostPosition = position

                findNavController().navigate(R.id.commentListFragment)
            }

            override fun onItemClick(email: String) {
                //println("click email $email")
                viewModel.userEmailToBeShowInProfile = email
                findNavController().navigate(R.id.othersProfileFragment)
            }

            override fun onClickLike(position: Int) {
                println("like post position...$position")

            }
        })
        viewModel.postList.observe(
            viewLifecycleOwner,
            {
                list ->
                adapter.submitList(list)
            }
        )
    }


}

