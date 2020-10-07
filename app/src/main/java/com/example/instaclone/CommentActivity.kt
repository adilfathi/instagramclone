package com.example.instagramclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instaclone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.instagramclone.model.User
import com.example.instagramclone.adapter.CommentAdapter
import com.example.instagramclone.model.Comments
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comment.*

class CommentActivity : AppCompatActivity() {

    private var postId      = ""
    private var publisherId = ""
    private var firebaseUser    : FirebaseUser? = null
    private var commentAdapter  : CommentAdapter? = null
    private var commentListData : MutableList<Comments>? = null
    private var recyclerView    : RecyclerView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        val intent  = intent
        postId      = intent.getStringExtra("postId").toString()
        publisherId = intent.getStringExtra("publisherId").toString()

        firebaseUser = FirebaseAuth.getInstance().currentUser

        recyclerView                = findViewById(R.id.recycler_comment)
        val layoutManager           = LinearLayoutManager(this)
//        layoutManager.reverseLayout = true
        recyclerView?.layoutManager = layoutManager

        commentListData       = ArrayList()
        commentAdapter        =
            CommentAdapter(
                this,
                commentListData as ArrayList<Comments>
            )
        recyclerView?.adapter = commentAdapter

        userInfo()
        readComment()
        getPostImage()

        txt_post_comment.setOnClickListener {
            if (et_add_comment.text.toString() == ""){
                Toast.makeText(this, "must be filled", Toast.LENGTH_SHORT).show()
            }else{
                addComment()
            }
        }
    }

    private fun getPostImage() {
        val postRef = FirebaseDatabase.getInstance().reference
            .child("Posts").child(postId).child("postimage")

        postRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val image = snapshot.value.toString()

                    Picasso.get().load(image).into(image_post11)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun readComment() {
        val commentRef = FirebaseDatabase.getInstance().reference
            .child("Comments").child(postId)

        commentRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    commentListData!!.clear()

                    for (s in snapshot.children){
                        val comment = s.getValue(Comments::class.java)
                        commentListData!!.add(comment!!)
                    }
                    commentAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun addComment() {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments").child(postId)

        val commentsMap = HashMap<String, Any>()
        commentsMap["comment"]   = et_add_comment.text.toString()
        commentsMap["publisher"] = firebaseUser!!.uid

        commentsRef.push().setValue(commentsMap)
        et_add_comment.text.clear()
    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().reference
            .child("Users").child(firebaseUser!!.uid)

        usersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(
                        User::class.java)

                    Picasso.get().load(user!!.getImage()).into(profile_comment)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}