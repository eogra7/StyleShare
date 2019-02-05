package com.evanogra.styleshare;


import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Evan on 2/17/2016.
 */
public class PostDetailFragment extends Fragment implements PostListener{
    private Post post;
    private User author;
    private Firebase mFirebase;
    private Activity activity;
    private final CommentAdapter commentAdapter;
    private Firebase postRef;

    private View v;

    private TextView postDetailAuthorName;
    private TextView postDetailAuthorDate;
    private ImageView postDetailAuthorPic;
    private ImageView postDetailImage;
    private TextView postDetailBody;
    private ImageView postDetailLikeButton;
    private ImageView postDetailCommentButton;
    private RecyclerView postDetailCommentsRecycler;
    private TextView postDetailCommentText;
    private Button postDetailCommentSubmitButton;
    private LinearLayout postDetailCommentSection;
    private LinearLayout postDetailAuthorContainer;
    private AppCompatCheckBox pollOptionOne;
    private AppCompatCheckBox pollOptionTwo;
    private TextView pollTitle;
    private LinearLayout pollContainer;

    public PostDetailFragment() {
        commentAdapter = new CommentAdapter();
        mFirebase = new Firebase("https://resplendent-inferno-4210.firebaseio.com/");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.layout_post_detail, container, false);
        activity = getActivity();

        //android.app.FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
        //fragmentTransaction.replace(R.id.container_post_detail, R.l)

        postDetailAuthorContainer = (LinearLayout) v.findViewById(R.id.post_detail_author_container);
        postDetailAuthorName = (TextView) v.findViewById(R.id.post_detail_author_name);
        postDetailAuthorDate = (TextView) v.findViewById(R.id.post_detail_author_date);
        postDetailAuthorPic = (ImageView) v.findViewById(R.id.post_detail_author_pic);
        postDetailImage = (ImageView) v.findViewById(R.id.post_detail_image);
        postDetailBody = (TextView) v.findViewById(R.id.post_detail_body);
        postDetailLikeButton = (ImageView) v.findViewById(R.id.post_detail_like_button);
        postDetailCommentButton = (ImageView) v.findViewById(R.id.post_detail_comment_button);
        postDetailCommentsRecycler = (RecyclerView) v.findViewById(R.id.post_detail_comments_recycler);
        postDetailCommentText = (TextView) v.findViewById(R.id.post_detail_comments_text);
        postDetailCommentSubmitButton = (Button) v.findViewById(R.id.post_detail_comment_submit);
        postDetailCommentSection = (LinearLayout) v.findViewById(R.id.post_detail_comments_section);
        pollOptionOne = (AppCompatCheckBox) v.findViewById(R.id.poll_option_one);
        pollOptionTwo = (AppCompatCheckBox) v.findViewById(R.id.poll_option_two);
        pollTitle = (TextView) v.findViewById(R.id.poll_title);
        pollContainer = (LinearLayout) v.findViewById(R.id.poll_container);

        RecyclerView.LayoutManager layoutManager = new org.solovyev.android.views.llm.LinearLayoutManager(activity);
        postDetailCommentsRecycler.setLayoutManager(layoutManager);
        commentAdapter.setContext(activity);
//        postDetailCommentsRecycler.addItemDecoration(new DividerDecoration(activity));
//        postDetailCommentsRecycler.addItemDecoration(new DividerDecoration(activity, R.drawable.line_divider));
        postDetailCommentsRecycler.setAdapter(commentAdapter);
        postDetailCommentsRecycler.setNestedScrollingEnabled(false);

        postDetailCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCommentsVisibility();
            }
        });

        postDetailAuthorContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// TODO: 2/25/2016
                /*activity.setSelectedUser(author);
                activity.onUserSelected(author);*/
            }
        });

        postDetailCommentSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = postDetailCommentText.getText().toString();
                if(body.isEmpty()) {
                    Toast toast = Toast.makeText(activity, "Comment cannot be blank!", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                Map<String, String> comment = new HashMap<>();
                AuthData authData = mFirebase.getAuth();
                comment.put("author", authData.getUid().toString());
                comment.put("body", body);
                comment.put("postID", post.getKey());
                mFirebase.child("comments").push().setValue(comment);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(post!=null) {
            notifyPostChanged();
        }
    }



    @Override
    public void setPost(Post post) {
        this.post = post;
    }

    private void notifyPostChanged() {
        if(post==null) {
            throw new RuntimeException("Post cannot be null");
        }
        loadAuthorData();
        postDetailImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                postDetailImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                loadPostData();
            }
        });
        loadPollData();
        toggleCommentsVisibility();


    }

    private void loadPostData() {
        String body = post.getBody();
        String imageUrl = post.getImage_url();
        postDetailBody.setText(body);
        Picasso.with(activity)
                .load(imageUrl)
                        //.fit()
                        //.centerCrop()
                .transform(new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap source) {
                        int mSize = postDetailImage.getWidth();
                        float scale;
                        int newSize;
                        Bitmap scaledBitmap;
                        scale = (float) mSize / source.getWidth();
                        newSize = (int) (source.getHeight() * scale);
                        scaledBitmap = Bitmap.createScaledBitmap(source, mSize, newSize, true);
                        if (scaledBitmap != source) {
                            source.recycle();
                        }
                        //OutputStream os = new
                        //scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, )
                        return scaledBitmap;
                    }

                    @Override
                    public String key() {
                        return "asd";
                    }
                })
                .into(postDetailImage);
    }

    private void loadPollData() {
        Firebase pollsRef = mFirebase.child("polls");
        Query queryRef = pollsRef.orderByChild("postID").equalTo(post.getKey());
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if (!post.getKey().equals((map.get("postID")))) {
                    return;
                }

                pollContainer.setVisibility(View.VISIBLE);
                Map<String, Boolean> votes = (Map<String, Boolean>) map.get("votes");
                pollTitle.setText((String) map.get("title"));
                int yes = 0;
                int no = 0;
                if (votes != null) {
                    Iterator iterator = votes.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry pair = (Map.Entry) iterator.next();
                        if ((Boolean) pair.getValue()) {
                            yes++;
                        } else {
                            no++;
                        }
                        iterator.remove();
                    }
                }

                final int total = yes + no;
                int yesP = (int) (((double) yes / total) * 100);
                int noP = (int) (((double) no / total) * 100);
                pollOptionOne.setText("Yes - " + yes + " (" + yesP + "%)");
                pollOptionTwo.setText("No - " + no + " (" + noP + "%)");
                pollOptionOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            pollOptionTwo.setChecked(false);
                            mFirebase.child("polls").child(dataSnapshot.getKey()).child("votes").child(mFirebase.getAuth().getUid()).setValue(true);
                            loadPollData();
                        }
                        if (pollOptionTwo.isChecked() == false && isChecked == false) {
                            mFirebase.child("polls").child(dataSnapshot.getKey()).child("votes").child(mFirebase.getAuth().getUid()).removeValue();
                            loadPollData();
                        }
                    }
                });

                pollOptionTwo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            pollOptionOne.setChecked(false);
                            mFirebase.child("polls").child(dataSnapshot.getKey()).child("votes").child(mFirebase.getAuth().getUid()).setValue(false);
                            loadPollData();
                        }
                        if (pollOptionOne.isChecked() == false && isChecked == false) {
                            mFirebase.child("polls").child(dataSnapshot.getKey()).child("votes").child(mFirebase.getAuth().getUid()).removeValue();
                            loadPollData();
                        }
                    }
                });
                /*Poll poll = dataSnapshot.getValue(Poll.class);
                poll.setKey(dataSnapshot.getKey());
                pollContainer.setVisibility(View.VISIBLE);
                pollTitle.setText(poll.getTitle());
                int yes = poll.getNumYesVotes();
                int no = poll.getNumNoVotes();
                int total = yes + no;
                pollOptionOne.setText("Yes - " + yes + " (" + (int) yes/total + "%)");
                pollOptionTwo.setText("No - " + yes + " (" + (int) no/total + "%)");*/

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
    private void loadCommentData() {
        Query queryRef = mFirebase.child("comments").orderByChild("postID").equalTo(post.getKey());
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Comment c = dataSnapshot.getValue(Comment.class);
                c.setKey(dataSnapshot.getKey());
                commentAdapter.addComment(c);
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

    private void loadAuthorData() {
        Query queryRef = mFirebase.child("users").orderByKey().equalTo(post.getAuthor());
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                author = dataSnapshot.getValue(User.class);
                author.setKey(dataSnapshot.getKey());

                String name = author.getName();
                String profile_pic = author.getProfile_picture();

                postDetailAuthorName.setText(name);
                Picasso.with(getActivity().getApplicationContext())
                        .load(profile_pic)
                        .transform(new CircleTransform())
                        .fit()
                        .into(postDetailAuthorPic);
                Date d = new Date(post.getTime_uploaded() * 1000L);
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
                String date = sdf.format(d);

                postDetailAuthorName.setText(name);
                postDetailAuthorDate.setText(date);
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

    private void toggleCommentsVisibility() {
        boolean visible = postDetailCommentSection.getVisibility() == View.VISIBLE;
        if(visible) {
            postDetailCommentSection.setVisibility(View.GONE);
            loadCommentData();
        } else {
            postDetailCommentSection.setVisibility(View.VISIBLE);
        }
    }
}

interface PostListener {
    void setPost(Post post);
}
