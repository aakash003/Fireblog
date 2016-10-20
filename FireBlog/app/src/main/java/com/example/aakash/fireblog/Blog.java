package com.example.aakash.fireblog;

/**
 * Created by Aakash on 16-Oct-16.
 */

public class Blog {
    private String title;
    private String image;
    private String desc;
    private String username;

    public Blog()
    {

    }
    public Blog(String username,String title,String image,String desc){
        this.desc=desc;
        this.title=title;
        this.image=image;
        this.username = username;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
