package com.example.instagramclone


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.example.instaclone.R

class PostProfileActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_profile)

        val intent = intent
        val caption = intent.getStringExtra("caption")
        val userId = intent.getStringExtra("username")
        val postImage = intent.getStringExtra("postimage")
        val postId = intent.getStringExtra("postId")
//
//        val view = layoutInflater.inflate(R.layout.list_post_p, contentView, false)
//
//        Picasso.get()
//            .load(postImage)
//            .into(view.image_post3)
//
//        view
    }

}