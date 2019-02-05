package com.evanogra.styleshare;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

public class TabActivity extends AppCompatActivity implements HomeTabFragment.OnFragmentInteractionListener, SearchTabAdapter.OnUserSelectedListener, MyAdapter.OnUserSelectedListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private static User selectedUser;
    private static Firebase mFirebase;

    public TabActivity() {
        mFirebase = new Firebase("https://resplendent-inferno-4210.firebaseio.com/");
    }

    public Firebase getFirebase() {
        return mFirebase;
    }

    public void setFirebase(Firebase mFirebase) {
        this.mFirebase = mFirebase;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("StyleShare");
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //mViewPager.setOffscreenPageLimit(0);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        setupTabLayout(mTabLayout);

        String uid = mFirebase.getAuth().getUid();
        Query queryRef = mFirebase.child("users").orderByKey().equalTo(uid);
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                System.out.println(dataSnapshot.toString());
                User user = dataSnapshot.getValue(User.class);
                user.setKey(dataSnapshot.getKey());
                setSelectedUser(user);
//                System.out.println(user.getKey());

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

    public TabLayout getmTabLayout() {
        return mTabLayout;
    }

    public void setPage(int i) {
        mViewPager.setCurrentItem(i, true);
    }


    private void setupTabLayout(TabLayout mTabLayout) {
        mTabLayout.setupWithViewPager(mViewPager);

        Drawable d0 = getApplication().getResources().getDrawable(R.drawable.ic_action_action_home, getTheme());
        d0.mutate().setColorFilter(Color.parseColor("#FF5722"), PorterDuff.Mode.MULTIPLY);
        mTabLayout.getTabAt(0).setIcon(d0);

        Drawable d1 = getApplication().getResources().getDrawable(R.drawable.ic_action_action_search, getTheme());
        d1.mutate().setColorFilter(Color.parseColor("#727272"), PorterDuff.Mode.MULTIPLY);
        mTabLayout.getTabAt(1).setIcon(d1);

        Drawable d2 = getApplication().getResources().getDrawable(R.drawable.ic_action_image_timer_auto, getTheme());
        d2.mutate().setColorFilter(Color.parseColor("#727272"), PorterDuff.Mode.MULTIPLY);
        mTabLayout.getTabAt(2).setIcon(d2);

        Drawable d3 = getApplication().getResources().getDrawable(R.drawable.ic_action_file_cloud_upload, getTheme());
        d3.mutate().setColorFilter(Color.parseColor("#727272"), PorterDuff.Mode.MULTIPLY);
        mTabLayout.getTabAt(3).setIcon(d3);

        mTabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                Drawable d = tab.getIcon();
                d.mutate().setColorFilter(Color.parseColor("#FF5722"), PorterDuff.Mode.MULTIPLY);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                Drawable d = tab.getIcon();
                d.mutate().setColorFilter(Color.parseColor("#727272"), PorterDuff.Mode.MULTIPLY);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
                if(tab.getPosition()==2) {
                    Query queryRef = mFirebase.child("users").orderByKey().equalTo(mFirebase.getAuth().getUid().toString());
                    queryRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            User user = dataSnapshot.getValue(User.class);
                            user.setKey(dataSnapshot.getKey());
                            ProfileFragment profileFragment = null;
                            for(Fragment f : getSupportFragmentManager().getFragments()) {
                                if (f instanceof ProfileFragment) {
                                    profileFragment = (ProfileFragment) f;
                                }
                            }
                            profileFragment.loadData(user);
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
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_edit_profile) {
            Intent intent = new Intent(getApplicationContext(), SetupProfileActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onUserSelected(User user) {
        System.out.println("Interface Triggered");
        setPage(2);
        ProfileFragment profileFragment = null;
        for(Fragment f : getSupportFragmentManager().getFragments()) {
            if (f instanceof ProfileFragment) {
                profileFragment = (ProfileFragment) f;
            }
        }
        setSelectedUser(user);
        profileFragment.loadData(user);

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tab, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private FragmentManager fragmentManager;
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragmentManager = fm;
        }

        public FragmentManager getFragmentManager() {
            return fragmentManager;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
            Fragment mFragment = PlaceholderFragment.newInstance(position + 1);

            switch(position) {
                case 0:
                    mFragment = new HomeTabFragment();
                    break;
                case 1:
                    mFragment = new SearchTabFragment();
                    break;
                case 2:
                    mFragment = new ProfileFragment();
                    break;
                case 3:
                    mFragment = new UploadTabFragment();
                    break;
            }
            return mFragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }


    }
}
