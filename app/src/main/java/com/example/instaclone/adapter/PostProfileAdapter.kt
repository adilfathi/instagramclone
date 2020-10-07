package com.example.instagramclone.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instaclone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.instagramclone.CommentActivity
import com.example.instagramclone.model.Post
import com.example.instagramclone.model.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostProfileAdapter(private val mContext : Context, private val mPost : List<Post>):
    RecyclerView.Adapter<PostProfileViewHolder>() {

    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostProfileViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.list_post_p, parent, false)
        return PostProfileViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: PostProfileViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val myPost = mPost[position]
        Picasso.get().load(myPost.getPostImage()).into(holder.postImage)

        if (myPost.getDescription().equals("")){
            holder.capt.visibility = View.GONE
        }else{
            holder.capt.visibility = View.GONE
            holder.capt.text = myPost.getDescription()
        }

        publisherInfo(holder.profileImage, holder.username, holder.unamePost, myPost.getPublisher())

        likePost(myPost.getPostId(), holder.likeBtn)

        totalLikes(holder.likes, myPost.getPostId())

        holder.likeBtn.setOnClickListener {
            if (holder.likeBtn.tag == "Like"){

                FirebaseDatabase.getInstance().reference
                    .child("Likes").child(myPost.getPostId()).child(firebaseUser!!.uid)
                    .setValue(true)
            } else {

                FirebaseDatabase.getInstance().reference
                    .child("Likes").child(myPost.getPostId()).child(firebaseUser!!.uid)
                    .removeValue()

//                val intent = Intent(mContext, MainActivity::class.java)
//                mContext.startActivity(intent)
            }
        }

        holder.commentBtn.setOnClickListener {
            val intentComment = Intent(mContext, CommentActivity::class.java)
            intentComment.putExtra("postId", myPost.getPostId())
            intentComment.putExtra("publisherId", myPost.getPublisher())
            mContext.startActivity(intentComment)
        }

        getTotalComment(holder.comments, myPost.getPostId())
    }

    private fun getTotalComment(comments: TextView, postId: String) {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments").child(postId)

        commentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    comments.text = snapshot.childrenCount.toString() + " comments"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun totalLikes(likes: TextView, postId: String) {
        val likesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postId)

        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                likes.text = snapshot.childrenCount.toString() + " Likes"
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun likePost(postId: String, likeBtn: ImageView) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val likesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postId)

        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(firebaseUser!!.uid).exists()){
                    likeBtn.setImageResource(R.drawable.heart_clicked)
                    likeBtn.tag = "Liked"
                } else {
                    likeBtn.setImageResource(R.drawable.heart_not_clicked)
                    likeBtn.tag = "Like"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun publisherInfo(profileImage: CircleImageView, userName: TextView, publisher: TextView, publisherId: String) {

        val usersRef = FirebaseDatabase.getInstance().reference
            .child("Users").child(publisherId)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(
                        User::class.java)

                    Picasso.get().load(user?.getImage()).into(profileImage)
                    userName.text = user?.getUsername()
                    publisher.text = user?.getUsername()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}

class PostProfileViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    var profileImage : CircleImageView = itemView.findViewById(R.id.profile_image_post3)
    var postImage : ImageView = itemView.findViewById(R.id.image_post3)
    var likeBtn : ImageView = itemView.findViewById(R.id.like_btn3)
    var commentBtn : ImageView = itemView.findViewById(R.id.comment_btn3)
    var shareBtn : ImageView = itemView.findViewById(R.id.share_btn3)
    var saveBtn : ImageView = itemView.findViewById(R.id.save_btn3)
    var username : TextView = itemView.findViewById(R.id.username_post3)
    var unamePost : TextView = itemView.findViewById(R.id.post_username3)
    var likes : TextView = itemView.findViewById(R.id.post_likes3)
    var capt : TextView = itemView.findViewById(R.id.post_caption3)
    var comments : TextView = itemView.findViewById(R.id.post_comments3)

}
