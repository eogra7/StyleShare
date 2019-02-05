package com.evanogra.styleshare;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.squareup.picasso.Picasso;

/**
 * Created by Evan on 2/23/2016.
 */
public class ProfileFragment extends Fragment {
    private Context context;
    private TabActivity tabActivity;
    private View v;
    private ThumbnailAdapter thumbnailAdapter;
    public ProfileFragment() {

    }

    public void loadData(User user) {
        String name = user.getName();
        System.out.println("Loading data for " + user.getName());

        String chapter = user.getChapter();
        TextView nameView = (TextView) v.findViewById(R.id.profile_name);
        TextView chapterView = (TextView) v.findViewById(R.id.profile_chapter);
        nameView.setText(name);
        chapterView.setText(chapter);
        Picasso.with(context)
                .load(user.getProfile_picture())
                .transform(new CircleTransform())
                .fit()
                .into((ImageView) v.findViewById(R.id.profile_pic));

        thumbnailAdapter.clearPosts();
        Query queryRef = tabActivity.getFirebase().child("posts").orderByChild("author").equalTo(user.getKey());
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post p = dataSnapshot.getValue(Post.class);
                p.setKey(dataSnapshot.getKey());
                thumbnailAdapter.addPost(p);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        tabActivity = (TabActivity) getActivity();
        v = inflater.from(context).inflate(R.layout.fragment_profile, container, false);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.profile_recycler);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(layoutManager);
        thumbnailAdapter = new ThumbnailAdapter(context);
        recyclerView.setAdapter(thumbnailAdapter);
        User user = tabActivity.getSelectedUser();
        loadData(user);
        return v;
    }



    @Override
    public void onResume() {
        super.onResume();

    }

}
