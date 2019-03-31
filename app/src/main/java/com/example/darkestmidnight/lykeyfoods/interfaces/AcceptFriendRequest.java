package com.example.darkestmidnight.lykeyfoods.interfaces;

/**
 * This interface was designed as callback functionms to go under the accepting friend requests functions*/
public interface AcceptFriendRequest {
    void addToSenderFriendsList(final String senderID, final String receiverID, final String receiverUsername);
    void addToReceiverFriendsList(final String receiverID, final String senderID, final String senderUsername);
    void removeFromSentRequest(final String senderID, final String receiverUsername);
    void removeFromReceivedRequest(final String receiverID, final String senderUsername);
    void sendAcceptedRequestNotif(final String receiverID, final String senderID, final String receiverUsername);
}
