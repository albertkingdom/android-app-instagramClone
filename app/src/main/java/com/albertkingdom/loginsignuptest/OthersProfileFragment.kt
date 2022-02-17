package com.albertkingdom.loginsignuptest

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.albertkingdom.loginsignuptest.profile.VPAdapter
import com.albertkingdom.loginsignuptest.viewModel.MyViewModel
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OthersProfileFragment: Fragment(R.layout.profile_fragment_test) {
    val TAG = "OthersProfileFragment"
    private lateinit var userImage: SimpleDraweeView
    private lateinit var userImageDefault: ImageView
    private lateinit var btnFollow: Button
    private lateinit var btnSendMsg: Button
    private lateinit var btnEditProfile: Button

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var pageAdapter: VPAdapter
    private lateinit var textViewPostCount: TextView //文章數
    private lateinit var textViewFollowingCount: TextView //追蹤人數
    private lateinit var textViewFansCount: TextView // 粉絲人數

    var isFollowing: Boolean = false
    private val viewModel: MyViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
        userImage = view.findViewById(R.id.user_image_new)
        userImageDefault = view.findViewById(R.id.user_image_new_default)
        tabLayout = view.findViewById(R.id.tab_layout)
        viewPager = view.findViewById(R.id.view_pager)
        btnEditProfile = view.findViewById(R.id.btn_edit_profile)
        btnFollow = view.findViewById(R.id.btn_follow)
        btnSendMsg = view.findViewById(R.id.btn_send_msg)
        setHasOptionsMenu(true)

        btnEditProfile.visibility = View.GONE
        btnFollow.visibility = View.VISIBLE
        btnSendMsg.visibility = View.VISIBLE
        textViewPostCount = view.findViewById(R.id.post_count)
        textViewFollowingCount = view.findViewById(R.id.following_count)
        textViewFansCount = view.findViewById(R.id.fans_count)


        setupViewPager()
        setupBtnFollow()
        queryProfilePageInfo()

        // change title
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.user_name_and_post, viewModel.userEmailToBeShowInProfile)
    }
    private fun setupBtnFollow() {

        viewModel.followingUserList.observe(viewLifecycleOwner, { list ->
           if(list.contains(viewModel.userEmailToBeShowInProfile)){
               isFollowing = true
               btnFollow.text = "追蹤中"
               btnFollow.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check_icon, 0)
           }
        })
        btnFollow.setOnClickListener {
            if (isFollowing) {
                cancelFollow()
            } else {
                viewModel.followUser()
            }
        }
    }





    private fun setupViewPager() {
        // setup page adapter and tab layout
        pageAdapter = VPAdapter(this, viewModel.userEmailToBeShowInProfile!!)
        viewPager.adapter = pageAdapter
        val title = listOf(R.drawable.grid_icon,R.drawable.assignment_ind_icon)
        TabLayoutMediator(tabLayout, viewPager){
                tab, position ->
            tab.icon = ContextCompat.getDrawable(requireContext(),title[position])
        }.attach()
    }

    private fun queryProfilePageInfo() {
        val email = viewModel.userEmailToBeShowInProfile
        viewModel.getFans(email)
        viewModel.getFollowingUser(email!!)
        viewModel.fansCount.observe(viewLifecycleOwner, { value ->
            textViewFansCount.text = String.format(resources.getString(R.string.fans_count),
                value)
        })
        viewModel.singleUserPostList.observe(viewLifecycleOwner, { list->

            textViewPostCount.text = String.format(resources.getString(R.string.post_count),
                list.size)
        })
        viewModel.othersfollowingUserList.observe(viewLifecycleOwner, { list ->
           textViewFollowingCount.text = String.format(resources.getString(R.string.following_count),
               list.size)
        })
    }

    private fun cancelFollow(){

            viewModel.cancelFollowUser()
            btnFollow.text = "追蹤"
            btnFollow.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

    }
}