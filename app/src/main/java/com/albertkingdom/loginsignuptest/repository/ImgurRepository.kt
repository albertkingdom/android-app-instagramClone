package com.albertkingdom.loginsignuptest.repository

import com.albertkingdom.loginsignuptest.api.ImgurApi
import com.albertkingdom.loginsignuptest.model.ImgurResponseResult
import okhttp3.MultipartBody

class ImgurRepository {
    suspend fun uploadToImgur(body: MultipartBody.Part): ImgurResponseResult {
        return ImgurApi.retrofitService.getResult(body)
    }
}