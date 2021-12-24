package com.albertkingdom.loginsignuptest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class BottomSheetFragment: BottomSheetDialogFragment() {
    private lateinit var cameraButton: MaterialButton
    private lateinit var galleryButton: MaterialButton
    lateinit var onItemClickListener: OnClickButton
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.bottom_sheet_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (dialog as? BottomSheetDialog)?.behavior?.apply {
            isFitToContents = false
//            peekHeight = 300
            isDraggable = false
            isHideable = true
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        cameraButton = view.findViewById(R.id.camera)
        galleryButton = view.findViewById(R.id.gallery)
        cameraButton.setOnClickListener {
            Log.d(TAG,"click camera text")
            onItemClickListener.onClickCamera()
            dismiss()
        }
        galleryButton.setOnClickListener {
            Log.d(TAG,"click gallery text")
            onItemClickListener.onClickGallery()
            dismiss()
        }
    }
    companion object {
        const val TAG = "ModalBottomSheet"
    }

    fun setOnClickButton(onClickListener: OnClickButton) {
        this.onItemClickListener = onClickListener
    }
}
interface OnClickButton {
    fun onClickCamera()
    fun onClickGallery()
}