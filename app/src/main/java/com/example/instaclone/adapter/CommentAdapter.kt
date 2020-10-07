package com.example.instagramclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instaclone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.instagramclone.model.Comments
import com.example.instagramclone.model.User
import com.squareup.picasso.Picasso

import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter(private val mContext : Context, private val mComments: MutableList<Comments>):
    RecyclerView.Adapter<CommentViewHolder>(){

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.list_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val comment = mComments[position]
        holder.commentCom.text = comment.getComment()

        getUserInfo(holder.imgProfileComment, holder.unameComment, comment.getPublisher())
    }

    private fun getUserInfo(imgProfileComment: CircleImageView, unameComment: TextView, publisher: String) {
        val userRef = FirebaseDatabase.getInstance().reference
            .child("Users").child(publisher)

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(User::class.java)
//                    Picasso.get().load(user!!.getImage()).into(imgProfileComment)
                    unameComment.text = user!!.getUsername()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    override fun getItemCount(): Int {
        return mComments.size
    }
}

class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var imgProfileComment : CircleImageView = itemView.findViewById(R.id.profile_in_comment)
    var unameComment : TextView = itemView.findViewById(R.id.uname_in_comment)
    var commentCom : TextView = itemView.findViewById(R.id.comment_in_comment)
}
