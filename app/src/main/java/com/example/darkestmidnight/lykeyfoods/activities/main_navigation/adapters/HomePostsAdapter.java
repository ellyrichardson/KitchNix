package com.example.darkestmidnight.lykeyfoods.activities.main_navigation.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.darkestmidnight.lykeyfoods.R;
import com.example.darkestmidnight.lykeyfoods.models.Post;

import java.util.List;

public class HomePostsAdapter extends  RecyclerView.Adapter<HomePostsAdapter.CustomViewHolder>{
    private List<Post> posts;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView pTitle, pContent;

        public CustomViewHolder(View view) {
            super(view);
            pTitle = (TextView) view.findViewById(R.id.userFullName);
            pContent = (TextView) view.findViewById(R.id.postContent);
        }
    }

    public HomePostsAdapter(List<Post> posts){
        this.posts = posts;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.posts_list, parent, false);

        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.pTitle.setText(post.getPostTitle());
        holder.pContent.setText(post.getPostContent());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
