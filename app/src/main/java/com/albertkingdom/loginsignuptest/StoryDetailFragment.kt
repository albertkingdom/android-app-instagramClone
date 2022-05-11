package com.albertkingdom.loginsignuptest

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.albertkingdom.loginsignuptest.model.Post
import com.albertkingdom.loginsignuptest.viewModel.MyViewModel
import com.albertkingdom.loginsignuptest.viewModel.StoryDetailViewModel
import com.facebook.drawee.view.SimpleDraweeView
import kotlinx.coroutines.*
import java.lang.Exception


private const val DEBUG_TAG = "StoryDetailFragment"
class StoryDetailFragment : DialogFragment() {
    private val viewModel: MyViewModel by activityViewModels()
    private val storyDetailViewModel: StoryDetailViewModel by viewModels()
    private lateinit var mDetector: GestureDetectorCompat
    lateinit var imageView: SimpleDraweeView
    lateinit var closeButton: ImageButton
    lateinit var progressBar: ProgressBar
    var currentImageIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
        Log.d(DEBUG_TAG, "onCreate")

        var customGestureListener = MyGestureListener()
        customGestureListener.onSwipeCallback = object : SwipeCallback {
            override fun onLeftSwipe() {
                Log.d(DEBUG_TAG, "onLeftSwipe")
                storyDetailViewModel.changeImageTimer.cancel()

                cardFlip()

                storyDetailViewModel.setTimer()
            }

            override fun onRightSwipe() {
                Log.d(DEBUG_TAG, "onRightSwipe")

            }

            override fun onDownSwipe() {
                Log.d(DEBUG_TAG, "onDownSwipe")
                dismiss()
            }

        }
        mDetector = GestureDetectorCompat(context, customGestureListener)



    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        // config animation for enter and exit
        dialog.getWindow()?.getAttributes()?.windowAnimations = R.style.DialogAnimation

        return dialog
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.postList.observe(viewLifecycleOwner) { list ->
            storyDetailViewModel.setupPostList(list)
            storyDetailViewModel.setTimer()
        }
        storyDetailViewModel.setCurrentImageIndex(currentImageIndex)
        return inflater.inflate(R.layout.fragment_story_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        closeButton = view.findViewById(R.id.btn_close)
        imageView = view.findViewById(R.id.story_detail_image)
        progressBar = view.findViewById(R.id.progressBar)


        closeButton.setOnClickListener {
            dismiss()
        }

        // For detect touch event
        view.setOnTouchListener { _, motionEvent ->
            mDetector.onTouchEvent(motionEvent)
            true
        }

        storyDetailViewModel.currentImageUrl.observe(viewLifecycleOwner) { url ->
            Log.d(DEBUG_TAG, "currentImageUrl= $url")
            imageView.setImageURI(Uri.parse(url.toString()), null)
            animateProgressBar()

        }

        storyDetailViewModel.flipCount.observe(viewLifecycleOwner) { count ->

            println("flip count $count")
            if (count > -1) {
                cardFlip()
            }
        }

        storyDetailViewModel.endOfPostList.observe(viewLifecycleOwner) { boolean ->
            if (boolean) {
                dismiss()
            }
        }
    }

    fun cardFlip(){
        println("flip")

        val rotationY1 = PropertyValuesHolder.ofFloat(View.ROTATION_Y, 0f, 90f)
        val alphaChange1 = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f)
        val rotationY2 = PropertyValuesHolder.ofFloat(View.ROTATION_Y, 270f, 360f)
        val alphaChange2 = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)

        // val animatorFirst = ObjectAnimator.ofFloat(imageView, View.ROTATION_Y, 0f, 90f)
        // animate 2 properties
        val animatorFirst = ObjectAnimator.ofPropertyValuesHolder(imageView, rotationY1, alphaChange1)
        // val animatorSecond = ObjectAnimator.ofFloat(imageView, View.ROTATION_Y, 270f, 360f)
        val animatorSecond = ObjectAnimator.ofPropertyValuesHolder(imageView, rotationY2, alphaChange2)


        animatorFirst.duration = 1000
        animatorSecond.duration = 1000
        animatorFirst.interpolator = AccelerateDecelerateInterpolator()
        animatorSecond.interpolator = AccelerateDecelerateInterpolator()

        animatorFirst.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                //rotateButton.isEnabled = false
            }
            override fun onAnimationEnd(animation: Animator?) {
                //rotateButton.isEnabled = true

                storyDetailViewModel.incrementCurrentImageIndex()

                Log.d(DEBUG_TAG, "onAnimationEnd current index = ${storyDetailViewModel.currentImageIndex.value}")

                animatorSecond.start()

            }
        })
        animatorSecond.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
               //animateProgressBar()

            }
        })
        animatorFirst.start()
    }
    fun animateProgressBar() {
        ObjectAnimator.ofInt(progressBar, "progress", 0, 100)
            .setDuration(5000)
            .start();
    }
    // communicate with custom MyGestureListener
    interface SwipeCallback {
        fun onLeftSwipe()
        fun onRightSwipe()
        fun onDownSwipe()
    }


    override fun onDestroy() {
        super.onDestroy()

        storyDetailViewModel.cancelTimer()
    }


    private class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
        val SWIPE_MIN_DISTANCE = 120
        val SWIPE_THRESHOLD_VELOCITY = 200

        lateinit var onSwipeCallback: SwipeCallback

        override fun onDown(event: MotionEvent): Boolean {
            //Log.d(DEBUG_TAG, "onDown: $event")
            return true
        }

        override fun onFling(
            event1: MotionEvent,
            event2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            Log.d(DEBUG_TAG, "onFling: $event1 $event2")
            try {
                // right to left swipe
                if (event1.x - event2.x > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
                ) {
                    onSwipeCallback.onLeftSwipe()
                }
                // left to right swipe
                else if (event2.x - event1.x > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
                ) {
                    onSwipeCallback.onRightSwipe()
                } else if (event2.y - event1.y > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY
                ) {
                    onSwipeCallback.onDownSwipe()
                }
            }catch (e:Exception)
            {
                Log.e(DEBUG_TAG, e.toString())
            }
            return true
        }

        public enum class Direction {
            UP, DOWN, LEFT, RIGHT
        }



    }
}