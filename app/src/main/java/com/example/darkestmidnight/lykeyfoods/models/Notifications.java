package com.example.darkestmidnight.lykeyfoods.models;

public class Notifications {
    int id, type;
    String username;
    String postName;
    String userPicture;

    public Notifications(int id, int type, String username) {
        this.id = id;
        this.type = type;
        this.username = username;
    }

    public Notifications(int id, int type, String username, String postName) {
        this.id = id;
        this.type = id;
        this.username = username;
        this.postName = postName;
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
}
