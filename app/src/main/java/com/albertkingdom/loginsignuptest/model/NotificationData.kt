package com.albertkingdom.loginsignuptest.model

data class NotificationData(
    val title: String,
    val message: String,
    val senderToken: String? = null,
    val loginUserEmail: String? = null
)