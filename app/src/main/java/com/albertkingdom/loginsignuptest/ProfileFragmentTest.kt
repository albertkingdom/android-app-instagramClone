package com.albertkingdom.loginsignuptest

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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

class ProfileFragmentTest: Fragment(R.layout.profile_fragment_test) {
    val TAG = "ProfileFragmentTest"
    private lateinit var userImage: SimpleDraweeView
    private lateinit var userImageDefault: ImageView
    private lateinit var btnFollow: Button
    private lateinit var btnSendMsg: Button
    private lateinit var btnEditProfile: Button
    private lateinit var textViewPostCount: TextView //文章數
    private lateinit var textViewFollowingCount: TextView //追蹤人數
    private lateinit var textViewFansCount: TextView // 粉絲人數

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var pageAdapter: VPAdapter
    private val viewModel: MyViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
        userImage = view.findViewById(R.id.user_image_new)
        userImageDefault = view.findViewById(R.id.user_image_new_default)
        tabLayout = view.findViewById(R.id.tab_layout)
        viewPager = view.findViewById(R.id.view_pager)
        btnEditProfile = view.findViewById(R.id.btn_edit_profile)
        textViewPostCount = view.findViewById(R.id.post_count)
        textViewFollowingCount = view.findViewById(R.id.following_count)
        textViewFansCount = view.findViewById(R.id.fans_count)

        setHasOptionsMenu(true)
        if(!viewModel.checkIsLogIn()) {
            findNavController().navigate(R.id.loginFragment)
            userImageDefault.visibility = View.VISIBLE
            userImage.visibility = View.GONE
        } else if (viewModel.checkIsLogIn() && viewModel.auth.currentUser?.photoUrl != null){
            userImage.visibility = View.VISIBLE
            userImageDefault.visibility = View.INVISIBLE
            userImage.setImageURI(Uri.parse(viewModel.auth.currentUser?.photoUrl.toString()), null)
            setupViewPager()
        } else {
            userImage.visibility = View.INVISIBLE
            userImageDefault.visibility = View.VISIBLE
            setupViewPager()
        }

        if (viewModel.checkIsLogIn()){
            viewModel.singleUserPostList.observe(viewLifecycleOwner, { list->

                textViewPostCount.text = String.format(resources.getString(R.string.post_count),
                    list.size)

            })
            viewModel.followingUserList.observe(viewLifecycleOwner, { list ->
                textViewFollowingCount.text = String.format(resources.getString(R.string.following_count),
                    list.size)
            })

            viewModel.fansCount.observe(viewLifecycleOwner, { value ->
                textViewFansCount.text = String.format(resources.getString(R.string.fans_count),
                    value)
            })
        }

        navigateToEditProfile()
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.profile_menu,menu)
        if (viewModel.checkIsLogIn()) {
            menu.findItem(R.id.btn_signIn).isVisible = false
        } else {
            menu.findItem(R.id.btn_signOut).isVisible = false
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.btn_signIn -> {
                Log.d(TAG,"sign in button click")
                findNavController().navigate(R.id.loginFragment)
                return true
            }
            R.id.btn_signOut -> {
                Log.d(TAG,"sign out button click")
                viewModel.signOut()
                Log.d(TAG, viewModel.checkIsLogIn().toString())
                viewModel.checkIsLogIn()
                if (!viewModel.checkIsLogIn()) {
                    //findNavController().popBackStack()
                    findNavController().popBackStack(R.id.postListFragment, false)
                   // (activity as MainActivity).bottomNavigation.selectedItemId = R.id.page_list
                }


                return true
            }
        }


        return super.onOptionsItemSelected(item)
    }
    fun setupViewPager() {
        // setup page adapter and tab layout

        pageAdapter = VPAdapter(this, viewModel.auth.currentUser?.email!!)
        viewPager.adapter = pageAdapter
        val title = listOf(R.drawable.grid_icon, R.drawable.assignment_ind_icon)
        TabLayoutMediator(tabLayout, viewPager){
                tab, position ->
            tab.icon = ContextCompat.getDrawable(requireContext(),title[position])
        }.attach()
    }
    fun navigateToEditProfile() {
        btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.updateProfileFragment)
        }

    }
}