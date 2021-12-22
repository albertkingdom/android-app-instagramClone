package com.albertkingdom.loginsignuptest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.albertkingdom.loginsignuptest.R
import com.albertkingdom.loginsignuptest.model.Comment

class CommentAdapter: ListAdapter<Comment, CommentAdapter.CommentViewHolder>(DiffCallback) {

    class CommentViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val userName = view.findViewById<TextView>(R.id.user_name)
        val commentContent = view.findViewById<TextView>(R.id.message)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentAdapter.CommentViewHolder {
       val view =LayoutInflater.from(parent.context).inflate(R.layout.item_message,parent,false )
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentAdapter.CommentViewHolder, position: Int) {
        val currentComment = getItem(position)

        holder.apply {
            userName.text = currentComment.userEmail
            commentContent.text = currentComment.commentContent
        }
    }


    companion object {

        val DiffCallback = object: DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem == newItem
            }

        }
    }
}