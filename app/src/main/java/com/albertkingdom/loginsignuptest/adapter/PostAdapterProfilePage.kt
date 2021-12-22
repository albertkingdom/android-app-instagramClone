package com.albertkingdom.loginsignuptest.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.albertkingdom.loginsignuptest.R
import com.albertkingdom.loginsignuptest.model.Post
import com.facebook.drawee.view.SimpleDraweeView

class PostAdapterProfilePage: ListAdapter<Post, PostAdapterProfilePage.PostViewHolder>(DiffCallback) {
    private lateinit var onItemClickListener: OnItemClickListener

    class PostViewHolder(view: View): RecyclerView.ViewHolder(view){

        val postImage: SimpleDraweeView = view.findViewById<SimpleDraweeView>(R.id.post_image)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post_profile_page, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentPost = getItem(position)

        holder.apply {

            postImage.setImageURI(Uri.parse(currentPost?.imageLink.toString()), null)


            itemView.setOnClickListener(View.OnClickListener {
                onItemClickListener.onItemClick(position)
            })
        }
    }

    companion object {
        private val DiffCallback = object: DiffUtil.ItemCallback<Post>(){
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem == newItem
            }


        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}

