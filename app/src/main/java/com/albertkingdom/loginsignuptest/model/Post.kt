package com.albertkingdom.loginsignuptest.model

data class Post (
    val imageLink: String? = null,
    val postContent: String? = null,
    val userEmail: String? = null,
    val commentList: List<Comment>? = null,
    val timestamp: Any? = null
)

data class Comment(
    val userEmail: String? = null,
    val commentContent: String? = null
)