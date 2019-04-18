package com.example.darkestmidnight.lykeyfoods.activities.main_navigation.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.darkestmidnight.lykeyfoods.R;
import com.example.darkestmidnight.lykeyfoods.activities.main_navigation.MainNavigation;
import com.example.darkestmidnight.lykeyfoods.models.Notifications;
import com.example.darkestmidnight.lykeyfoods.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ShowNotificationsAdapter extends  RecyclerView.Adapter<ShowNotificationsAdapter.CustomViewHolder>{
    private List<Notifications> notifications;
    private List<User> users;
    private String currentUserID;

    Context context;

    /*SharedPreferences ShPreference;
    String currentUserID = "Current User ID";
    //SharedPreferences.Editor PrefEditor;
    String accessToken = "Access Token";*/

    //ShPreference = getSharedPreferences(currentUserID, Context.MODE_PRIVATE);

    // converts the userSignedIn id to string
    //final String strSignedInUID = ShPreference.getInt(currentUserID, 0) + "";

    // to update status of the user's notification holder
    final FirebaseDatabase firebase = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = firebase.getReference("users");

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView notifTitle, notifStatus, notifDate;

        public CustomViewHolder(View view) {
            super(view);
            notifTitle = (TextView) view.findViewById(R.id.notifTitleTxV);
            notifStatus = (TextView) view.findViewById(R.id.notifStatusTxV);
            notifDate = (TextView) view.findViewById(R.id.notifDateTxV);
            notifTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // pass MainNavigation instance to the adapter
                    if (context instanceof MainNavigation) {

                        Bundle args = new Bundle();

                        String tempArray[] = notifTitle.getText().toString().split(" ");
                        // Only puts the username of the notification sender
                        String nUsername = tempArray[0];
                        String tmpFullName = "";
                        String tmpUserID = "";
                        String tmpUsername = "";

                        // gets other info of matching user based on username
                        for (int i = 0; i < users.size(); i++) {
                            if (users.get(i).getUsername().equals(nUsername)) {
                                tmpFullName = users.get(i).getFirstName() + " " + users.get(i).getLastName();
                                tmpUserID = users.get(i).getUserId() + "";
                                tmpUsername = users.get(i).getUsername();
                            }
                        }
                        // to pass result values to userFragment
                        args.putString("sRFullName", tmpFullName);
                        args.putString("sRUserID", tmpUserID);
                        args.putString("sRUsername", tmpUsername);
                        ((MainNavigation) context).userFragment(args);
                    }


                }
            });
        }

        /**
         * This OnClick is not executing for some reason.
         * TODO: Fix this!
         **/
        @Override
        public void onClick(View v) {

            if (context instanceof MainNavigation) {

                Bundle args = new Bundle();

                String tempArray[] = notifTitle.getText().toString().split(" ");
                // Only puts the username of the notification sender
                String nUsername = tempArray[0];
                String tmpFullName = "";
                String tmpUserID = "";
                String tmpUsername = "";

                // gets other info of matching user based on username
                for (int i = 0; i < getItemCount(); i++) {
                    if (users.get(i).getUsername().equals(nUsername)) {
                        tmpFullName = users.get(i).getFirstName() + " " + users.get(i).getLastName();
                        tmpUserID = users.get(i).getUserId() + "";
                        tmpUsername = users.get(i).getUsername();
                    }
                }
                // to pass result values to userFragment
                args.putString("sRFullName", tmpFullName);
                args.putString("sRUserID", tmpUserID);
                args.putString("sRUsername", tmpUsername);
                ((MainNavigation) context).userFragment(args);
            }
        }
    }

    public ShowNotificationsAdapter(Context context, List<Notifications> notifications, List<User> users, final String currentUserID){
        this.context = context;
        this.notifications = notifications;
        this.users = users;
        this.currentUserID = currentUserID;
    }

    @Override
    public ShowNotificationsAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notifications_list, parent, false);

        return new ShowNotificationsAdapter.CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ShowNotificationsAdapter.CustomViewHolder holder, int position) {
        Notifications notification = notifications.get(position);
        if (!notification.getNotifStatus().equals("opened")) {
            holder.itemView.setBackgroundColor(Color.parseColor("#A6C246"));
            //TODO: must somehow need the current user ID in the "<SignedInID>"
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getUsername().equals(notification.getNotifUsername())) {
                    //DatabaseReference friendReqRef = database.getReference(senderID + "/friend_requests/");
                    DatabaseReference senderSentFriendReqRef = rootRef.child(currentUserID + "/notifications/sentFriendReqNotif/" + users.get(i).getUserId());
                    //sentFriendReqRef.push().setValue(uVisited);
                    senderSentFriendReqRef.child("status").setValue("opened");
                }
            }
        }
        else {
            holder.itemView.setBackgroundColor(Color.parseColor("#F7EFE2"));
        }

        Date dateFormat = notification.getNotifDateTime();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

        if (notification.getNotifType().equals("0")) {
            holder.notifTitle.setText(notification.getNotifUsername() + " sent you a friend request!");
            holder.notifStatus.setText(notification.getNotifStatus());
            holder.notifDate.setText(dateFormatter.format(dateFormat));
        }

        //Notifications working but not as intended. BufferedReader not getting executed in the Firebase function inside NotificationsFragment. Added some updates on ShowNotificationsAdapter ready to use

        /*if (notification.getNotifStatus().equals("unopened")) {
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getUsername().equals(notification.getNotifUsername())) {
                    //DatabaseReference friendReqRef = database.getReference(senderID + "/friend_requests/");
                    DatabaseReference senderSentFriendReqRef = rootRef.child("<SignedInID>" + "/notifications/sentFriendReqNotif/" + users.get(i).getUserId());
                    //sentFriendReqRef.push().setValue(uVisited);
                    senderSentFriendReqRef.child("status").setValue("opened");
                }
            }
        }*/
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
}
