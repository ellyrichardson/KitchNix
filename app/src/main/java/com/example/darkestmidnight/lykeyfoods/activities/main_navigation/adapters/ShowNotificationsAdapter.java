package com.example.darkestmidnight.lykeyfoods.activities.main_navigation.adapters;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.darkestmidnight.lykeyfoods.R;
import com.example.darkestmidnight.lykeyfoods.models.Notifications;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ShowNotificationsAdapter extends  RecyclerView.Adapter<ShowNotificationsAdapter.CustomViewHolder>{
    private List<Notifications> notifications;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView notifTitle, notifStatus, notifDate;

        public CustomViewHolder(View view) {
            super(view);
            notifTitle = (TextView) view.findViewById(R.id.notifTitleTxV);
            notifStatus = (TextView) view.findViewById(R.id.notifStatusTxV);
            notifDate = (TextView) view.findViewById(R.id.notifDateTxV);
        }
    }

    public ShowNotificationsAdapter(List<Notifications> notifications){
        this.notifications = notifications;
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
        }
        else {
            holder.itemView.setBackgroundColor(Color.parseColor("#F7EFE2"));
        }

        Date dateFormat = notification.getNotifDateTime();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

        if (notification.getNotifType().equals("0")) {
            holder.notifTitle.setText(notification.getNotifUsername() + "sent you a friend request!");
            holder.notifStatus.setText(notification.getNotifStatus());
            holder.notifDate.setText(dateFormatter.format(dateFormat));
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
}
