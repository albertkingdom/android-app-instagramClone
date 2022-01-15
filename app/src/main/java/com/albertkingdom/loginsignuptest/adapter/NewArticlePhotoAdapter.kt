package com.albertkingdom.loginsignuptest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.albertkingdom.loginsignuptest.R
import com.albertkingdom.loginsignuptest.model.Photo
import com.bumptech.glide.Glide


class NewArticlePhotoAdapter : ListAdapter<Photo, NewArticlePhotoAdapter.PhotoViewHolder>(DiffCallback){
    lateinit var onClickImageListener: ClickGallery
    class PhotoViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val photoImageView = view.findViewById<ImageView>(R.id.gallery_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo_new_article, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        //holder.photoImageView.setImageURI(getItem(position).uri)
        Glide.with(holder.photoImageView.context).load(getItem(position).uri).thumbnail(0.5f).into(holder.photoImageView)
        holder.itemView.setOnClickListener {
            onClickImageListener.onClickImage(position)
        }
    }
    companion object {
        private val DiffCallback = object: DiffUtil.ItemCallback<Photo>(){
            override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
                return oldItem == newItem
            }


        }
    }
}

interface ClickGallery {
    fun onClickImage(position: Int)
}