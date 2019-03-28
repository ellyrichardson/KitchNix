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
    String currentUserID = "Current User";

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
        sRFullName = (TextView) view.findViewById(R.id.sRUFullNameET);

        addFriendBtn = (Button) view.findViewById(R.id.sRUAddFriendBtn);
        sentRequestBtn = (Button) view.findViewById(R.id.sRUFriendReqSentBtn);
        acceptRequestBtn = (Button) view.findViewById(R.id.sRUAcceptRequestBtn);
        wereFriendsBtn = (Button) view.findViewById(R.id.sRUWeFriendsBtn);

        final String strVisitedUserID = getArguments().getString("sRUserID");

        ShPreference = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        // converts Stringed userID back to Int
        int userSignedIn = ShPreference.getInt(currentUserID, 0);
        // converts the userSignedIn id to string
        final String strSignedInUID = userSignedIn + "";

        // checks if the current User visited has been sent a friend Request
        checkFriendRequestStatus(new ButtonStatus() {
            @Override
            public void setButtonStatus(int choice, int button) {
                /**
                 * The choice params is for the choose if to show or hide buttons.
                 * The buttons params selects which buttons are to show or hide
                 */

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
        }, strSignedInUID, strVisitedUserID);

        // when the existing Friends button was pressed
        addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sentRequestBtn.setVisibility(View.VISIBLE);
                addFriendBtn.setVisibility(View.GONE);
                acceptRequestBtn.setVisibility(View.GONE);
                wereFriendsBtn.setVisibility(View.GONE);
                addToFriends(strVisitedUserID, strSignedInUID);
            }
        });
        //TODO: Check conflicts on showing status, could be friends but not really
        // when accepting friend request from visited user
        acceptRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptFriendRequest(strVisitedUserID, strSignedInUID);
                sentRequestBtn.setVisibility(View.GONE);
                addFriendBtn.setVisibility(View.GONE);
                acceptRequestBtn.setVisibility(View.GONE);
                wereFriendsBtn.setVisibility(View.VISIBLE);
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

    // function to Add Friends
    @Override
    public void addToFriends(String uVisited, final String uSignedIn) {
        // TODO: change this to actual feature requirements, this is a test only
        // to put the sent request to the logged in user's sentFriendRequests
        DatabaseReference sentFriendReqRef = rootRef.child(uSignedIn + "/sentFriendRequests");
        sentFriendReqRef.push().setValue(uVisited);

        // To put the request to addedUser receivedFriendRequests
        DatabaseReference receivedFriendReqRef = rootRef.child(uVisited + "/receivedFriendRequests");
        receivedFriendReqRef.push().setValue(uSignedIn);
    }

    @Override
    public void acceptFriendRequest(final String receiverID, final String senderID) {

        // senderReceiver[0] is the sender, senderReceiver[1] is the receiver
        final boolean[] senderReceiver = new boolean[2];

        // checks if the id of the receiver exists in the sentFriendRequests of the sender
        checkSenderReq(receiverID);
        // checks if the id of the sender exists in the receivedFriendRequests of the receiver
        checkReceiverReq(senderID);

        // checks if the request exists on the sentRequests of the sender and the receivedRequests of the receiver
        if (senderReceiver[0] == senderReceiver[1]) {
            // add them to friends if they both exist
            DatabaseReference senderAddFriendRef = rootRef.child(receiverID + "/friends");
            senderAddFriendRef.push().setValue(senderID);
            DatabaseReference receiverAddFriendRef = rootRef.child(senderID + "/friends");
            receiverAddFriendRef.push().setValue(receiverID);
        }
    }

    private void deleteValuesFromRequests(String accptUID, String signedInUID, String aUIDDelete, String sUIDDelete) {
        final String strSignedInUID = signedInUID;

        DatabaseReference checkSenderReqRef = rootRef.child(accptUID + "/sentFriendRequests");
        DatabaseReference checkReceiverReqRef = rootRef.child(strSignedInUID + "/receivedFriendRequests");
    }

    @Override
    public void declineFriendRequest() {

    }

    @Override
    public void checkIfRequestExist() {

    }

    private void checkFriendRequestStatus(final ButtonStatus buttonsCallback, final String strSignedInUID, final String strVisitedUserID) {
        final DatabaseReference checkFriendRequestsRef = database.getReference("friend_requests/test/" + strSignedInUID);
        checkFriendRequestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // choice is 1 to show buttons, then select which buttons to show with second params
                if (dataSnapshot.hasChild("/friends/" + strVisitedUserID)) {
                    buttonsCallback.setButtonStatus(1, 1);
                }
                else if (dataSnapshot.hasChild("/sentFriendRequest/" + strVisitedUserID)) {
                    buttonsCallback.setButtonStatus(1, 2);
                }
                else if (dataSnapshot.hasChild("/receivedFriendRequests/" + strVisitedUserID)) {
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

    /**
     * checkSenderReq:
     * Checks if the sender has the id of the receiver in sentFriendRequests
     */
    private void checkSenderReq (final String receiverID) {
        DatabaseReference checkSenderReqRef = rootRef.child(receiverID + "/sentFriendRequests");
        checkSenderReqRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // stores children of sentFriendRequests from the sending user
                ArrayList<String> sentReqArrayList = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    // gets ids in the sentFriendRequests of the adding user
                    String sentFriendReqIds = ds.getValue(String.class);
                    sentReqArrayList.add(sentFriendReqIds);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    /**
     * checkReceiverReq:
     * Checks if the receiver has the id of the sender in receivedFriendRequests
     */
    private void checkReceiverReq (final String senderID) {
        DatabaseReference checkReceiverReqRef = rootRef.child(senderID + "/receivedFriendRequests");

        checkReceiverReqRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // stores children of receivedFriendRequests from the receiving user
                ArrayList<String> receivedReqArrayList = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    // gets ids in the receivedFriendRequests of the receiving user
                    String receivedFriendReqIds = ds.getValue(String.class);
                    receivedReqArrayList.add(receivedFriendReqIds);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
