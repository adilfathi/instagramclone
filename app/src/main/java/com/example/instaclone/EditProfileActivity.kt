package com.example.instagramclone

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.instaclone.MainActivity
import com.example.instaclone.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.example.instagramclone.model.User
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private var cekInfoProfile = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfilePicture: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicture = FirebaseStorage.getInstance().reference.child("Profile Picture")

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val move = Intent(this@EditProfileActivity, LoginActivity::class.java)
            startActivity(move)
            finish()
        }

        edit_photo_profile.setOnClickListener {
            cekInfoProfile = "Clicked"

            CropImage.activity()
                .setAspectRatio(1, 1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this)
        }

        btn_saveInfo.setOnClickListener {

            if (cekInfoProfile == "Clicked") {
                uploadImageProfileAndUpdateInfoProfile()
            } else {
                updateUserInfoOnly()
            }
        }

        userInfo()
    }

    private fun uploadImageProfileAndUpdateInfoProfile() {

        when {
            imageUri == null -> Toast.makeText(this, "Select Image...", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(fullname_edit.text.toString()) -> {
                Toast.makeText(this, "Cannot be Empty", Toast.LENGTH_SHORT).show()

            }
            TextUtils.isEmpty(username_edit.text.toString()) -> {
                Toast.makeText(this, "Cannot be Empty", Toast.LENGTH_SHORT).show()

            }
            TextUtils.isEmpty(bio_edit.text.toString()) -> {
                Toast.makeText(this, "Cannot be Empty", Toast.LENGTH_SHORT).show()

            }
            else -> {
                val progressDialog = ProgressDialog(this@EditProfileActivity)
                progressDialog.setTitle("Updating Profile")
                progressDialog.setMessage("Kela atuh, ai maneh!")
                progressDialog.show()

                val fileRef = storageProfilePicture?.child(firebaseUser.uid + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef!!.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (task.isSuccessful){
                        task.exception.let {
                            throw it!!
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful){
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val userRef = FirebaseDatabase.getInstance().reference.child("Users")

                        val userMap = HashMap<String, Any>()
                        userMap["fullname"] = fullname_edit.text.toString()
                        userMap["username"] = username_edit.text.toString()
                        userMap["bio"]      = bio_edit.text.toString()
                        userMap["image"]    = myUrl

                        userRef.child(firebaseUser.uid).updateChildren(userMap)
                        Toast.makeText(this, "Your profile has been updated", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                        progressDialog.dismiss()
                    }else{
                        progressDialog.dismiss()
                    }
                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK
            && data != null
        ) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            set_profile_image.setImageURI(imageUri)
        }
    }

    private fun updateUserInfoOnly() {

        when {
            TextUtils.isEmpty(fullname_edit.text.toString()) -> {
                Toast.makeText(this, "Cannot be Empty", Toast.LENGTH_SHORT).show()

            }
            TextUtils.isEmpty(username_edit.text.toString()) -> {
                Toast.makeText(this, "Cannot be Empty", Toast.LENGTH_SHORT).show()

            }
            TextUtils.isEmpty(bio_edit.text.toString()) -> {
                Toast.makeText(this, "Cannot be Empty", Toast.LENGTH_SHORT).show()

            }
            else -> {
                val userRef = FirebaseDatabase.getInstance().reference
                    .child("Users")

                val userMap = HashMap<String, Any>()
                userMap["fullname"] = fullname_edit.text.toString()
                userMap["username"] = username_edit.text.toString()
                userMap["bio"] = bio_edit.text.toString()

                userRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText(this, "Profile has been updated", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }
    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().getReference()
            .child("Users").child(firebaseUser.uid)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue<User>(
                    User::class.java)

                fullname_edit.setText(user?.getFullname())
                username_edit.setText(user?.getUsername())
                bio_edit.setText(user?.getBio())
            }
        })
    }
}