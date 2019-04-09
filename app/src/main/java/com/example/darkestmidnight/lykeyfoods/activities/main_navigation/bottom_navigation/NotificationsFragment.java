package com.example.darkestmidnight.lykeyfoods.activities.main_navigation.bottom_navigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.darkestmidnight.lykeyfoods.R;
import com.example.darkestmidnight.lykeyfoods.activities.main_navigation.adapters.ShowNotificationsAdapter;
import com.example.darkestmidnight.lykeyfoods.interfaces.RetrivUserRecivFriendReq;
import com.example.darkestmidnight.lykeyfoods.models.Notifications;
import com.example.darkestmidnight.lykeyfoods.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
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

    SharedPreferences ShPreference;
    static String MyPREFERENCES = "API Authentication";   //// TODO:: Change the name of preferences everywhere
    String currentUserID = "Current User ID";

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
        getUserNotifications(strSignedInUID);
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

    private void getUserNotifications(final String userID) {
        retrieveSentFriendReqNotif(new ProcessNotifData() {
            @Override
            public void putNotifDataToRecycView(List<Notifications> notif) {
                RecyclerView notificationsRecycVw;
                ShowNotificationsAdapter notificationsAdapter;

                notificationsAdapter = new ShowNotificationsAdapter(notif);
                RecyclerView.LayoutManager nLayoutManager = new LinearLayoutManager(getContext());
                notificationsRecycVw = (RecyclerView) getActivity().findViewById(R.id.notifRcyclrView);
                notificationsRecycVw.setLayoutManager(nLayoutManager);
                notificationsRecycVw.setItemAnimator(new DefaultItemAnimator());
                notificationsRecycVw.setAdapter(notificationsAdapter);
                notificationsAdapter.notifyDataSetChanged();
            }
        }, userID);

        /*
        final List<String> receivedFriendReq = new ArrayList<>();
        retrieveReceivedFriendReqIds(new RetrivUserRecivFriendReq() {
            @Override
            public void setRetrievedRecivReqIds(List<String> array) {
                //TODO: set the empty List string above with this!
                receivedFriendReq.addAll(array);
            }
        }, userID);

        if (!receivedFriendReq.isEmpty()) {
            //TODO: do the main notification retrieval here? FIX THIS!
            retrieveSentFriendReqNotif(new ProcessNotifData() {
                @Override
                public void putNotifDataToRecycView(List<Notifications> notif) {
                    RecyclerView notificationsRecycVw;
                    ShowNotificationsAdapter notificationsAdapter;

                    notificationsAdapter = new ShowNotificationsAdapter(notif);
                    RecyclerView.LayoutManager nLayoutManager = new LinearLayoutManager(getContext());
                    notificationsRecycVw = (RecyclerView) getActivity().findViewById(R.id.notifRcyclrView);
                    notificationsRecycVw.setLayoutManager(nLayoutManager);
                    notificationsRecycVw.setItemAnimator(new DefaultItemAnimator());
                    notificationsRecycVw.setAdapter(notificationsAdapter);
                    notificationsAdapter.notifyDataSetChanged();
                }
            }, userID);
        }*/
    }

    /**
     * Async for retrieving notifications from Firebase
     * PARAMS:
     * userID is the id of the current user signed in
     * receivedNotifFrom is the username of the user who the current user received a notification from
     **/
    private void retrieveSentFriendReqNotif(final ProcessNotifData notifData, final String userID) {
        //TODO: clean this mess! ProcessNotifData interface, is that final? or can be changed
        //final String finalNotifType = notifType;
        DatabaseReference retrieveNotifRef = rootRef.child(userID);

        retrieveNotifRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int dsIterator = 0;
                List<Notifications> acceptReqNotifList = new ArrayList<>();
                List<Notifications> friendReqNotifList = new ArrayList<>();
                //Notifications sentReqNotifReqIds = new Notifications();
                for (DataSnapshot ds : dataSnapshot.child("notifications/sentFriendReqNotif").getChildren()) {
                    if (ds.hasChild(String.valueOf(dsIterator))) {
                        friendReqNotifList.add(new Notifications(ds.child(String.valueOf(dsIterator)).child("type").getValue(String.class),
                                ds.child(String.valueOf(dsIterator)).child("username").getValue(String.class),
                                ds.child(String.valueOf(dsIterator)).child("date").getValue(Date.class)));
                    }
                    dsIterator++;
                }
                notifData.putNotifDataToRecycView(friendReqNotifList);
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
    private void retrieveReceivedFriendReqIds(final RetrivUserRecivFriendReq retrivFriendReq, final String currentUserID) {
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
        void putNotifDataToRecycView(List<Notifications> notif);
    }
}
