package com.evanogra.styleshare;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.List;

/**
 * Created by Evan on 2/22/2016.
 */
public class SearchTabFragment extends Fragment {

    private Context context;
    private Firebase mFirebaseRef;

    public SearchTabFragment() {
        mFirebaseRef = new Firebase("https://resplendent-inferno-4210.firebaseio.com/");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_search_tab, container, false);
        context = getActivity();

        EditText searchText = (EditText) v.findViewById(R.id.search_tab_bar);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.search_results_recycler);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        final SearchTabAdapter searchTabAdapter = new SearchTabAdapter(getActivity(), getFragmentManager());
        recyclerView.setAdapter(searchTabAdapter);

        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_action_action_search);
        icon.setColorFilter(Color.parseColor("#88000000"), PorterDuff.Mode.MULTIPLY);
        searchText.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                searchTabAdapter.clearUsers();
                Query queryRef = mFirebaseRef.child("users").orderByKey();
                queryRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChild) {
                        User user = dataSnapshot.getValue(User.class);
                        user.setKey(dataSnapshot.getKey());
                        String name;
                        if(user.getName()==null) {
                            return;
                        }
                        name = user.getName().toLowerCase();
                        String query = s.toString().toLowerCase();
                        String chapter = user.getChapter().toLowerCase();
                        if (name.contains(query) || chapter.contains(query)) {
                            searchTabAdapter.addUser(user);
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

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return v;
    }
}
