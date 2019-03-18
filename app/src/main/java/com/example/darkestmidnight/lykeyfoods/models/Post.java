package com.example.darkestmidnight.lykeyfoods.models;

public class Post {
    int id;
    String title;
    String content;
    String date_created;

    // sets values for the posts
    public Post(int p_id, String p_title, String p_content) {
        this.id = p_id;
        this.title = p_title;
        this.content = p_content;
        //this.date_created = p_date_created;
    }

    public int getPostId() {
        return this.id;
    }

    public String getPostTitle() {
        return this.title;
    }

    public String getPostContent() {
        return this.content;
    }

    public String getPostDateCreated() {
        return this.date_created;
    }
}
