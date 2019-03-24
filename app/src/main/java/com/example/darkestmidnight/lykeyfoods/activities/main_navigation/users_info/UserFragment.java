package com.example.darkestmidnight.lykeyfoods.activities.main_navigation.users_info;

import android.content.Context;
import android.content.Intent;
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
public class UserFragment extends Fragment implements UserInteraction.FriendRequests{
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

        // sentRequested[0] for checking if sent request, sentRequested[1] for checking if received request
        final boolean[] sentRequested = new boolean[2];
        final boolean[] weFriends = new boolean[1];

        final String sRUID = getArguments().getString("sRUserID");

        ShPreference = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        // converts Stringed userID back to Int
        int userSignedIn = ShPreference.getInt(currentUserID, 0);
        // converts the userSignedIn id to string
        final String strSignedInUID = userSignedIn + "";

        //TODO: Refactor the whole adding friendRequest and make it elegant if possible
        final DatabaseReference checkSentRequestsRef = database.getReference("friend_requests/test/" + strSignedInUID + "/sentFriendRequests");
        final DatabaseReference checkReceivedRequestsRef = database.getReference("friend_requests/test/" + strSignedInUID + "/receivedFriendRequests");
        final DatabaseReference checkMyFriendsRef = database.getReference("friend_requests/test/" + strSignedInUID + "/friends");
        //final DatabaseReference innerCheckRequestsRef = checkRequestsRef.child("sentFriendRequests");

        addFriendBtn.setVisibility(View.GONE);
        sentRequestBtn.setVisibility(View.GONE);
        acceptRequestBtn.setVisibility(View.GONE);
        wereFriendsBtn.setVisibility(view.GONE);

        // checks if the current User visited has been sent a friend Request
        checkSentRequestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // stores children of sentFriendRequests for checking if sent a request to the user already
                ArrayList<String> sentReqArrayList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String sentFriendReqIds = ds.getValue(String.class);
                    sentReqArrayList.add(sentFriendReqIds);
                }
                // hides add friend button if already sent a request to the user
                if (sentReqArrayList.contains(sRUID)) {
                    sentRequested[0] = true;
                    //sentRequestBtn.setVisibility(View.VISIBLE);
                }
                else {
                    //addFriendBtn.setVisibility(View.VISIBLE);
                    sentRequested[0] = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // checks if the current User visiting received a request from the visited user
        checkReceivedRequestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // stores children of sentFriendRequests for checking if sent a request to the user already
                ArrayList<String> receivedReqArrayList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String receivedFriendReqIds = ds.getValue(String.class);
                    receivedReqArrayList.add(receivedFriendReqIds);
                }
                // sets receivedRequest element of boolean to true if received request
                if (receivedReqArrayList.contains(sRUID)) {
                    sentRequested[1] = true;
                    //sentRequestBtn.setVisibility(View.VISIBLE);
                }
                else {
                    //addFriendBtn.setVisibility(View.VISIBLE);
                    sentRequested[1] = false;
                }

                // when the existing Friends button was pressed
                addFriendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentRequestBtn.setVisibility(View.VISIBLE);
                        addFriendBtn.setVisibility(View.GONE);
                        addToFriends(sRUID, strSignedInUID);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // checks if the current User visiting received a request from the visited user
        checkMyFriendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // stores children of sentFriendRequests for checking if sent a request to the user already
                ArrayList<String> myFriendsArrayList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String myFriendsIds = ds.getValue(String.class);
                    myFriendsArrayList.add(myFriendsIds);
                }
                // sets receivedRequest element of boolean to true if received request
                if (myFriendsArrayList.contains(sRUID)) {
                    weFriends[0] = true;
                    //sentRequestBtn.setVisibility(View.VISIBLE);
                }
                else {
                    //addFriendBtn.setVisibility(View.VISIBLE);
                    weFriends[0] = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //addFriendBtn = (Button) view.findViewById(R.id.sRUAddFriendBtn);

        // checks if signed in user is friends with the visited user, if not then allow friend requesting
        if (!weFriends[0]) {
            if (sentRequested[0]  && !sentRequested[1]) {
                sentRequestBtn.setVisibility(View.VISIBLE);
            }
            else if (!sentRequested[0] && sentRequested[1]) {
                acceptRequestBtn.setVisibility(View.VISIBLE);
            }
            else if (!sentRequested[0] && !sentRequested[1]) {
                addFriendBtn.setVisibility(View.VISIBLE);
            }
        }
        else {
            wereFriendsBtn.setVisibility(View.VISIBLE);
        }

        // when the existing Friends button was pressed
        addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sentRequestBtn.setVisibility(View.VISIBLE);
                addFriendBtn.setVisibility(View.GONE);
                acceptRequestBtn.setVisibility(View.GONE);
                wereFriendsBtn.setVisibility(View.GONE);
                addToFriends(sRUID, strSignedInUID);
            }
        });
        //TODO: Check conflicts on showing status, could be friends but not really
        // when accepting friend request from visited user
        acceptRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptFriendRequest(sRUID, strSignedInUID);
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
    public void addToFriends(String uName, String uSignedIn) {
        final String strUID = uSignedIn;

        // TODO: change this to actual feature requirements, this is a test only
        // to put the sent request to the logged in user's sentFriendRequests
        DatabaseReference sentFriendReqRef = rootRef.child(strUID + "/sentFriendRequests");
        //usersRef.push().setValue("sentFriendRequests");
        //DatabaseReference sentFriendReqRef = usersRef.child("sentFriendRequests");
        sentFriendReqRef.push().setValue(uName);

        // To put the request to addedUser receivedFriendRequests
        DatabaseReference receivedFriendReqRef = rootRef.child(uName + "/receivedFriendRequests");
        //DatabaseReference receivedFriendReqRef = addedUserRef.child("receivedFriendRequests");
        receivedFriendReqRef.push().setValue(strUID);

        Map<String, FriendRequestJSON> friendRequests = new HashMap<>();
        //friendRequests.child("")
    }

    @Override
    public void acceptFriendRequest(String accptUID, String signedInUID) {
        final String strSignedInUID = signedInUID;

        // senderReceiver[0] is the sender, senderReceiver[1] is the receiver
        final boolean[] senderReceiver = new boolean[2];


        DatabaseReference checkSenderReqRef = rootRef.child(accptUID + "/sentFriendRequests");
        DatabaseReference checkReceiverReqRef = rootRef.child(strSignedInUID + "/receivedFriendRequests");

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
                // checks if the requester have actually sent the request if the userSignedIn received it
                if (sentReqArrayList.contains(strSignedInUID)) {
                    senderReceiver[0] = true;
                }
                else {
                    senderReceiver[0] = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                // checks if the requester have actually sent the request if the userSignedIn received it
                if (receivedReqArrayList.contains(strSignedInUID)) {
                    senderReceiver[1] = true;
                }
                else {
                    senderReceiver[1] = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // checks if the request exists on the sentRequests of the sender and the receivedRequests of the receiver
        if (senderReceiver[0] == senderReceiver[1]) {
            // add them to friends if they both exist
            DatabaseReference senderAddFriendRef = rootRef.child(accptUID + "/friends");
            senderAddFriendRef.push().setValue(strSignedInUID);
            DatabaseReference receiverAddFriendRef = rootRef.child(strSignedInUID + "/friends");
            receiverAddFriendRef.push().setValue(accptUID);
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
}
