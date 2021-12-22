package com.albertkingdom.loginsignuptest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.albertkingdom.loginsignuptest.viewModel.MyViewModel


class HomeFragment: Fragment(R.layout.home_fragment) {
    //private lateinit var auth: FirebaseAuth
    private lateinit var btnSignout: Button
    private  val viewModel: MyViewModel by activityViewModels()
    val TAG = "HomeFragment"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       //auth = (activity as MainActivity).auth


        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnContinue = view.findViewById<Button>(R.id.btn_continue)
        val btnGoToProfile = view.findViewById<Button>(R.id.btn_go_to_profile)
        btnSignout = view.findViewById<Button>(R.id.btn_signout)
// Check if user is signed in (non-null) and update UI accordingly.
        //val currentUser = auth.currentUser
        Log.d(TAG, "onViewCreated")
        if(viewModel.checkIsLogIn()){
            println("already login")
            println("home , user ${viewModel.auth.currentUser}")
            btnSignout.visibility = View.VISIBLE
            btnContinue.visibility = View.GONE
        }
        btnContinue.setOnClickListener {
            println("click")
            findNavController().navigate(R.id.loginFragment)
        }
        btnSignout.setOnClickListener {
            viewModel.signOut()

            //btnSignout.visibility = View.INVISIBLE
            //btnContinue.visibility = View.VISIBLE
        }
        btnGoToProfile.setOnClickListener {
            navigateToProfile()
        }

    }

    private fun navigateToProfile(){
        findNavController().navigate(R.id.profileFragmentTest)
    }
}