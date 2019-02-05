package com.evanogra.styleshare;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Evan on 2/18/2016.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context context;
    private List<Comment> comments;

    public CommentAdapter() {
        comments = new ArrayList<>();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    public void addComment(Comment comment) {
        for(Comment c : comments) {
            if(c.getKey().equals(comment.getKey())) {
                return;
            }
        }
        comments.add(comment);
        notifyDataSetChanged();
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        notifyDataSetChanged();
    }

    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(context).inflate(R.layout.comment_fragment, parent, false);
        ImageView imageView = (ImageView) v.findViewById(R.id.comment_author_pic);
        TextView authorView = (TextView) v.findViewById(R.id.comment_author_name);
        TextView bodyView = (TextView) v.findViewById(R.id.comment_body);
        TextView timeView = (TextView) v.findViewById(R.id.comment_time);

        ViewHolder vh = new ViewHolder(v, authorView, bodyView, imageView, timeView);

        return vh;
    }

    @Override
    public void onBindViewHolder(final CommentAdapter.ViewHolder holder, int position) {
        final Comment comment = comments.get(position);
        holder.bodyView.setText(comment.getBody());
        final TextView authorView = holder.authorView;
        User.getRealName(comment.getAuthor(), new UserDataCallback() {
            @Override
            public void onFinished(String data) {
                authorView.setText(data);
            }
        });
        User.getProfilePicUrl(comment.getAuthor(), new UserDataCallback() {
            @Override
            public void onFinished(String data) {
                Picasso.with(context)
                        .load(data)
                        .transform(new CircleTransform())
                        .fit()
                        .into(holder.imageView);
            }
        });
        Date d = new Date(comment.getTime()*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM. dd");
        final String date = sdf.format(d);
        holder.timeView.setText(date);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase mFirebase = new Firebase("https://resplendent-inferno-4210.firebaseio.com/");
                Query queryRef = mFirebase.child("users").orderByKey().equalTo(comment.getAuthor());
                queryRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        User user = dataSnapshot.getValue(User.class);
                        user.setKey(dataSnapshot.getKey());
                        if(context instanceof TabActivity) {
                            TabActivity activity = (TabActivity) context;
                            activity.setSelectedUser(user);
                            activity.onUserSelected(user);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        if(comments==null) {
            return 0;
        }
        return comments.size();
    }

    public void clearComments() {
        comments.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView authorView;
        private TextView bodyView;
        private ImageView imageView;
        private TextView timeView;

        public ViewHolder(View itemView, TextView authorView, TextView bodyView, ImageView imageView, TextView timeView) {
            super(itemView);
            this.authorView = authorView;
            this.bodyView = bodyView;
            this.imageView = imageView;
            this.timeView = timeView;
        }
    }
}
