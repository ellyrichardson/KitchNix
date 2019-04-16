package com.example.darkestmidnight.lykeyfoods.activities.main_navigation.bottom_navigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
        retrieveReceivedFriendReqIds(new RetrivUserRecivFriendReq() {
            @Override
            public void setRetrievedRecivReqIds(List<String> array) {
                //TODO: set the empty List string above with this!
                receivedFriendReq.addAll(array);
            }
        }, strSignedInUID);

        getUserNotifications(strSignedInUID, receivedFriendReq, getContext());
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

    private void getUserNotifications(final String userID, List<String>receivedFriendReq, final Context context) {

        retrieveSentFriendReqNotif(new ProcessNotifData() {
            @Override
            public void putNotifDataToRecycView(List<Notifications> notif, List<User> users) { ;
                /*for (int j = 0; j < notif.size(); j++) {

                    try {
                        JSONArray pJObjArray = new JSONArray(getUserInfoOfNotif(notif.get(j).getNotifUsername()));

                        Log.e("TAG", "Length" + pJObjArray.length());

                        for (int i = 0; i < pJObjArray.length(); i++) {
                            // puts the current iterated JSON object from the array to another temporary object
                            JSONObject pJObj_data = pJObjArray.getJSONObject(i);

                            // inputs necesarry elements for the User
                            users.add(new User(pJObj_data.getString("first_name"), pJObj_data.getString("last_name"), pJObj_data.getString("username"), pJObj_data.getString("email"), pJObj_data.getInt("id")));
                        }

                    } catch (JSONException e) {
                        //Toast.makeText(JSonActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        Log.d("Json","Exception = "+e.toString());
                    }

                }*/

                RecyclerView notificationsRecycVw;
                ShowNotificationsAdapter notificationsAdapter;

                notificationsAdapter = new ShowNotificationsAdapter(getContext(), notif, users);
                RecyclerView.LayoutManager nLayoutManager = new LinearLayoutManager(getContext());
                notificationsRecycVw = (RecyclerView) getActivity().findViewById(R.id.notifRcyclrView);
                notificationsRecycVw.setLayoutManager(nLayoutManager);
                notificationsRecycVw.setItemAnimator(new DefaultItemAnimator());
                notificationsRecycVw.setAdapter(notificationsAdapter);
                notificationsAdapter.notifyDataSetChanged();
            }

            @Override
            public String getUserInfoOfNotif(String username) {


                /*Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                StringBuilder result = new StringBuilder();

                // gets the AccessToken
                ShPreference = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                String APIAuthentication = "Bearer " + ShPreference.getString(accessToken, "");

                HttpURLConnection httpURLConnection = null;
                try {

                    // Sets up connection to the URL (params[2] from .execute in "login")
                    httpURLConnection = (HttpURLConnection) new URL("http://192.168.1.4:8000/api/search/?search=" + username).openConnection();
                    // Sets the request method for the URL
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setRequestProperty ("Authorization", APIAuthentication);

                    // Tells the URL that I want to read the response data
                    httpURLConnection.setDoInput(true);

                    // // Representing the input stream to URL response
                    InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    // reading the input stream / response from the url
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // Disconnects socket after using
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }

                Log.e("TAG", result.toString());
                return result.toString();*/
                return "";
            }

        }, userID, receivedFriendReq);
    }

    /**
     * Async for retrieving notifications from Firebase
     * PARAMS:
     * userID is the id of the current user signed in
     * receivedNotifFrom is the username of the user who the current user received a notification from
     **/
    private void retrieveSentFriendReqNotif(final ProcessNotifData notifData, final String userID, final List<String> recivFriendReqIds) {
        //TODO: clean this mess! ProcessNotifData interface, is that final? or can be changed
        //final String finalNotifType = notifType;
        final DatabaseReference retrieveNotifRef = rootRef.child(userID);

        retrieveNotifRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int dsIterator = 0;
                List<Notifications> acceptReqNotifList = new ArrayList<>();
                List<Notifications> friendReqNotifList = new ArrayList<>();

                // date format for the date of the Notification from the Firebase
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

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

                StringBuilder result = new StringBuilder();
                List<User> users = new ArrayList<>();

                // gets the AccessToken
                ShPreference = getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                String APIAuthentication = "Bearer " + ShPreference.getString(accessToken, "");

                for (int i = 0; i < friendReqNotifList.size(); i++) {
                    HttpURLConnection httpURLConnection = null;
                    try {

                        // Sets up connection to the URL (params[2] from .execute in "login")
                        httpURLConnection = (HttpURLConnection) new URL("http://192.168.1.4:8000/api/search/?search=" + friendReqNotifList.get(i).getNotifUsername()).openConnection();
                        // Sets the request method for the URL
                        httpURLConnection.setRequestMethod("GET");
                        httpURLConnection.setRequestProperty ("Authorization", APIAuthentication);

                        // Tells the URL that I want to read the response data
                        httpURLConnection.setDoInput(true);

                        // // Representing the input stream to URL response
                        InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());

                        Log.e("TAG", "Length" + in);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                        // reading the input stream / response from the url
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        // Disconnects socket after using
                        if (httpURLConnection != null) {
                            httpURLConnection.disconnect();
                        }
                    }

                    Log.e("TAG", result.toString());
                    //result.toString();

                    try {
                        JSONArray pJObjArray = new JSONArray(result.toString());

                        Log.e("TAG", "Length" + pJObjArray.length());

                        for (int j = 0; i < pJObjArray.length(); i++) {
                            // puts the current iterated JSON object from the array to another temporary object
                            JSONObject pJObj_data = pJObjArray.getJSONObject(j);

                            // inputs necesarry elements for the User
                            users.add(new User(pJObj_data.getString("first_name"), pJObj_data.getString("last_name"), pJObj_data.getString("username"), pJObj_data.getString("email"), pJObj_data.getInt("id")));
                        }

                    } catch (JSONException e) {
                        //Toast.makeText(JSonActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        Log.d("Json","Exception = "+e.toString());
                    }
                }

                notifData.putNotifDataToRecycView(friendReqNotifList, users);
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
        void putNotifDataToRecycView(List<Notifications> notif, List<User> users);
        String getUserInfoOfNotif(String username);
        //void initializeUserObjOfNotif(String result);

    }
}
