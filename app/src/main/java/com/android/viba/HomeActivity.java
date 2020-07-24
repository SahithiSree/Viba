package com.android.viba;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference, postReference;
    private CircleImageView circleImageView;
    private TextView adNavname, adNavrole, showText;
    private String profileimage, adname, adrole, cuurentLoaction;
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ArrayList<GetSet> getSets;
    private HomepostAdapter homepostAdapter;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ProgressBar progressBar;
    //private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        progressBar = findViewById(R.id.progress_home);
        showText = findViewById(R.id.show_nothingHome);
       // searchView = findViewById(R.id.searchHome);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Admins")
                .child(firebaseAuth.getCurrentUser().getUid());
        postReference = FirebaseDatabase.getInstance().getReference().child("Homepost Info");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressBar.setVisibility(View.VISIBLE);
                    adNavname = findViewById(R.id.home_userName);
                    adNavrole = findViewById(R.id.home_teamName);
                    circleImageView = findViewById(R.id.home_profileImage);

                    adname = dataSnapshot.child("adname").getValue().toString();
                    adrole = dataSnapshot.child("adrole").getValue().toString();
                    profileimage = dataSnapshot.child("adimage").getValue().toString();
                    Picasso.get().load(profileimage).placeholder(R.drawable.profilepic).into(circleImageView);
                    adNavname.setText(adname);
                    adNavrole.setText(adrole);
                    progressBar.setVisibility(View.GONE);

                    circleImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(HomeActivity.this, GetSet.class));
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_events, R.id.nav_startups, R.id.nav_profile, R.id.nav_settings, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        fab = findViewById(R.id.home_fab);
        refreshLayout = findViewById(R.id.refreshhome);
        recyclerView = findViewById(R.id.homeRecycler);

        recyclerView = findViewById(R.id.homeRecycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setNestedScrollingEnabled(false);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, HomepostActivity.class));
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //checkConnection.checkConnection(refreshLayout);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                    }
                }, 1500);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(null);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.action_about:
                startActivity(new Intent(HomeActivity.this, AboutActivity.class));
                break;
            case R.id.action_contact:
                startActivity(new Intent(HomeActivity.this, ContactActivity.class));

                break;
            case R.id.action_help:
                startActivity(new Intent(HomeActivity.this, HelpActivity.class));
                break;
            case R.id.action_join:
                startActivity(new Intent(HomeActivity.this, JoinUsActivity.class));
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_events:
                startActivity(new Intent(HomeActivity.this, EventsActivity.class));
                break;
            case R.id.nav_startups:
                startActivity(new Intent(HomeActivity.this, StartupsActivity.class));
                break;
            case R.id.nav_profile:
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                break;
            case R.id.nav_logout:
                firebaseAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (postReference != null) {
            progressBar.setVisibility(View.VISIBLE);
            postReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    getSets = new ArrayList<GetSet>();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        GetSet h = dataSnapshot1.getValue(GetSet.class);
                        getSets.add(h);
                    }
                    homepostAdapter = new HomepostAdapter(HomeActivity.this, getSets);
                    recyclerView.setAdapter(homepostAdapter);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
        else
            showText.setVisibility(View.VISIBLE);
//        if (searchView != null) {
//            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                @Override
//                public boolean onQueryTextSubmit(String query) {
//                    return false;
//                }
//
//                @Override
//                public boolean onQueryTextChange(String newText) {
//                    //search(newText);
//                    return true;
//                }
//            });
//        }
    }

//    private void search(final String str) {
//        try {
//            databaseReference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    cuurentLoaction = dataSnapshot.child("adcity").getValue().toString();
//                    ArrayList<GetSet> list = new ArrayList<>();
//                    for (GetSet object : getSets) {
//                        if ((object.getAdname().toLowerCase()).contains(str.toLowerCase())) {
//                            if (object.getAdctry().toLowerCase().equals(cuurentLoaction)) {
//                                list.add(object);
//                            }
//                        }
//                    }
//                    HomepostAdapter adapter = new HomepostAdapter(list);
//                    recyclerView.setAdapter(adapter);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });

//            ArrayList<GetSet> list = new ArrayList<>();
//            for (GetSet object : getSets) {
//                if ((object.getAdname().toLowerCase()).contains(str.toLowerCase())) {
//                    if (object.getAdctry().toLowerCase().equals(cuurentLoaction)) {
//                        list.add(object);
//                    }
//                }
//            }
//            HomepostAdapter adapter = new HomepostAdapter(list);
//            recyclerView.setAdapter(adapter);
//        } catch (Exception e) {
//            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
//    }
}
