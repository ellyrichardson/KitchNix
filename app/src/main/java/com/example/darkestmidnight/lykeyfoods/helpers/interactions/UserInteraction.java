package com.example.darkestmidnight.lykeyfoods.helpers.interactions;

public class UserInteraction {

    public interface FriendRequests {
        void acceptFriendRequest(String accptUID, String signedInUID);
        void checkIfRequestExist();
        void declineFriendRequest();
        void addToFriends(String uName, String signedInID);
    }

    interface ChatRooms {
        void getChatSessions();
    }
}
