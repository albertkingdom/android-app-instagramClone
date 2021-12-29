package com.albertkingdom.loginsignuptest.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.albertkingdom.loginsignuptest.MainActivity
import com.albertkingdom.loginsignuptest.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random


class FirebaseService: FirebaseMessagingService() {
    val TAG = "FirebaseService"

    companion object {
        var sharedPref: SharedPreferences? = null

        var token: String?
            get() {
                return sharedPref?.getString("token", "")
            }
            set(value) {
                sharedPref?.edit()?.putString("token", value)?.apply()
            }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        Log.d(TAG, "onNewToken...$newToken")

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "onMessageReceived...${message.from} ")
        Log.d(TAG, "onMessageReceived.data..${message.data} ")

        // send local notification if source is not the current device login user
        val sharedPref = getSharedPreferences("user", MODE_PRIVATE)
        if (message.data["loginUserEmail"] != sharedPref.getString("loginUserEmail","")) {
            Log.d(TAG," sender token...${message.data["senderToken"]}")
            Log.d(TAG," local token...$token")
            sendLocalNotification(messageBody = message)
        }

    }

    private fun sendLocalNotification(messageBody: RemoteMessage) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val notificationID = Random.nextInt()
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT)

        val channelId = "my_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_android_black_24dp)
            .setContentTitle(messageBody.data["title"])
            .setContentText(messageBody.data["message"])
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "New Post",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationID /* ID of notification */, notificationBuilder.build())
    }
}