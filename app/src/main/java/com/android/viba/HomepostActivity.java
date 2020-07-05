package com.android.viba;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomepostActivity extends AppCompatActivity {

    private ImageView imageView;
    private EditText editText;
    private Button btnpost, btnchoose, btncleardescp;
    private ProgressDialog progressDialog;
    private final static int GalleryPick = 1;
    private Uri imageUri;
    private StorageReference storageReference, filepath;
    private DatabaseReference postReference, adReference;
    private String time, date;
    private CircleImageView circleImageView;
    private String downloadUrl = "!", description = " ";
    private String currentId;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepost);

        getSupportActionBar().setTitle("Share Bulletin");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        imageView = findViewById(R.id.adpostImage);
        circleImageView = findViewById(R.id.adproImage);
        btncleardescp =findViewById(R.id.clearHomeDescp);
        editText = findViewById(R.id.adeditPost);
        btnpost = findViewById(R.id.btnHomepost);
        btnchoose = findViewById(R.id.chooseHomeImage);
        currentId = firebaseAuth.getCurrentUser().getUid();

        postReference = FirebaseDatabase.getInstance().getReference("Homepost Info");
        adReference = FirebaseDatabase.getInstance().getReference().child("Admins");
        storageReference = FirebaseStorage.getInstance().getReference("Homepost Images");

        btncleardescp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
//        btnClearimage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imageUri = null;
//                imageView.setImageURI(imageUri);
//            }
//        });

        btnchoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent setImage = new Intent();
                setImage.setAction(Intent.ACTION_GET_CONTENT);
                setImage.setType("image/*");
                startActivityForResult(setImage, GalleryPick);
            }
        });

        btnpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                description = editText.getText().toString();

                if (imageUri == null && TextUtils.isEmpty(description)) {
                    Snackbar.make(v, "Post an image or write a post", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if (imageUri == null) {
                    dateTime();
                    storePostInfo();
                }
                else {
                    progressDialog.setTitle("Updating post");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.show();
                    dateTime();
                    storageImage();
                }
            }
        });

        adReference.child(currentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userImagestr = dataSnapshot.child("adimage").getValue().toString();
                Picasso.get().load(userImagestr).placeholder(R.drawable.profilepic).into(circleImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void dateTime() {
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(" dd-mm-yyyy ");
        date = dateFormat.getDateInstance().format(calendar.getTime());

        Calendar calendarTime = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat(" hh:mm:ss:ms ");
        time = simpleDateFormatTime.format(calendarTime.getTime());
    }
    private void storageImage() {

        filepath = storageReference.child(currentId +time +date +".jpg");

        filepath.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadUrl = String.valueOf(uri);
                                storePostInfo();
                            }
                        });
                    }
                });
    }

    private void storePostInfo() {
        adReference.child(currentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressDialog.show();

                    String username = dataSnapshot.child("adname").getValue().toString();
                    String userProfileimage = dataSnapshot.child("adimage").getValue().toString();
                    String userRole = dataSnapshot.child("adrole").getValue().toString();
                    String userCountry = dataSnapshot.child("adctry").getValue().toString();

                    HashMap<String, Object> postMap = new HashMap<>();
                    postMap.put("date", date);
                    postMap.put("time", time.substring(0, 6));
                    postMap.put("homepd", description);
                    postMap.put("homepi", downloadUrl);
                    postMap.put("adimage", userProfileimage);
                    postMap.put("adname", username);
                    postMap.put("adrole", userRole);
                    postMap.put("adctry", userCountry);
                    postMap.put("aduid", currentId);
                    postMap.put("homeuid", time +date +currentId);

                    postReference.child(time +date +currentId).updateChildren(postMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(HomepostActivity.this, HomeActivity.class));
                                        finish();
                                        Toast.makeText(HomepostActivity.this, "Post updated successfully!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(HomepostActivity.this, "Error occurred! Try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(HomepostActivity.this, "Error occurred! Try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        progressDialog.dismiss();
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null && data.getData() != null) {
            progressDialog.dismiss();
            imageUri = data.getData();
            Picasso.get().load(imageUri).resize(imageView.getMeasuredWidth(), imageView.getMeasuredHeight()).placeholder(R.drawable.profilepic).into(imageView);
        }
    }
}
