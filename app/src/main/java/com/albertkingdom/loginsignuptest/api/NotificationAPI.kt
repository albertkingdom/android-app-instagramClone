package com.albertkingdom.loginsignuptest.api


import com.albertkingdom.loginsignuptest.api.Constants.Companion.CONTENT_TYPE
import com.albertkingdom.loginsignuptest.api.Constants.Companion.FIREBASE_NOTIFICATION_BASE_URL
import com.albertkingdom.loginsignuptest.api.Constants.Companion.SERVER_KEY
import com.albertkingdom.loginsignuptest.model.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST



interface NotificationAPI {
    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}



object PushNotificationApi {
    // create retrofit instance
    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(FIREBASE_NOTIFICATION_BASE_URL)
        .build()

    val retrofitService: NotificationAPI by lazy{
        retrofit.create(NotificationAPI::class.java)

    }
}