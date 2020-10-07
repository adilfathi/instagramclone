package com.example.instagramclone.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instaclone.R
import com.example.instagramclone.PostProfileActivity
import com.example.instagramclone.model.Post

class ImagesAdapter(private val context: Context, private val mPost: List<Post>)
    :RecyclerView.Adapter<ImagesViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_images, parent, false)
        return ImagesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        val mPost = mPost[position]

        Glide.with(context).load(mPost.getPostImage()).into(holder.postImageGrid)

        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, PostProfileActivity::class.java)
                .putExtra("username", mPost.getPublisher())
                .putExtra("postId", mPost.getPostId())
                .putExtra("postimage", mPost.getPostImage())
                .putExtra("caption", mPost.getDescription())
            )
        }
    }

    override fun getItemCount(): Int {
        return mPost.size
    }
}

class ImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val postImageGrid : ImageView = itemView.findViewById(R.id.image_post_grid)
}
