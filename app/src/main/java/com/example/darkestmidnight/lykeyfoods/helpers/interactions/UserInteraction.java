package com.example.darkestmidnight.lykeyfoods.helpers.interactions;

public class UserInteraction {

    interface FriendRequests {
            void acceptFriendRequest();
            void checkIfRequestExist();
            void declineFriendRequest();
            void addToFriends();
    }

    interface ChatRooms {
        void getChatSessions();
    }
}
