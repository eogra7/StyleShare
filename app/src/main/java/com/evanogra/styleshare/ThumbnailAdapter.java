package com.evanogra.styleshare;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evan on 2/23/2016.
 */
public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ViewHolder> {
    private List<Post> posts;
    private Context context;

    public ThumbnailAdapter(Context context) {
        this.context = context;
        posts = new ArrayList<>();
    }

    public void addPost(Post newPost) {
        boolean duplicate = false;
        for(Post post : posts) {
            System.out.println(post.getImage_url().toString());
            System.out.println(newPost.getImage_url().toString());
            if (post.getImage_url().toString().equals(newPost.getImage_url().toString())) {
                duplicate = true;
            }
        }

        if(!duplicate) {
            posts.add(newPost);
            notifyDataSetChanged();
        }

    }
    public void clearPosts() {
        posts = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public ThumbnailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(context).inflate(R.layout.fragment_post_profile, parent, false);
        ImageView imageView = (ImageView) v.findViewById(R.id.profile_post_thumbnail);

        ViewHolder vh = new ViewHolder(v, imageView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ThumbnailAdapter.ViewHolder holder, int position) {
        final Post post = posts.get(position);
        Picasso.with(context)
                .load(post.getImage_url())
                .centerCrop()
                .fit()
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("post", post);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ViewHolder(View itemView, ImageView imageView) {
            super(itemView);
            this.imageView = imageView;
        }
    }
}
