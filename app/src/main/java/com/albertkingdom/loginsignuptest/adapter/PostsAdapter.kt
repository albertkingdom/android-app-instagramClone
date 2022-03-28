package com.albertkingdom.loginsignuptest.adapter

import android.graphics.drawable.AnimatedVectorDrawable
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
import com.albertkingdom.loginsignuptest.model.LikeBy
import com.albertkingdom.loginsignuptest.model.Post
import com.albertkingdom.loginsignuptest.util.DoubleClickListener
import com.facebook.drawee.view.SimpleDraweeView


class PostsAdapter: ListAdapter<Post,PostsAdapter.PostViewHolder>(DiffCallback) {
    private lateinit var onItemClickListener: OnItemClickListener
    private var loginUserEmail: String = ""
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
        val heart_animation = view.findViewById<ImageView>(R.id.heart_animation)
        val heart_animation_drawable = heart_animation.drawable
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
            if (currentPost.likeByUsers?.contains(LikeBy(userEmail = loginUserEmail)) == true) {
                likeButton.setImageResource(R.drawable.favorite_icon)
                holder.isLike = true
            }
            adapter.submitList(currentPost.commentList?.subList(0, 1))

            commentButton.setOnClickListener(View.OnClickListener {
                onItemClickListener.onItemClick(position)
            })
            // click on username to get email
            username.setOnClickListener {
                onItemClickListener.onItemClick(currentPost.userEmail!!)
            }
            likeButton.setOnClickListener{
                if (!holder.isLike){
                    likeButton.setImageResource(R.drawable.favorite_icon)
                    holder.isLike = true
                    onItemClickListener.onAddLike(position)
                } else {
                    likeButton.setImageResource(R.drawable.favorite_border_icon)
                    holder.isLike = false
                    onItemClickListener.onRemoveLike(position)
                }

            }
            // double click photo
            itemView.setOnClickListener(object: DoubleClickListener() {
                override fun onDoubleClick(v: View?) {
                    heart_animation.alpha = 0.7f
                    if (heart_animation_drawable is AnimatedVectorDrawable) {
                        heart_animation_drawable.start()
                    }
                    if (!holder.isLike){
                        likeButton.setImageResource(R.drawable.favorite_icon)
                        holder.isLike = true
                        onItemClickListener.onAddLike(position)
                    }
                }
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
    fun setCurrentLoginUserEmail(email: String) {
        this.loginUserEmail = email
    }
}

interface OnItemClickListener {
    fun onItemClick(position: Int)
    fun onItemClick(email: String)
    fun onAddLike(position: Int)
    fun onRemoveLike(position: Int)
}