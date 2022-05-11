package com.albertkingdom.loginsignuptest.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import com.albertkingdom.loginsignuptest.R
import com.albertkingdom.loginsignuptest.adapter.CommentAdapter.Companion.DiffCallback
import com.albertkingdom.loginsignuptest.model.Post
import com.facebook.drawee.view.SimpleDraweeView

class StoryAdapter: ListAdapter<Post, StoryAdapter.StoryViewHolder>(DiffCallback) {
    lateinit var onClickListener: onStoryClickListener
    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: SimpleDraweeView = itemView.findViewById(R.id.story_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val currentPost = getItem(position)

        holder.imageView.setImageURI(Uri.parse(currentPost?.imageLink.toString()), null)
        holder.itemView.setOnClickListener {
            onClickListener.clickImage(position)
        }
    }
    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem.timestamp == newItem.timestamp
            }

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem == newItem
            }

        }
    }

}
interface onStoryClickListener {
    fun clickImage(position: Int)
}