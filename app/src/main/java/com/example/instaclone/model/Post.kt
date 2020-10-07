package com.example.instagramclone.model

class Post {
    private var postid      : String = ""
    private var postimage   : String = ""
    private var publisher   : String = ""
    private var description : String = ""

    constructor()
    constructor(postId: String, postimage: String, publisher: String, description: String) {
        this.postid      = postid
        this.postimage   = postimage
        this.publisher   = publisher
        this.description = description
    }

    fun getPostId():String{
        return postid
    }
    fun getPostImage():String{
        return postimage
    }
    fun getPublisher():String{
        return publisher
    }
    fun getDescription():String{
        return description
    }
    fun setPostId(postId: String){
        this.postid = postid
    }
    fun setPostImage(postImage: String){
        this.postimage = postimage
    }
    fun setPublisher(publisher: String){
        this.publisher = publisher
    }
    fun setDescription(description: String){
        this.description = description
    }

}