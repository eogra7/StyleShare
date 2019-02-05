package com.evanogra.styleshare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.readystatesoftware.simpl3r.Uploader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class SetupProfileActivity extends AppCompatActivity {
    private final Firebase mFirebase = new Firebase("https://resplendent-inferno-4210.firebaseio.com/");
    private Bitmap profilePic;

    private final String TAG = "SetupProfile";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);
        ImageView profilePicUpload = (ImageView) findViewById(R.id.profile_pic_upload);
        FrameLayout viewContainer = (FrameLayout) findViewById(R.id.container_upload_profile_pic);

        viewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImages();
            }
        });

        Button submitProfileButton = (Button) findViewById(R.id.setup_profile_submit);
        submitProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText inputNameBox = (EditText) findViewById(R.id.input_name);
                String name = String.valueOf(inputNameBox.getText());
                EditText inputChapterBox = (EditText) findViewById(R.id.input_chapter);
                String chapter = String.valueOf(inputChapterBox.getText());

                AuthData mAuthData = mFirebase.getAuth();
                mFirebase.child("users").child(mAuthData.getUid()).child("name").setValue(name);
                mFirebase.child("users").child(mAuthData.getUid()).child("chapter").setValue(chapter);
                //mFirebase.child("users").child(mAuthData.getUid()).child("setup").setValue(true);
                try {
                    uploadProfilePic();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void getImages() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1) {
            if (data!=null) {
                Uri imageUri = data.getData();
                Log.d("hi", imageUri.toString());
                FrameLayout viewContainer = (FrameLayout) findViewById(R.id.container_upload_profile_pic);
                viewContainer.removeAllViews();
                ImageView picView = new ImageView(getApplicationContext());
                try {
                    InputStream is = getContentResolver().openInputStream(imageUri);
                    Bitmap pic = BitmapFactory.decodeStream(is);
                    Bitmap croppedPic = Bitmap.createBitmap(pic.getWidth(), pic.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas c = new Canvas(croppedPic);
                    Paint paint = new Paint();
                    Rect rect = new Rect(0, 0, pic.getWidth(), pic.getHeight());
                    paint.setAntiAlias(true);
                    c.drawARGB(0, 0, 0, 0);
                    int color = Color.argb(0xff, 0x42, 0x42, 0x42);
                    paint.setColor(color);
                    c.drawCircle(pic.getWidth() / 2, pic.getHeight() / 2, pic.getWidth() / 2, paint);
                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                    c.drawBitmap(pic, rect, rect, paint);
                    picView.setImageBitmap(pic);
                    viewContainer.addView(picView);
                    profilePic = pic;


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void uploadProfilePic() throws IOException {
        if(profilePic==null) {
            Log.e("SetupProfile", "profile pic null");
            return;
        }
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        profilePic.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String s3Key = UUID.nameUUIDFromBytes(imageBytes).toString() + ".jpg";
        File f = new File(path, s3Key);
        OutputStream outputStream = new FileOutputStream(f);
        byteArrayOutputStream.writeTo(outputStream);
        AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials("AKIAIDLUOXC2I4PDHWDQ", "SiFS5l6+KEr5BAh5lW0uBgp6+vxokOotlhIgxGY0"));

        final Uploader uploader = new Uploader(getApplicationContext(), s3Client, "fblastyleshareuploads", s3Key, f);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String urlLocation = uploader.start();
                Log.d(TAG, urlLocation);
                AuthData authData = mFirebase.getAuth();
                Firebase mProfilePicRef = mFirebase.child("users").child(authData.getUid()).child("profile_picture");
                mProfilePicRef.setValue(urlLocation);
                mFirebase.child("users").child(mFirebase.getAuth().getUid()).child("setup").setValue(true);
                finish();
            }
        });

        thread.start();


    }
}
