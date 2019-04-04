package com.example.darkestmidnight.lykeyfoods.models;

import java.util.Date;

public class Notifications {
    int id, type;
    String username;
    String postName;
    String userPicture;
    Date dateTime;

    public Notifications(int id, int type, String username, Date dateTime) {
        this.id = id;
        this.type = type;
        this.username = username;
        this.dateTime = dateTime;
    }

    public Notifications(int id, int type, String username, String postName, Date dateTime) {
        this.id = id;
        this.type = id;
        this.username = username;
        this.postName = postName;
        this.dateTime = dateTime;
    }

    public int getNotificationId() {
        return this.id;
    }

    public int getNotificationType() {
        return this.type;
    }
    
    public String getNotficationUsername() {
        return this.username;
    }

    public String getNotificationUserPic() {
        return this.userPicture;
    }

    public String getNotificationPostName() {
        return this.postName;
    }

    public Date getDateTime() {
        return this.dateTime;
    }
}
