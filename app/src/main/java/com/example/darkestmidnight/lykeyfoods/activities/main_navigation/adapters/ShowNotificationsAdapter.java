package com.example.darkestmidnight.lykeyfoods.activities.main_navigation.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.darkestmidnight.lykeyfoods.R;
import com.example.darkestmidnight.lykeyfoods.models.Notifications;

import java.util.List;

public class ShowNotificationsAdapter extends  RecyclerView.Adapter<ShowNotificationsAdapter.CustomViewHolder>{
    private List<Notifications> notifications;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView notifUsername, notifDescr;

        public CustomViewHolder(View view) {
            super(view);
            notifUsername = (TextView) view.findViewById(R.id.notifUsernameTxV);
            notifDescr = (TextView) view.findViewById(R.id.notifDescrTxV);
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
        holder.notifUsername.setText(notification.getNotficationUsername());
        holder.notifDescr.setText(notification.getNotificationType());
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
}
