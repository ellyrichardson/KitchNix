package com.example.darkestmidnight.lykeyfoods.interfaces.UserFragmentInterfaces;

/**
 * This interface was designed as callback functionms to go under the accepting friend requests functions*/
public interface AcceptFriendRequest {
    void addToSenderFriendsList(final String cbSenderID, final String cbReceiverID, final String cbReceiverUsername);
    void addToReceiverFriendsList(final String cbReceiverID, final String cbSenderID, final String cbSenderUsername);
    void removeFromSentRequest(final String cbSenderID, final String cbReceiverUsername);
    void removeFromReceivedRequest(final String cbReceiverID, final String cbSenderUsername);
}
