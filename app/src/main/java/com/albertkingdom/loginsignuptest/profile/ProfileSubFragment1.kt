package com.albertkingdom.loginsignuptest.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.albertkingdom.loginsignuptest.R
import com.albertkingdom.loginsignuptest.adapter.OnItemClickListener
import com.albertkingdom.loginsignuptest.adapter.PostAdapterProfilePage
import com.albertkingdom.loginsignuptest.adapter.PostsAdapter
import com.albertkingdom.loginsignuptest.util.GridColumnLayout
import com.albertkingdom.loginsignuptest.viewModel.MyViewModel

class ProfileSubFragment1(private val email: String) : Fragment(R.layout.profile_sub_fragment_1) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapterProfilePage
    private val viewModel: MyViewModel by activityViewModels()
    val TAG = "ProfileSubFragment1"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        viewModel.getSingleUserPost(email)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val numberOfColumn = GridColumnLayout.calculateNoOfColumns(requireContext(), 100f)
        recyclerView = view.findViewById(R.id.recyclerview_profilePage_post_list)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        adapter = PostAdapterProfilePage()
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                println("click profile post $position")
            }

            override fun onItemClick(email: String) {

            }

            override fun onClickLike(position: Int) {

            }

        })
        recyclerView.adapter = adapter
        viewModel.singleUserPostList.observe(
            viewLifecycleOwner,
            { list ->
                adapter.submitList(list)
            }
        )
    }
}