package com.example.instagramclone.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instaclone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.instagramclone.EditProfileActivity
import com.example.instagramclone.adapter.ImagesAdapter
import com.example.instagramclone.model.Post
import com.example.instagramclone.model.User
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment() {
    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser

    private var postListGrid: MutableList<Post>? = null
    private var imagesAdapter: ImagesAdapter? = null
    private var myRecyclerImages: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewProfile = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        myRecyclerImages = viewProfile.findViewById(R.id.rv_profile)
        myRecyclerImages!!.setHasFixedSize(true)
        val linearLayoutManager = GridLayoutManager (context, 3)
        myRecyclerImages!!.layoutManager = linearLayoutManager

        postListGrid = ArrayList()
        imagesAdapter = context?.let {
            ImagesAdapter(
                it,
                postListGrid as ArrayList<Post>
            )
        }
        myRecyclerImages!!.adapter = imagesAdapter

        val pref = context?.getSharedPreferences("PREF", Context.MODE_PRIVATE)

        if (pref != null) {

            this.profileId = pref.getString("profileId", "none")!!

        }
        if (profileId == firebaseUser.uid) {

            viewProfile?.btn_editProfile?.text = "Edit Profile"

        } else if (profileId == firebaseUser.uid) {

            cekFllwFllwingStat()

        }

        viewProfile.btn_editProfile.setOnClickListener {
            val getButtonText = view?.btn_editProfile?.toString()

            when{
                getButtonText == "Edit Profile" -> startActivity(Intent(context, EditProfileActivity::class.java))

                getButtonText == "Follow" -> {
                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(it1.toString()).setValue(true)
                    }

                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString()).setValue(true)
                    }
                }

                getButtonText == "Following" -> {
                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId).removeValue()
                    }

                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString()).removeValue()
                    }
                }
            }
            startActivity(Intent(context, EditProfileActivity::class.java))
        }

        getFollowers()
        getFollowing()
        userInfo()
        myPost()
        return viewProfile
    }

    private fun myPost() {
        var postRef = FirebaseDatabase.getInstance().reference
            .child("Posts")

        postRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    (postListGrid as ArrayList<Post>).clear()

                    for (snapshot in p0.children){
                        val post = snapshot.getValue(Post::class.java)
                        if (post!!.getPublisher() == profileId)
                            (postListGrid as ArrayList<Post>).add(post)
                    }
                    postListGrid!!.reverse()
                    imagesAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun userInfo() {
        val userRef = FirebaseDatabase.getInstance().reference
            .child("Users").child(profileId)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue<User>(
                        User::class.java)

//                    Picasso.get().load(user?.getImage()).into(view?.profile_pic)
                    view?.profile_username?.text = user?.getUsername()
                    view?.profile_fullname?.text = user?.getFullname()
                    view?.profile_bio?.text = user?.getBio()

                }
            }
        })
    }

    private fun getFollowing() {
        val following = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Following")
        following.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    view?.user_following?.text = snapshot.childrenCount.toString()


                }
            }
        })
    }

    private fun cekFllwFllwingStat() {
        val following = firebaseUser.uid.let { it ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it.toString())
                .child("Following")
        }
        if (following != null) {
            following.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        view?.btn_editProfile?.text = "Following"
                    } else {
                        view?.btn_editProfile?.text = "Follow"
                    }
                }
            })
        }
    }

    private fun getFollowers() {
        val followers = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Followers")
        followers.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    view?.user_followers?.text = snapshot.childrenCount.toString()
                }
            }

        })
    }

    override fun onStop() {
        super.onStop()
        val pref = context?.getSharedPreferences("PREF", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREF", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREF", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

}