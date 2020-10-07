package com.example.instagramclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.instaclone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.instagramclone.fragment.ProfileFragment
import com.example.instagramclone.model.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class SearchAdapter (private var context: Context, private val mUser: List<User>, private var isFragment: Boolean = false) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>(){

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_search,parent, false)
        return SearchViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val user = mUser[position]
        holder.userNameTxtView.text = user.getUsername()
        holder.fullNameTxtView.text = user.getFullname()

        Picasso.get()
            .load(user.getImage())
            .into(holder.userProfileImage)

        checkFollowingStat(user.getUID(), holder.followButton)

        holder.itemView.setOnClickListener {
            val pref = context.getSharedPreferences("PREF", Context.MODE_PRIVATE).edit()
            pref.putString("profileId", user.getUID())
            pref.apply()

            (context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.item_container,
                    ProfileFragment()
                ).commit()

        }

        holder.followButton.setOnClickListener {
            if(holder.followButton.text.toString() == "Follow"){

                firebaseUser?.uid.let { it ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it.toString())
                        .child("Following").child(user.getUID())
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseUser?.uid.let { it ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUID())
                                        .child("Following").child(it.toString())
                                        .setValue(true).addOnCompleteListener {
                                            if (task.isSuccessful){

                                            }
                                        }
                                }
                            }
                        }
                }
            }else{
                firebaseUser?.uid.let {it->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it.toString())
                        .child("Following").child(user.getUID())
                        .removeValue().addOnCompleteListener {task ->
                            if (task.isSuccessful){
                                firebaseUser?.uid.let {
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUID())
                                        .child("Followers").child(it.toString())
                                        .removeValue().addOnCompleteListener {task ->
                                            if (task.isSuccessful){

                                            }
                                        }
                                }
                            }
                        }
                }
            }
        }

    }

    private fun checkFollowingStat(uid: String, followButton: Button) {
        val following = firebaseUser?.uid.let {
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it.toString())
                .child("Following")
        }

        following.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(uid).exists()){
                    followButton.text = "Following"
                }else{
                    followButton.text = "Follow"
                }
            }
        })
    }

     class SearchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val userNameTxtView : TextView = itemView.findViewById(R.id.tv_uname)
        val fullNameTxtView : TextView = itemView.findViewById(R.id.tv_fullname)
        val userProfileImage : CircleImageView = itemView.findViewById(R.id.img_view_search)
        val followButton : Button = itemView.findViewById(R.id.btn_follow)
    }
}