package com.albertkingdom.loginsignuptest.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.albertkingdom.loginsignuptest.R
import com.albertkingdom.loginsignuptest.model.Post
import com.facebook.drawee.view.SimpleDraweeView


class PostsAdapter: ListAdapter<Post,PostsAdapter.PostViewHolder>(DiffCallback) {
    private lateinit var onItemClickListener: OnItemClickListener

    class PostViewHolder(view: View): RecyclerView.ViewHolder(view){
        var isLike:Boolean = false
        val username: TextView = view.findViewById<TextView>(R.id.user_name)
        val username_2 = view.findViewById<TextView>(R.id.user_name_2)
        val postImage: SimpleDraweeView = view.findViewById<SimpleDraweeView>(R.id.post_image)
        val userImage: SimpleDraweeView = view.findViewById<SimpleDraweeView>(R.id.user_image)
        val userImageDefault: ImageView = view.findViewById<ImageView>(R.id.user_image_default)
        val postContent: TextView = view.findViewById<TextView>(R.id.post_content)
        val commentList: RecyclerView = view.findViewById(R.id.recyclerview_commentList)
        val commentButton = view.findViewById<ImageButton>(R.id.btn_comment)
        val likeButton = view.findViewById<ImageButton>(R.id.btn_like)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentPost = getItem(position)

        holder.apply {
            username.text = currentPost.userEmail
            username_2.text = currentPost.userEmail
            postImage.setImageURI(Uri.parse(currentPost?.imageLink.toString()), null)
            postContent.text = currentPost.postContent
            val adapter = CommentAdapter()
            commentList.adapter = adapter
            adapter.submitList(currentPost.commentList)

            commentButton.setOnClickListener(View.OnClickListener {
                onItemClickListener.onItemClick(position)
            })
            // click on username to get email
            username.setOnClickListener {
                onItemClickListener.onItemClick(currentPost.userEmail!!)
            }
            likeButton.setOnClickListener{
                onItemClickListener.onClickLike(position)
                if (!holder.isLike){
                    likeButton.setImageResource(R.drawable.favorite_icon)
                    holder.isLike = true
                } else {
                    likeButton.setImageResource(R.drawable.favorite_border_icon)
                    holder.isLike = false
                }

            }

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

interface OnItemClickListener {
    fun onItemClick(position: Int)
    fun onItemClick(email: String)
    fun onClickLike(position: Int)
}