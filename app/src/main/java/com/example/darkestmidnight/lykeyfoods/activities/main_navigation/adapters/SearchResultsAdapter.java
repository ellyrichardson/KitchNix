package com.example.darkestmidnight.lykeyfoods.activities.main_navigation.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.darkestmidnight.lykeyfoods.R;
import com.example.darkestmidnight.lykeyfoods.activities.main_navigation.MainNavigation;
import com.example.darkestmidnight.lykeyfoods.activities.main_navigation.users_info.UserFragment;
import com.example.darkestmidnight.lykeyfoods.models.User;

import java.util.List;

public class SearchResultsAdapter extends  RecyclerView.Adapter<SearchResultsAdapter.CustomViewHolder>{
    private List<User> users;

    MainNavigation mainNavHelper;

    UserFragment userFragHelper;

    Context context;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView userFN, userUN, sRFullName;

        public CustomViewHolder(View view) {
            super(view);
            userFN = (TextView) view.findViewById(R.id.userFullName);
            userUN = (TextView) view.findViewById(R.id.userUsername);

            // when userUN is clicked, then go to their profile
            userUN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // pass MainNavigation instance to the adapter
                    if (context instanceof MainNavigation) {

                        Bundle args = new Bundle();

                        String rUsername = userUN.getText().toString();
                        String tmpFullName = "";
                        String tmpUserID = "";
                        String tmpUsername = "";

                        // gets other info of matching user based on username
                        for (int i = 0; i < getItemCount(); i++) {
                            if (users.get(i).getUsername() == rUsername) {
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
    }

    public SearchResultsAdapter(Context context, List<User> users){
        this.context = context;
        this.users = users;
    }

    @Override
    public SearchResultsAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_list, parent, false);

        return new SearchResultsAdapter.CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchResultsAdapter.CustomViewHolder holder, int position) {
        User user = users.get(position);
        String uFN = user.getFirstName() + " " + user.getLastName();
        holder.userFN.setText(uFN);
        holder.userUN.setText(user.getUsername());
        // test
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
