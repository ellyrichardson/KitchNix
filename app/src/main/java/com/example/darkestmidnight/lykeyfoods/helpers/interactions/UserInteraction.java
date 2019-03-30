package com.example.darkestmidnight.lykeyfoods.helpers.interactions;

public class UserInteraction {

    public interface FriendRequests {
        void acceptFriendRequest(String receiverUsername, String senderUsername, String receiverID, String senderID);
        void checkIfRequestExist();
        void declineFriendRequest();
        void addToFriends(String uVisited, final String uSignedIn, final String visitedUsername, final String signedInUsernam);
    }

    interface ChatRooms {
        void getChatSessions();
    }
}
