package com.example.darkestmidnight.lykeyfoods.activities.main_navigation.bottom_navigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.darkestmidnight.lykeyfoods.R;
import com.example.darkestmidnight.lykeyfoods.helpers.async.GetNotifUser;
import com.example.darkestmidnight.lykeyfoods.interfaces.RetrivInfoForNotif;
import com.example.darkestmidnight.lykeyfoods.models.Notifications;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotificationsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
//TODO: Fix connection leakage with http
public class NotificationsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    final FirebaseDatabase firebase = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = firebase.getReference("users");

    GetNotifUser getNotifUser;

    SharedPreferences ShPreference;
    static String MyPREFERENCES = "API Authentication";   //// TODO:: Change the name of preferences everywhere
    String currentUserID = "Current User ID";
    //SharedPreferences.Editor PrefEditor;
    String accessToken = "Access Token";

    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
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
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        ShPreference = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        // converts the userSignedIn id to string
        final String strSignedInUID = ShPreference.getInt(currentUserID, 0) + "";

        final List<String> receivedFriendReq = new ArrayList<>();
        final List<String> friendsIds = new ArrayList<>();
        retrieveReceivedFriendReqIds(new RetrivInfoForNotif() {
            @Override
            public void setRetrievedRecivReqIds(List<String> array) {
                //TODO: set the empty List string above with this!
                receivedFriendReq.addAll(array);
            }

            @Override
            public void setRetrievedFriendsIds(List<String> array) {
                friendsIds.addAll(array);
            }
        }, strSignedInUID);

        getUserNotifications(strSignedInUID, receivedFriendReq, friendsIds, getContext());
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

    private void getUserNotifications(final String userID, List<String>receivedFriendReq, List<String> friendIds,final Context context) {

        retrieveSentFriendReqNotif(new ProcessNotifData() {
            @Override
            public void putNotifDataToRecycView(List<Notifications> notif, final String userID) {
                getNotifUser = new GetNotifUser(getContext(), getActivity(), notif);
                getNotifUser.execute("http://192.168.1.4:8000/api/search/?search=", userID);
            }

            @Override
            public String getUserInfoOfNotif(String username) {

                return "";
            }

        }, userID, receivedFriendReq, friendIds);
    }

    /**
     * Async for retrieving notifications from Firebase
     * PARAMS:
     * userID is the id of the current user signed in
     * receivedNotifFrom is the username of the user who the current user received a notification from
     **/
    private void retrieveSentFriendReqNotif(final ProcessNotifData notifData, final String userID, final List<String> recivFriendReqIds, final List<String> friendsIds) {
        //TODO: clean this mess! ProcessNotifData interface, is that final? or can be changed
        //final String finalNotifType = notifType;
        final DatabaseReference retrieveNotifRef = rootRef.child(userID);

        retrieveNotifRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int dsIterator = 0;
                List<Notifications> acceptReqNotifList = new ArrayList<>();
                List<Notifications> friendReqNotifList = new ArrayList<>();
                List<Notifications> allNotifList = new ArrayList<>();

                // date format for the date of the Notification from the Firebase
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

                // to set the sentFriendReq notifications for the logged in user
                for (int i = 0; i < recivFriendReqIds.size(); i++) {
                    if (dataSnapshot.hasChild("notifications/sentFriendReqNotif/" + recivFriendReqIds.get(i))) {
                        DataSnapshot ds = dataSnapshot.child("notifications/sentFriendReqNotif/" + recivFriendReqIds.get(i));
                        try {
                            // the string of date from the Firebase will be parsed to a Date object before inputting.
                            friendReqNotifList.add(new Notifications(ds.child("type").getValue(String.class),ds.child("status").getValue(String.class),
                                    ds.child("username").getValue(String.class),
                                    dateFormatter.parse(ds.child("date").getValue(String.class))));
                        } catch (java.text.ParseException e) {
                            // this catch is needed for parsing the date format
                            e.printStackTrace();
                        }
                    }
                }

                //TODO: Issue here is that the data is not getting called to the correct data structure branch. Must traverse to children more.
                // to set the acceptedFriendReq notif for the logged in user
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    try {
                        // the string of date from the Firebase will be parsed to a Date object before inputting.
                        acceptReqNotifList.add(new Notifications(ds.child("type").getValue(String.class),ds.child("status").getValue(String.class),
                                ds.child("username").getValue(String.class),
                                dateFormatter.parse(ds.child("date").getValue(String.class))));
                    } catch (java.text.ParseException e) {
                        // this catch is needed for parsing the date format
                        e.printStackTrace();
                    }
                }

                allNotifList.addAll(friendReqNotifList);
                allNotifList.addAll(acceptReqNotifList);


                notifData.putNotifDataToRecycView(allNotifList, userID);
                //notifData.putNotifDataToRecycView(acceptReqNotifList, userID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Mainly called in getUserNotifications()
     * Retrieves ids from the receivedFriendRequests of the user
     **/
    private void retrieveReceivedFriendReqIds(final RetrivInfoForNotif retrivFriendReq, final String currentUserID) {
        DatabaseReference sentReceivedReqIdsRef = rootRef.child(currentUserID + "/friend_requests/receivedFriendRequests");

        sentReceivedReqIdsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> receivedFriendReqIds = new ArrayList<>();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    receivedFriendReqIds.add(ds.getValue(String.class));
                }

                retrivFriendReq.setRetrievedRecivReqIds(receivedFriendReqIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private interface ProcessNotifData {
        void putNotifDataToRecycView(List<Notifications> notif,final String userID);
        String getUserInfoOfNotif(String username);
        //void initializeUserObjOfNotif(String result);

    }
}
