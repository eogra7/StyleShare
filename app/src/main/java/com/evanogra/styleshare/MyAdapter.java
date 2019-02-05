package com.evanogra.styleshare;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Evan on 2/11/2016.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private static final String TAG = "HomeTabAdapter";
    private List<Post> posts;
    private Context context;
    private TabActivity activity;
    private FragmentManager fragmentManager;
    private Firebase mFirebase;

    public void setActivity(TabActivity activity) {
        this.activity = activity;
    }

    public MyAdapter(List<Post> posts, Context context, FragmentManager fragmentManager) {
        this.posts = posts;
        this.context = context;
        this.fragmentManager = fragmentManager;
        mFirebase = new Firebase("https://resplendent-inferno-4210.firebaseio.com/");
    }

    public MyAdapter() {
    }

    public MyAdapter(List<Post> posts) {
        this.posts = posts;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    public void addPost(Post post) {
        for(Post p : posts) {
            if(p.getKey().equals(post.getKey()))
            {
                return;
            }
        }
        posts.add(post);
        notifyDataSetChanged();
    }

    public void removePost(Post post) {
        posts.remove(post);
        notifyDataSetChanged();
    }

    public void removeAllPosts() {
        posts = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void removePost(int i) {
        posts.remove(i);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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

        public ViewHolder(View itemView, TextView postDetailAuthorName, TextView postDetailAuthorDate, ImageView postDetailAuthorPic, ImageView postDetailImage, TextView postDetailBody, ImageView postDetailLikeButton, ImageView postDetailCommentButton, RecyclerView postDetailCommentsRecycler, TextView postDetailCommentText, Button postDetailCommentSubmitButton, LinearLayout postDetailCommentSection, LinearLayout postDetailAuthorContainer, AppCompatCheckBox pollOptionOne, AppCompatCheckBox pollOptionTwo, TextView pollTitle, LinearLayout pollContainer) {
            super(itemView);
            this.postDetailAuthorName = postDetailAuthorName;
            this.postDetailAuthorDate = postDetailAuthorDate;
            this.postDetailAuthorPic = postDetailAuthorPic;
            this.postDetailImage = postDetailImage;
            this.postDetailBody = postDetailBody;
            this.postDetailLikeButton = postDetailLikeButton;
            this.postDetailCommentButton = postDetailCommentButton;
            this.postDetailCommentsRecycler = postDetailCommentsRecycler;
            this.postDetailCommentText = postDetailCommentText;
            this.postDetailCommentSubmitButton = postDetailCommentSubmitButton;
            this.postDetailCommentSection = postDetailCommentSection;
            this.postDetailAuthorContainer = postDetailAuthorContainer;
            this.pollOptionOne = pollOptionOne;
            this.pollOptionTwo = pollOptionTwo;
            this.pollTitle = pollTitle;
            this.pollContainer = pollContainer;
        }
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_post_detail, parent, false);
        LinearLayout postDetailAuthorContainer = (LinearLayout) v.findViewById(R.id.post_detail_author_container);
        TextView postDetailAuthorName = (TextView) v.findViewById(R.id.post_detail_author_name);
        TextView postDetailAuthorDate = (TextView) v.findViewById(R.id.post_detail_author_date);
        ImageView postDetailAuthorPic = (ImageView) v.findViewById(R.id.post_detail_author_pic);
        ImageView postDetailImage = (ImageView) v.findViewById(R.id.post_detail_image);
        TextView postDetailBody = (TextView) v.findViewById(R.id.post_detail_body);
        ImageView postDetailLikeButton = (ImageView) v.findViewById(R.id.post_detail_like_button);
        ImageView postDetailCommentButton = (ImageView) v.findViewById(R.id.post_detail_comment_button);
        RecyclerView postDetailCommentsRecycler = (RecyclerView) v.findViewById(R.id.post_detail_comments_recycler);
        TextView postDetailCommentText = (TextView) v.findViewById(R.id.post_detail_comments_text);
        Button postDetailCommentSubmitButton = (Button) v.findViewById(R.id.post_detail_comment_submit);
        LinearLayout postDetailCommentSection = (LinearLayout) v.findViewById(R.id.post_detail_comments_section);
        AppCompatCheckBox pollOptionOne = (AppCompatCheckBox) v.findViewById(R.id.poll_option_one);
        AppCompatCheckBox pollOptionTwo = (AppCompatCheckBox) v.findViewById(R.id.poll_option_two);
        TextView pollTitle = (TextView) v.findViewById(R.id.poll_title);
        LinearLayout pollContainer = (LinearLayout) v.findViewById(R.id.poll_container);
        ViewHolder vh = new ViewHolder(v, postDetailAuthorName, postDetailAuthorDate, postDetailAuthorPic, postDetailImage, postDetailBody, postDetailLikeButton, postDetailCommentButton, postDetailCommentsRecycler, postDetailCommentText, postDetailCommentSubmitButton, postDetailCommentSection, postDetailAuthorContainer, pollOptionOne, pollOptionTwo, pollTitle, pollContainer);

        return vh;
    }

    public interface OnUserSelectedListener {
        void onUserSelected(User user);
    }

    @Override
    public void onBindViewHolder(final MyAdapter.ViewHolder holder, int position) {
        final Post post = posts.get(position);
        final CommentAdapter commentAdapter = new CommentAdapter();
        commentAdapter.setContext(activity);
        holder.pollContainer.setVisibility(View.GONE);

        loadAuthorData(holder, post);
        loadPostData(holder, post);
        loadPollData(holder, post);
        setCommentsVisibility(holder, View.GONE);
        holder.postDetailCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView.LayoutManager layoutManager = new org.solovyev.android.views.llm.LinearLayoutManager(activity);
                commentAdapter.setContext(activity);
                holder.postDetailCommentsRecycler.setLayoutManager(layoutManager);
                holder.postDetailCommentsRecycler.setAdapter(commentAdapter);
                toggleCommentsVisibility(holder, post, commentAdapter);
            }
        });

        holder.postDetailCommentSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = holder.postDetailCommentText.getText().toString();
                if (body.isEmpty()) {
                    Toast toast = Toast.makeText(activity, "Comment cannot be blank!", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                Comment comment = new Comment();
                AuthData authData = mFirebase.getAuth();
                comment.setTime(System.currentTimeMillis() / 1000L);
                comment.setAuthor(authData.getUid());
                comment.setBody(body);
                comment.setPostID(post.getKey());
                Firebase mRef = mFirebase.child("comments").push();
                mRef.setValue(comment);

                holder.postDetailCommentText.setText("");
                loadCommentData(post, commentAdapter);
            }
        });
    }

    private void loadPollData(final ViewHolder holder, final Post post) {
        Firebase pollsRef = mFirebase.child("polls");
        Query queryRef = pollsRef.orderByChild("postID").equalTo(post.getKey());
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if(!post.getKey().equals((map.get("postID")))) {
                    return;
                }

                holder.pollContainer.setVisibility(View.VISIBLE);
                Map<String, Boolean> votes = (Map<String, Boolean>) map.get("votes");
                holder.pollTitle.setText((String) map.get("title"));
                int yes = 0;
                int no = 0;
                if(votes!=null) {
                    Iterator iterator = votes.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry pair = (Map.Entry) iterator.next();
                        if((Boolean) pair.getValue()) {
                            yes++;
                        } else {
                            no++;
                        }
                        iterator.remove();
                    }
                }

                final int total = yes + no;
                int yesP = (int)(((double) yes/total)*100);
                int noP = (int)(((double) no/total)*100);
                holder.pollOptionOne.setText("Yes - " + yes + " (" + yesP + "%)");
                holder.pollOptionTwo.setText("No - " + no + " (" + noP + "%)");
                holder.pollOptionOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            holder.pollOptionTwo.setChecked(false);
                            mFirebase.child("polls").child(dataSnapshot.getKey()).child("votes").child(mFirebase.getAuth().getUid()).setValue(true);
                            loadPollData(holder, post);
                        }
                        if (holder.pollOptionTwo.isChecked() == false && isChecked == false) {
                            mFirebase.child("polls").child(dataSnapshot.getKey()).child("votes").child(mFirebase.getAuth().getUid()).removeValue();
                            loadPollData(holder, post);
                        }
                    }
                });

                holder.pollOptionTwo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            holder.pollOptionOne.setChecked(false);
                            mFirebase.child("polls").child(dataSnapshot.getKey()).child("votes").child(mFirebase.getAuth().getUid()).setValue(false);
                            loadPollData(holder, post);
                        }
                        if (holder.pollOptionOne.isChecked() == false && isChecked == false) {
                            mFirebase.child("polls").child(dataSnapshot.getKey()).child("votes").child(mFirebase.getAuth().getUid()).removeValue();
                            loadPollData(holder, post);
                        }
                    }
                });
                /*Poll poll = dataSnapshot.getValue(Poll.class);
                poll.setKey(dataSnapshot.getKey());
                holder.pollContainer.setVisibility(View.VISIBLE);
                holder.pollTitle.setText(poll.getTitle());
                int yes = poll.getNumYesVotes();
                int no = poll.getNumNoVotes();
                int total = yes + no;
                holder.pollOptionOne.setText("Yes - " + yes + " (" + (int) yes/total + "%)");
                holder.pollOptionTwo.setText("No - " + yes + " (" + (int) no/total + "%)");*/

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

    private void loadPostData(final ViewHolder vh, Post post) {
        String body = post.getBody();
        String imageUrl = post.getImage_url();
        vh.postDetailBody.setText(body);
        Picasso.with(activity)
                .load(imageUrl)
                .transform(new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap source) {
                        int mSize = vh.postDetailImage.getWidth();
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
                        //.fit()
                        //.centerCrop()
                .into(vh.postDetailImage);

    }


    private void loadCommentData(Post post, final CommentAdapter commentAdapter) {
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

    private void loadAuthorData(final ViewHolder vh, final Post post) {
        Query queryRef = mFirebase.child("users").orderByKey().equalTo(post.getAuthor());
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final User author = dataSnapshot.getValue(User.class);
                author.setKey(dataSnapshot.getKey());

                String name = author.getName();
                String profile_pic = author.getProfile_picture();

                vh.postDetailAuthorName.setText(name);
                Picasso.with(context)
                        .load(profile_pic)
                        .transform(new CircleTransform())
                        .fit()
                        .into(vh.postDetailAuthorPic);
                Date d = new Date(post.getTime_uploaded() * 1000L);
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
                String date = sdf.format(d);

                vh.postDetailAuthorName.setText(name);
                vh.postDetailAuthorDate.setText(date);

                vh.postDetailAuthorContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.setSelectedUser(author);
                        activity.onUserSelected(author);
                    }
                });
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

    private void toggleCommentsVisibility(ViewHolder vh, Post post, CommentAdapter commentAdapter) {
        boolean visible = vh.postDetailCommentSection.getVisibility() == View.VISIBLE;
        if(visible) {
            vh.postDetailCommentSection.setVisibility(View.GONE);
        } else {
            vh.postDetailCommentSection.setVisibility(View.VISIBLE);
            loadCommentData(post, commentAdapter);

        }
    }

    private void setCommentsVisibility(ViewHolder vh, int visibility) {
        vh.postDetailCommentSection.setVisibility(visibility);
    }
    @Override
    public int getItemCount() {
        return posts.size();
    }




}
