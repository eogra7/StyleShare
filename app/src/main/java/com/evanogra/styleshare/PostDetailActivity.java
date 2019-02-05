package com.evanogra.styleshare;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class PostDetailActivity extends AppCompatActivity {

    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("StyleShare");
        setSupportActionBar(toolbar);

        post = (Post) getIntent().getExtras().get("post");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PostDetailFragment mFragment= new PostDetailFragment();
        fragmentTransaction.replace(R.id.container_post_detail, mFragment)
                .commit();

        mFragment.setPost(post);

    }

}
