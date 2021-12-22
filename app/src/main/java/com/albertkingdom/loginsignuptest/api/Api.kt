package com.albertkingdom.loginsignuptest.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

class Api {
}

data class SignUpResponse(
    val profile: Profile
)

data class Profile(
    val firstName: String,
    val lastName: String,
    val email: String,
    val login: String

)

data class ImgurResponseResult (
    val data: ImgurData,
    val status: Int
)

data class ImgurData (
    val link: String
)

const val BASE_URL = "https://api.imgur.com/3/"

// create moshi object
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// create retrofit instance
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()


interface ImgurApiService {
    @Headers("Authorization: Client-ID 823063d2a8e21a7")
    @Multipart
    @POST("image")
    suspend fun getResult(@Part image: MultipartBody.Part,
//                          @Part("name") imageName:RequestBody
    ): ImgurResponseResult
}

object ImgurApi {
    val retrofitService: ImgurApiService by lazy {
        retrofit.create(ImgurApiService::class.java)
    }
}

// Pass it like this
//val file = File(RealPathUtils.getRealPathFromURI_API19(context, uri))
//val file = File("123")
//val requestFile: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)

// MultipartBody.Part is used to send also the actual file name
//val body: MultipartBody.Part = MultipartBody.Part.createFormData("image", file.name, requestFile)

