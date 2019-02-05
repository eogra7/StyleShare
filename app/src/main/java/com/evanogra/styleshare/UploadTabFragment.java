package com.evanogra.styleshare;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.readystatesoftware.simpl3r.Uploader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;


public class UploadTabFragment extends Fragment {
    private final int GET_IMAGE = 1;
    private LinearLayout imageContainer;
    private Context context;
    private final Firebase mFirebase = new Firebase("https://resplendent-inferno-4210.firebaseio.com/");
    private Bitmap image;
    private Button submitButton;
    private Button pollButton;
    private TextView pollTitleView;
    private ImageView mImageView;


    public UploadTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_upload_tab2, container, false);
        image = null;
        context = getActivity().getApplicationContext();
        imageContainer = (LinearLayout) v.findViewById(R.id.container_upload_photo);


        //FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(imageContainer.getWidth(), imageContainer.getWidth());
        //imageContainer.setLayoutParams(lp);

        imageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImages();
            }
        });
        mImageView = (ImageView) v.findViewById(R.id.upload_image);
        submitButton = (Button) v.findViewById(R.id.submit_post_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSubmit();
            }
        });

        pollButton = (Button) v.findViewById(R.id.poll_button);
        pollTitleView = (TextView) v.findViewById(R.id.poll_title);
        pollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pollTitleView.getVisibility() == View.GONE) {
                    pollTitleView.setVisibility(View.VISIBLE);
                    pollButton.setText("REMOVE POLL");
                } else {
                    pollTitleView.setVisibility(View.GONE);
                    pollButton.setText("ADD POLL");
                }

            }
        });

        Picasso.with(context)
                .load("http://s3.amazonaws.com/static.graphemica.com/glyphs/i500s/000/010/184/original/002B-500x500.png?1275328183")
                .fit()
                .centerInside()
                .into(mImageView);

        return v;
    }

    private int doSubmit() {
        if(image==null) {
            return -1;
        }
        EditText bodyEditText = (EditText) getView().findViewById(R.id.input_description);

        final String title = "";
        final String body = String.valueOf(bodyEditText.getText());
        //final String id = String.valueOf(UUID.randomUUID());

        final Firebase mPostRef = mFirebase.child("posts").push();
        final AuthData mAuthData = mFirebase.getAuth();
        final String author = mAuthData.getUid();
        final String pollTitle = pollTitleView.getText().toString();

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        final byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String s3Key = UUID.nameUUIDFromBytes(imageBytes).toString() + ".jpg";
        File f = new File(path, s3Key);
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            byteArrayOutputStream.writeTo(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials("AKIAIDLUOXC2I4PDHWDQ", "SiFS5l6+KEr5BAh5lW0uBgp6+vxokOotlhIgxGY0"));
        final Uploader uploader = new Uploader(context, s3Client, "fblastyleshareuploads", s3Key, f);

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String imageUrl = uploader.start();
                long time = System.currentTimeMillis() / 1000L;
                Post post = new Post(author, title, body, imageUrl, time);
                mPostRef.setValue(post);

                if(!pollTitle.isEmpty()) {
                    Poll poll = new Poll();
                    poll.setPostID(mPostRef.getKey());
                    poll.setTitle(pollTitle);
                    mFirebase.child("polls").push().setValue(poll);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast toast = Toast.makeText(context, "Post Submitted", Toast.LENGTH_LONG);
                toast.show();
                doAfterSubmit();
            }
        };
        task.execute();

        return 1;
    }

    private void doAfterSubmit() {

        TabActivity tabActivity = (TabActivity) getActivity();
        tabActivity.setPage(0);
    }


    private void getImages() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Complete action using"), GET_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GET_IMAGE && data!=null) {
            Uri imageUri = data.getData();
            Picasso.with(context)
                    .load(imageUri)
                    //.centerInside()
                    //.fit()
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            mImageView.setImageBitmap(bitmap);
                            mImageView.requestLayout();
                            setImage(bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });

            /*try {
                Resources r = getResources();
                InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                image = BitmapFactory.decodeStream(inputStream);
                MyImageView imageView = new MyImageView(context);
                imageView.setImageBitmap(image);
                imageContainer.removeAllViews();
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageContainer.addView(imageView);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }*/

        }
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }
}
