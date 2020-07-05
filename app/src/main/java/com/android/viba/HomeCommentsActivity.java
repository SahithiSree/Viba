package com.android.viba;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zolad.zoominimageview.ZoomInImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeCommentsActivity extends AppCompatActivity {

    TextView userName, userDescp, comments;
    EditText shareCom;
    String comuid;
    DatabaseReference databaseReference, userReference;
    CircleImageView circleImageView, userImage;
    ImageView imageView;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    ArrayList<GetSet> homeComs;
    HomeComAdapter homeComAdapter;
    RecyclerView recyclerView;
    String currentId, username, userProfileimage;
    String date, time, disCom;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_comments);

        firebaseAuth = FirebaseAuth.getInstance();
        currentId = firebaseAuth.getCurrentUser().getUid();
        Intent intent = getIntent();
        comuid = intent.getStringExtra("homeuid");

        databaseReference = FirebaseDatabase.getInstance().getReference("Homepost Info").child(comuid);
        userReference = FirebaseDatabase.getInstance().getReference().child("Admins").child(currentId);

        recyclerView = findViewById(R.id.homecomRecycler);
        floatingActionButton = findViewById(R.id.homecom_fab);
        shareCom = findViewById(R.id.ed_homeshareCom);
        userName = findViewById(R.id.homeComAdname);
        userDescp = findViewById(R.id.homeComDescp);
        userImage = findViewById(R.id.homecomments_pro);
        comments = findViewById(R.id.txt_commentsfromHome);
        circleImageView = findViewById(R.id.homeComProImage);
        imageView = findViewById(R.id.homeComPostimage);
        progressBar = findViewById(R.id.progress_homeCom);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setNestedScrollingEnabled(false);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTime();
                storeComInfo();
            }
        });

        homeComs = new ArrayList<GetSet>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userproImage = dataSnapshot.child("adimage").getValue().toString();
                final String userpostImage = dataSnapshot.child("homepi").getValue().toString();
                String userDescr = dataSnapshot.child("homepd").getValue().toString();
                String userNamestr = dataSnapshot.child("adname").getValue().toString();
                String userUid = dataSnapshot.child("aduid").getValue().toString();

                userDescp.setText(userDescr);
                userName.setText(userNamestr);
                Picasso.get().load(userproImage).placeholder(R.drawable.profilepic).into(circleImageView);

                (Picasso.get().load(userpostImage))
                        .into(imageView, new com.squareup.picasso.Callback() {
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

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), PostImageActivity.class).putExtra("homeimage", userpostImage));
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(HomeCommentsActivity.this, "Error occurred! Try again", Toast.LENGTH_SHORT).show();
            }
        });

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userproImage = dataSnapshot.child("adimage").getValue().toString();
                Picasso.get().load(userproImage).placeholder(R.drawable.profilepic).into(userImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
        recyler();
    }

    private void dateTime() {
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(" dd-mm-yyyy ");
        date = dateFormat.getDateInstance().format(calendar.getTime());

        Calendar calendarTime = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat(" hh:mm:ss:ms ");
        time = simpleDateFormatTime.format(calendarTime.getTime());
    }

    private void storeComInfo() {
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    disCom = shareCom.getText().toString();
                    username = dataSnapshot.child("adname").getValue().toString();
                    userProfileimage = dataSnapshot.child("adimage").getValue().toString();

                    HashMap<String, Object> postMap = new HashMap<>();
                    postMap.put("userimage", userProfileimage);
                    postMap.put("username", username);
                    postMap.put("comuid", currentId);
                    postMap.put("comment", disCom);
                    postMap.put("date", date);

                    databaseReference.child("Comments").child(time +date +currentId).updateChildren(postMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        //Toast.makeText(HomepostClickActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(HomeCommentsActivity.this, HomeCommentsActivity.class).putExtra("homeuid", comuid));
                                        overridePendingTransition(0, 0);
                                        finish();
                                    } else {
                                        Toast.makeText(HomeCommentsActivity.this, "Can't comment! Try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeCommentsActivity.this, "Error occurred! Try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void recyler() {
        databaseReference.child("Comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.setVisibility(View.GONE);
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    GetSet getSet = dataSnapshot1.getValue(GetSet.class);
                    homeComs.add(getSet);
                }
                homeComAdapter = new HomeComAdapter(HomeCommentsActivity.this, homeComs);
                recyclerView.setAdapter(homeComAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(HomeCommentsActivity.this, HomeActivity.class));
        finish();
        super.onBackPressed();
    }
}
