package com.albertkingdom.loginsignuptest.util

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.albertkingdom.loginsignuptest.api.ImgurApi
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import android.annotation.SuppressLint
import android.content.ContentUris
import android.database.Cursor
import android.os.Build
import android.os.Environment
import android.webkit.MimeTypeMap
import java.io.*


class FileUtil {
    companion object {


        // 12-24 test ok
        fun fileFromContentUri(context: Context, contentUri: Uri): File {
            // Preparing Temp file name
            val fileExtension = getFileExtension(context, contentUri)
            val fileName = "temp_file" + if (fileExtension != null) ".$fileExtension" else ""

            // Creating Temp file
            val tempFile = File(context.cacheDir, fileName)
            tempFile.createNewFile()

            try {
                val oStream = FileOutputStream(tempFile)
                val inputStream = context.contentResolver.openInputStream(contentUri)

                inputStream?.let {
                    copy(inputStream, oStream)
                }

                oStream.flush()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return tempFile
        }
        private fun getFileExtension(context: Context, uri: Uri): String? {
            val fileType: String? = context.contentResolver.getType(uri)
            return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
        }

        @Throws(IOException::class)
        private fun copy(source: InputStream, target: OutputStream) {
            val buf = ByteArray(8192)
            var length: Int
            while (source.read(buf).also { length = it } > 0) {
                target.write(buf, 0, length)
            }
        }
    }

}