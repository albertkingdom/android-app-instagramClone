package com.albertkingdom.loginsignuptest.model

data class ImgurResponseResult (
    val data: ImgurData,
    val status: Int
)

data class ImgurData (
    val link: String
)