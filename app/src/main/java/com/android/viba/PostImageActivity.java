package com.android.viba;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PostImageActivity extends AppCompatActivity {

    private String postImage;
    private PhotoView photoView;
    ProgressBar progressBar;
    ImageView imageView;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_image);

        Intent intent = getIntent();
        postImage = intent.getStringExtra("homeimage");
        photoView = findViewById(R.id.homepost_photoview);
        progressBar = findViewById(R.id.progress_homeimage);
        imageView = findViewById(R.id.down_homeimage);

        try {
            Picasso.get().load(postImage).placeholder(R.drawable.profilepic)
                    .into(photoView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, "Error Occurred!", Toast.LENGTH_SHORT).show();
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
                String time = new SimpleDateFormat("yyyymmddhhmmss", Locale.getDefault())
                        .format(System.currentTimeMillis());
                File path = Environment.getExternalStorageDirectory();
                File dir = new File(path + "/Viba");
                dir.mkdir();
                String imageName = time + ".png";
                File file = new File(dir, imageName);
                OutputStream outputStream;

                try {
                    outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(PostImageActivity.this, "Image downloaded!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(PostImageActivity.this, "Can't download! Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
