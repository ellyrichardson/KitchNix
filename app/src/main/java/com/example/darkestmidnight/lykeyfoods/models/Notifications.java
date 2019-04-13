package com.example.darkestmidnight.lykeyfoods.models;

import java.util.Date;

public class Notifications {
    //int id;
    String type;
    String username;
    String postName;
    String userPicture;
    String status;
    Date dateTime;


    public Notifications(String type, String status, String username, Date dateTime) {
        this.type = type;
        this.status = status;
        this.username = username;
        this.dateTime = dateTime;
    }

    public Notifications(String type, String status, String username, String postName, Date dateTime) {
        this.type = type;
        this.status = status;
        this.username = username;
        this.postName = postName;
        this.dateTime = dateTime;
    }

    public String getNotifType() {
        return this.type;
    }
    
    public String getNotifUsername() {
        return this.username;
    }

    public String getNotifUserPic() {
        return this.userPicture;
    }

    public String getNotifPostName() {
        return this.postName;
    }

    public String getNotifStatus() {return this.status;}

    public Date getNotifDateTime() {
        return this.dateTime;
    }
}
