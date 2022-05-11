package com.albertkingdom.loginsignuptest.adapter

import android.graphics.drawable.AnimatedVectorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.albertkingdom.loginsignuptest.R
import com.albertkingdom.loginsignuptest.model.LikeBy
import com.albertkingdom.loginsignuptest.model.Post
import com.albertkingdom.loginsignuptest.util.DoubleClickListener
import com.albertkingdom.loginsignuptest.util.OnPostItemClickListener
import com.bumptech.glide.Glide
import com.facebook.drawee.view.SimpleDraweeView

/*
this adapter has 2 types of view holder.
- PostViewHolder [item_post.xml] has nested recyclerview for comment list (CommentAdapter)
- HeaderViewHolder [item_post_header.xml] is placed at the top for story. And this view holder has nested recyclerview for stories. (StoryAdapter)
 */
const val HEADER_HOLDER = 1
const val CONTENT_HOLDER = 2

class ParentPostAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data = listOf<Post>()
    private lateinit var postItemClickListener: OnPostItemClickListener
    private lateinit var clickStoryItemListener: onStoryClickListener
    private var loginUserEmail: String = ""

    inner class PostViewHolder(view: View): RecyclerView.ViewHolder(view){
        var isLike:Boolean = false
        val username = view.findViewById<TextView>(R.id.user_name)
        val username_2 = view.findViewById<TextView>(R.id.user_name_2)
        val postImage: ImageView = view.findViewById(R.id.post_image)
        val userImage: SimpleDraweeView = view.findViewById<SimpleDraweeView>(R.id.user_image)
        val userImageDefault: ImageView = view.findViewById<ImageView>(R.id.user_image_default)
        val postContent: TextView = view.findViewById<TextView>(R.id.post_content)
        val commentListRecyclerView: RecyclerView = view.findViewById(R.id.recyclerview_commentList)
        val commentButton = view.findViewById<ImageButton>(R.id.btn_comment)
        val likeButton = view.findViewById<ImageButton>(R.id.btn_like)
        val heart_animation = view.findViewById<ImageView>(R.id.heart_animation)
        val heart_animation_drawable = heart_animation.drawable

        fun bind(currentPost: Post, position: Int) {
            itemView.apply {
                username.text = currentPost.userEmail
                username_2.text = currentPost.userEmail

                Glide.with(itemView)
                    .load(Uri.parse(currentPost.imageLink.toString()))
                    .centerCrop()
                    .into(postImage)

                postContent.text = currentPost.postContent
                val adapter = CommentAdapter()
                commentListRecyclerView.adapter = adapter
                if (currentPost.likeByUsers?.contains(LikeBy(userEmail = loginUserEmail)) == true) {
                    likeButton.setImageResource(R.drawable.favorite_icon)
                    isLike = true
                }
                adapter.submitList(currentPost.commentList?.subList(0, 1))
                commentButton.setOnClickListener(View.OnClickListener {
                    postItemClickListener.onClickCommentButton(position)
                })
                // click on username to get email
                username.setOnClickListener {
                    postItemClickListener.onClickEmail(currentPost.userEmail!!)
                }
                likeButton.setOnClickListener {
                    if (!isLike) {
                        likeButton.setImageResource(R.drawable.favorite_icon)
                        isLike = true
                        postItemClickListener.onAddLike(position)
                    } else {
                        likeButton.setImageResource(R.drawable.favorite_border_icon)
                        isLike = false
                        postItemClickListener.onRemoveLike(position)
                    }

                }
                // double click photo
                itemView.setOnClickListener(object : DoubleClickListener() {
                    override fun onDoubleClick(v: View?) {
                        heart_animation.alpha = 0.7f
                        if (heart_animation_drawable is AnimatedVectorDrawable) {
                            heart_animation_drawable.start()
                        }
                        if (!isLike) {
                            likeButton.setImageResource(R.drawable.favorite_icon)
                            isLike = true
                            postItemClickListener.onAddLike(position)
                        }
                    }
                })
            }
        }
    }
    inner class HeaderViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val headerRecyclerView: RecyclerView = view.findViewById(R.id.post_list_header_recycler_view)
    }


    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return HEADER_HOLDER
        }
        return CONTENT_HOLDER
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == HEADER_HOLDER) {

            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post_header,parent,false)
            return HeaderViewHolder(view)
        }

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        if (holder is PostViewHolder) {
            val currentPost = data[holder.adapterPosition-1]
            holder.bind(currentPost, holder.adapterPosition - 1)
        }

        if (holder is HeaderViewHolder) {
            val storyAdapter = StoryAdapter()

            holder.headerRecyclerView.adapter = storyAdapter
            storyAdapter.submitList(data)
            storyAdapter.onClickListener = clickStoryItemListener
        }
    }


    override fun getItemCount(): Int {
        return data.size
    }

    fun setOnItemClickListener(onItemClickListener: OnPostItemClickListener) {
        this.postItemClickListener = onItemClickListener
    }
    fun setClickStoryItemListener(listener: onStoryClickListener) {
        this.clickStoryItemListener = listener
    }
    fun setCurrentLoginUserEmail(email: String) {
        this.loginUserEmail = email
    }
}