package com.albertkingdom.loginsignuptest.util

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext

class AlertDialogUtil {
 companion object {
     fun showAlertDialog(reason: AlertReason, context: Context) {
         val builder = AlertDialog.Builder(context)
         when (reason) {
             AlertReason.EMPTY_COMMENT ->  builder.setMessage("留言不可為空白")
             AlertReason.NOT_LOGIN -> builder.setMessage("請先登入才能發佈留言")
             AlertReason.EMPTY_IMAGE -> builder.setMessage("請選擇一張照片")
         }

         builder.setPositiveButton("Ok！",null)
         builder.show()
     }

 }
}
enum class AlertReason {
    NOT_LOGIN, EMPTY_COMMENT, EMPTY_IMAGE

}