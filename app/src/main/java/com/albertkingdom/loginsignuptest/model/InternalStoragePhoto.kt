package com.albertkingdom.loginsignuptest.model

import android.graphics.Bitmap
import android.net.Uri

data class InternalStoragePhoto(
    val name: String,
    val bmp: Bitmap,
    val url: Uri
)
