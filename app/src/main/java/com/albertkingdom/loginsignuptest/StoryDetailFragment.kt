package com.albertkingdom.loginsignuptest

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.albertkingdom.loginsignuptest.model.Post
import com.facebook.drawee.view.SimpleDraweeView
import kotlinx.coroutines.*
import java.lang.Exception


private const val DEBUG_TAG = "StoryDetailFragment"
class StoryDetailFragment : DialogFragment() {
    private lateinit var mDetector: GestureDetectorCompat
    lateinit var imageView: SimpleDraweeView
    lateinit var closeButton: ImageButton
    lateinit var progressBar: ProgressBar
    lateinit var changeImageTimer: Job
    var postList = listOf<Post>()
    var currentImageIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        val dialog = super.onCreateDialog(savedInstanceState)
        val window = dialog.window
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        super.onCreate(savedInstanceState)
        Log.d(DEBUG_TAG, "onCreate")

        var customGestureListener = MyGestureListener()
        customGestureListener.onSwipeCallback = object : SwipeCallback {
            override fun onLeftSwipe() {
                Log.d(DEBUG_TAG, "onLeftSwipe")
                changeImageTimer.cancel()
                //currentImageIndex += 1
                cardFlip()
                autoChangeImage()

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_story_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        closeButton = view.findViewById(R.id.btn_close)
        imageView = view.findViewById(R.id.story_detail_image)
        progressBar = view.findViewById(R.id.progressBar)
        //imageView.setImageURI(Uri.parse(postList[currentImageIndex].imageLink.toString()), null)

        closeButton.setOnClickListener {
            dismiss()
        }

        // For detect touch event
        view.setOnTouchListener { _, motionEvent ->
            mDetector.onTouchEvent(motionEvent)
            true
        }
       autoChangeImage()
    }
    fun autoChangeImage() {
        Log.d(DEBUG_TAG, "autochangeimage, current index = $currentImageIndex")
        imageView.setImageURI(Uri.parse(postList[currentImageIndex].imageLink.toString()), null)
        animateProgressBar()
        changeImageTimer = viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                while (currentImageIndex < postList.lastIndex) {
                    //animateProgressBar()
                    delay(5000 + 2000)
                    cardFlip()

                }
                if (currentImageIndex == postList.lastIndex) {
                    delay(5000)
                    dismiss()
                }

            }
        }

        changeImageTimer.start()

    }
    fun cardFlip(){
        if (currentImageIndex>=postList.lastIndex){
            return
        }
        // first quarter turn
        val animatorFirst = ObjectAnimator.ofFloat(imageView, View.ROTATION_Y, 0f, 90f)
        val animatorSecond = ObjectAnimator.ofFloat(imageView, View.ROTATION_Y, -90f, 0f)

        animatorFirst.duration = 1000
        animatorSecond.duration = 1000
        animatorFirst.interpolator = LinearInterpolator()
        animatorSecond.interpolator = LinearInterpolator()

        animatorFirst.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                //rotateButton.isEnabled = false
            }
            override fun onAnimationEnd(animation: Animator?) {
                //rotateButton.isEnabled = true
                currentImageIndex += 1
                Log.d(DEBUG_TAG, "onAnimationEnd current index = $currentImageIndex")
                imageView.setImageURI(Uri.parse(postList[currentImageIndex].imageLink.toString()), null)
                animatorSecond.start()

            }
        })
        animatorSecond.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
               animateProgressBar()

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
        changeImageTimer.cancel()
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