package com.albertkingdom.loginsignuptest.util

interface OnPostItemClickListener {
    fun onClickCommentButton(position: Int)
    fun onClickEmail(email: String)
    fun onAddLike(position: Int)
    fun onRemoveLike(position: Int)
}