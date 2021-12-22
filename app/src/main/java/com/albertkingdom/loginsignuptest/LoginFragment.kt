package com.albertkingdom.loginsignuptest

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.albertkingdom.loginsignuptest.viewModel.MyViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment(R.layout.login_fragment) {
    //private lateinit var auth: FirebaseAuth
    private val viewModel: MyViewModel by activityViewModels()
    private val TAG = "loginfragment"

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val signupButton = view.findViewById<Button>(R.id.sign_up)
        val signinButton = view.findViewById<Button>(R.id.sign_in)
        val emailView = view.findViewById(R.id.email_input) as TextInputLayout
        val passwordView = view.findViewById(R.id.password_input) as TextInputLayout

        signupButton.setOnClickListener {
            val email = emailView.editText?.text.toString()
            val password = passwordView.editText?.text.toString()
            println("email ${email}, password ${password}")

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseSignup(email, password)
            }
        }

        signinButton.setOnClickListener {
            val email = emailView.editText?.text.toString()
            val password = passwordView.editText?.text.toString()

            hideKeyboard(view)
            emailView.clearFocus()
            passwordView.clearFocus()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseSignIn(email, password)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

    }

    fun hideKeyboard(view: View) {
        val inputMethodManager =
            context?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun validateEmail() {

    }

    private fun firebaseSignup(email: String, password: String) {


        viewModel.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity as MainActivity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")

//                    updateUI(user)
                    navigate()
                    if (viewModel.auth.currentUser != null) {
                        println("user, ${viewModel.auth.currentUser?.email}")
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)

                    Snackbar.make(requireView(), "Sign Up Failure", Snackbar.LENGTH_LONG).show()

                }
            }

    }

    private fun firebaseSignIn(email: String, password: String) {
        viewModel.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity as MainActivity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    viewModel.getFollowingUser(email)
                    viewModel.getFans()
                    navigate()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)

                    Snackbar.make(requireView(), "Sign In Failure", Snackbar.LENGTH_LONG).show()
                }
            }

    }

    private fun navigate() {
//        findNavController().popBackStack(R.id.profileFragmentTest, false)
        findNavController().popBackStack(R.id.postListFragment, false)
        viewModel.checkIsLogIn()
    }
}