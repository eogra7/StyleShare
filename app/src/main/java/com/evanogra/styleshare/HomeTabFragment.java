package com.evanogra.styleshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeTabFragment} factory method to
 * create an instance of this fragment.
 */
public class HomeTabFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private Firebase mFirebase;
    private OnFragmentInteractionListener mListener;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MyAdapter myAdapter;

    public HomeTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_home_tab, container, false);
        mFirebase = new Firebase("https://resplendent-inferno-4210.firebaseio.com/");
        AuthData authData = mFirebase.getAuth();
        mFirebase.child("users").child(authData.getUid()).child("setup").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!(boolean) dataSnapshot.getValue()) {
                    setupProfile();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        List<Post> posts = new ArrayList<Post>();
        //final Post post1 = new Post("evan", "hi", "hi", "hi", System.currentTimeMillis()/1000L);
        //posts.add(post1);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.home_card_recycler);
        mRecyclerView.setHasFixedSize(true);
        //mLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        LinearLayoutManager layoutManager = new android.support.v7.widget.LinearLayoutManager(getActivity());
        //mLayoutManager.set
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);

        myAdapter = new MyAdapter(posts, getActivity().getApplicationContext(), getFragmentManager());
        myAdapter.setActivity((TabActivity) getActivity());
        mRecyclerView.setAdapter(myAdapter);
        //mRecyclerView.addItemDecoration(new MyItemDecoration(8));
        downloadPosts();

        return v;
    }


    private void downloadPosts() {
        mFirebase.child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myAdapter.removeAllPosts();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    //System.out.println(snapshot.toString());
                    Post mPost = snapshot.getValue(Post.class);
                    mPost.setKey(snapshot.getKey());
                    myAdapter.addPost(mPost);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void setupProfile() {
        Toast t = Toast.makeText(getActivity().getApplicationContext(), "Needs setup!", Toast.LENGTH_LONG);
        t.show();
        Intent intent = new Intent(getActivity().getApplicationContext(), SetupProfileActivity.class);
        startActivity(intent);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class MyItemDecoration extends RecyclerView.ItemDecoration {
        private final int mSpace;

        public MyItemDecoration(int mSpace) {
            this.mSpace = mSpace;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = mSpace;
            outRect.top = mSpace;
            outRect.left = mSpace;
            outRect.right = mSpace;
        }
    }
}
