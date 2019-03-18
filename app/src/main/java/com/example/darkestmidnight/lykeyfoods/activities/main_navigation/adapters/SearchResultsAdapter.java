package com.example.darkestmidnight.lykeyfoods.activities.main_navigation.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.darkestmidnight.lykeyfoods.R;
import com.example.darkestmidnight.lykeyfoods.activities.main_navigation.MainNavigation;
import com.example.darkestmidnight.lykeyfoods.models.User;

import java.util.List;

public class SearchResultsAdapter extends  RecyclerView.Adapter<SearchResultsAdapter.CustomViewHolder>{
    private List<User> users;

    MainNavigation mainNavHelper;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView userFN, userUN;

        public CustomViewHolder(View view) {
            super(view);
            userFN = (TextView) view.findViewById(R.id.userFullName);
            userUN = (TextView) view.findViewById(R.id.userUsername);

            // when userUN is clicked, then go to their profile
            userUN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userUN.setText("this text was clicked");

                    mainNavHelper = new MainNavigation();
                    mainNavHelper.userFragment();
                }
            });
        }
    }

    public SearchResultsAdapter(List<User> users){
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
