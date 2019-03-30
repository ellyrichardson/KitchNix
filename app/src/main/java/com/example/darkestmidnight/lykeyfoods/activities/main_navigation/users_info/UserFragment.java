package com.example.darkestmidnight.lykeyfoods.activities.main_navigation.users_info;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.darkestmidnight.lykeyfoods.R;
import com.example.darkestmidnight.lykeyfoods.helpers.interactions.UserInteraction;
import com.example.darkestmidnight.lykeyfoods.interfaces.FirebaseGetData;
import com.example.darkestmidnight.lykeyfoods.interfaces.ButtonStatus;
import com.example.darkestmidnight.lykeyfoods.interfaces.UserFragmentInterfaces.AcceptFriendRequest;
import com.example.darkestmidnight.lykeyfoods.models.FriendRequestJSON;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment implements UserInteraction.FriendRequests {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference rootRef = database.getReference("friend_requests/users");
    DatabaseReference rootRef = database.getReference("friend_requests/test");

    SharedPreferences ShPreference;
    SharedPreferences.Editor PrefEditor;
    static String MyPREFERENCES = "API Authentication";   //// TODO:: Change the name of preferences everywhere
    String currentUserID = "Current User ID";
    String currentUsername = "Current Username";

    TextView sRFullName;
    Button addFriendBtn, sentRequestBtn, acceptRequestBtn, wereFriendsBtn;

    private OnFragmentInteractionListener mListener;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param srArgs Parameter 2
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, Bundle srArgs) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(srArgs);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        //TODO: Refactor button status when buttons are pressed inside this function.
        sRFullName = (TextView) view.findViewById(R.id.sRUFullNameET);

        addFriendBtn = (Button) view.findViewById(R.id.sRUAddFriendBtn);
        sentRequestBtn = (Button) view.findViewById(R.id.sRUFriendReqSentBtn);
        acceptRequestBtn = (Button) view.findViewById(R.id.sRUAcceptRequestBtn);
        wereFriendsBtn = (Button) view.findViewById(R.id.sRUWeFriendsBtn);

        final String strVisitedUserID = getArguments().getString("sRUserID");
        final String visitedUsername = getArguments().getString("sRUsername");

        ShPreference = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        // converts the userSignedIn id to string
        final String strSignedInUID = ShPreference.getInt(currentUserID, 0) + "";
        final String signedInUsername = ShPreference.getString(currentUsername, "");

        // checks if the current User visited has been sent a friend Request and then an appropriate button
        setFriendRequestButtons(strSignedInUID, strVisitedUserID, visitedUsername);

        // when the existing Friends button was pressed
        addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sentRequestBtn.setVisibility(View.VISIBLE);
                addFriendBtn.setVisibility(View.GONE);
                acceptRequestBtn.setVisibility(View.GONE);
                wereFriendsBtn.setVisibility(View.GONE);
                addToFriends(strVisitedUserID, strSignedInUID, visitedUsername, signedInUsername);
            }
        });
        //TODO: Check conflicts on showing status, could be friends but not really
        // when accepting friend request from visited user
        acceptRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptFriendRequest(strSignedInUID, strVisitedUserID, signedInUsername, visitedUsername);
                sentRequestBtn.setVisibility(View.GONE);
                addFriendBtn.setVisibility(View.GONE);
                acceptRequestBtn.setVisibility(View.GONE);
                wereFriendsBtn.setVisibility(View.VISIBLE);
            }
        });

        wereFriendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFriend(strSignedInUID, strVisitedUserID, signedInUsername, visitedUsername);
                sentRequestBtn.setVisibility(View.GONE);
                addFriendBtn.setVisibility(View.VISIBLE);
                acceptRequestBtn.setVisibility(View.GONE);
                wereFriendsBtn.setVisibility(View.GONE);
            }
        });

        // sets the name with the Full Name; called from SearchResultsAdapter
        sRFullName.setText(getArguments().getString("sRFullName"));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * addToFriends:
     * Mainly called in button press in onViewCreated
     * Adds friend requests to both user's appropriate category of requests status
     **/
    @Override
    public void addToFriends(String uVisited, final String uSignedIn, final String visitedUsername, final String signedInUsername) {
        // TODO: reformat the parameter organization of this function
        // to put the sent request to sender's sentFriendRequests
        addFriendToSentReq(uSignedIn, uVisited, visitedUsername);
        // To put the request to receiver's receivedFriendRequests
        addFriendToRecReq(uVisited, uSignedIn, signedInUsername);
    }

    /**
     * Mainly called in addToFriends
     **/
    private void addFriendToSentReq(final String senderID, final String receiverID, final String receiverUsername) {
        // to put the sent request to the logged in user's sentFriendRequests
        DatabaseReference senderSentFriendReqRef = rootRef.child(senderID + "/sentFriendRequests");
        //sentFriendReqRef.push().setValue(uVisited);
        senderSentFriendReqRef.child(receiverUsername).setValue(receiverID);
    }

    /**
     * Mainly called in addToFriends
     **/
    private void addFriendToRecReq(final String receiverID, final String senderID, final String senderUsername) {
        // To put the request to addedUser receivedFriendRequests
        DatabaseReference receiverReceivedFriendReqRef = rootRef.child(receiverID + "/receivedFriendRequests");
        //receivedFriendReqRef.push().setValue(uSignedIn);
        receiverReceivedFriendReqRef.child(senderUsername).setValue(senderID);
    }

    /**
     * acceptFriendRequest:
     * Uses AcceptFriendRequest Interface for the callback of Firebase Async checkReceiverRequest
     */
    @Override
    public void acceptFriendRequest(final String receiverID, final String senderID, final String receiverUsername, final String senderUsername) {
        changeRequestStatus(new AcceptFriendRequest() {
            @Override
            public void addToSenderFriendsList(final String cbSenderID, final String cbReceiverID, final String cbReceiverUsername) {
                // adds the receiver to the friends list of the sender
                DatabaseReference senderAddToFriendsRef = rootRef.child(cbSenderID + "/friends");
                senderAddToFriendsRef.child(cbReceiverUsername).setValue(cbReceiverID);
            }

            @Override
            public void addToReceiverFriendsList(final String cbReceiverID, final String cbSenderID, final String cbSenderUsername) {
                // adds the sender to the friends list of the receiver
                DatabaseReference receiverAddToFriendsRef = rootRef.child(cbReceiverID + "/friends");
                receiverAddToFriendsRef.child(cbSenderUsername).setValue((cbSenderID));
            }

            @Override
            public void removeFromSentRequest(final String cbSenderID, final String cbReceiverUsername) {
                // removes the user from sentRequest list of the sender
                DatabaseReference removeFromSentRequestsRef = rootRef.child(cbSenderID + "/sentFriendRequests");
                removeFromSentRequestsRef.child(cbReceiverUsername).removeValue();
            }

            @Override
            public void removeFromReceivedRequest(final String cbReceiverID, final String cbSenderUsername) {
                // removes the sender from the receivedRequests of the current user
                DatabaseReference removeFromReceivedRequestsRef = rootRef.child(cbReceiverID + "/receivedFriendRequests");
                removeFromReceivedRequestsRef.child(cbSenderUsername).removeValue();
            }
        }, receiverID, senderID, receiverUsername, senderUsername);
    }

    /**
     * checkReceiverReq:
     * changes request statuses of both receiver and sender by removing requests and adding friends to their lists
     */
    private void changeRequestStatus (final AcceptFriendRequest checkReceiverReqCB, final String receiverID, final String senderID, final String receiverUsername, final String senderUsername) {
        DatabaseReference checkReceiverReqRef = rootRef.child(receiverID + "/receivedFriendRequests");
        checkReceiverReqRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // callbacks for actual changing of requests and friends list
                if (dataSnapshot.hasChild(senderUsername)) { ;
                    checkReceiverReqCB.addToSenderFriendsList(senderID, receiverID, receiverUsername);
                    checkReceiverReqCB.addToReceiverFriendsList(receiverID, senderID, senderUsername);
                    checkReceiverReqCB.removeFromSentRequest(senderID, receiverUsername);
                    checkReceiverReqCB.removeFromReceivedRequest(receiverID, senderUsername);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void declineFriendRequest() {

    }

    /**
     * removeFriend:
     * Mainly called under a button press in onViewCreated
     * Deletes friend for both sides of the users
     **/
    @Override
    public void removeFriend(final String deleterID, final String toBeDeletedID, final String deleterUsername, final String toBeDeletedUsername) {
        removeFriendInDeleter(deleterID, toBeDeletedUsername);
        removeFriendInToBeDel(toBeDeletedID, deleterUsername);
    }

    /**
     * Mainly called under removeFriend
     **/
    private void removeFriendInDeleter(final String deleterID, final String toBeDeletedUsername) {
        DatabaseReference removeFriendInDeleterRef = rootRef.child(deleterID + "/friends");
        removeFriendInDeleterRef.child(toBeDeletedUsername).removeValue();
    }

    /**
     * Mainly called under removeFriend
     **/
    private void removeFriendInToBeDel(final String toBeDeletedID, final String deleterUsername) {
        DatabaseReference removeFriendInToBeDelRef = rootRef.child(toBeDeletedID + "/friends");
        removeFriendInToBeDelRef.child(deleterUsername).removeValue();
    }

    /**
     * Mainly called in onViewCreated()
     * The choice params is for the choose if to show or hide buttons.
     * The buttons params selects which buttons are to show or hide
     */
    private void setFriendRequestButtons(final String receiverID, final String senderID, final String senderUsername) {
        checkFriendRequestStatus(new ButtonStatus() {
            @Override
            public void setButtonStatus(int choice, int button) {
                addFriendBtn.setVisibility(View.GONE);
                sentRequestBtn.setVisibility(View.GONE);
                acceptRequestBtn.setVisibility(View.GONE);
                wereFriendsBtn.setVisibility(View.GONE);

                // if choosed to show buttons
                if (choice == 1) {
                    // show buttons depending on the friendRequest status
                    if (button == 1) {
                        wereFriendsBtn.setVisibility(View.VISIBLE);
                    }
                    else if (button == 2) {
                        sentRequestBtn.setVisibility(View.VISIBLE);
                    }
                    else if (button == 3) {
                        acceptRequestBtn.setVisibility(View.VISIBLE);
                    }
                    else {
                        addFriendBtn.setVisibility(View.VISIBLE);
                    }
                }
            }
        }, receiverID, senderUsername, senderID);
    }

    /**
     * Mainly called in setFriendRequestButtons()
     * Will check the firebase for setting appropriate buttons
     **/
    private void checkFriendRequestStatus(final ButtonStatus buttonsCallback, final String strSignedInUID, final String visitedUsername, final String strVisitedUID) {
        final DatabaseReference checkFriendRequestsRef = database.getReference("friend_requests/test/" + strSignedInUID);
        checkFriendRequestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // choice is 1 to show buttons, then select which buttons to show with second params
                if (dataSnapshot.hasChild("friends/" + visitedUsername)) {
                    buttonsCallback.setButtonStatus(1, 1);
                }
                else if (dataSnapshot.hasChild("sentFriendRequests/" + visitedUsername)) {
                    buttonsCallback.setButtonStatus(1, 2);
                }
                else if (dataSnapshot.hasChild("receivedFriendRequests/" + visitedUsername)) {
                    buttonsCallback.setButtonStatus(1, 3);
                }
                else {
                    buttonsCallback.setButtonStatus(1, 4);;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
