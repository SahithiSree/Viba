package com.android.viba;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private EditText setName, setEmail, setCity, setRole, setCountry, setState, setClg, setPhone;
    private String editName, editEmail, editCity, editClg, editRole, editCountry, editState, editPhone;
    private CircleImageView circleImageView;
    private String currentId;
    private Uri imageUri;
    private FloatingActionButton editInfo, editImage;
    private ProgressBar progressBar;
    ProgressDialog progressDialog;
    private SlidrInterface slidrInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        slidrInterface = Slidr.attach(this);

        getSupportActionBar().setTitle("My Profile");

        firebaseAuth = FirebaseAuth.getInstance();
        currentId = firebaseAuth.getCurrentUser().getUid();
        editInfo = findViewById(R.id.setFloat);
        editImage = findViewById(R.id.uploadImage);
        progressBar = findViewById(R.id.progress_profile);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Admins").child(currentId);
        storageReference = FirebaseStorage.getInstance().getReference("Profile Images").child("Admins").child(currentId + ".jpg");

        setName = findViewById(R.id.setname);
        setRole = findViewById(R.id.setrol);
        setCity = findViewById(R.id.setcity);
        setClg = findViewById(R.id.setcollege);
        setEmail = findViewById(R.id.setemail);
        setCountry = findViewById(R.id.setcountry);
        setState = findViewById(R.id.setstate);
        setPhone = findViewById(R.id.setphone);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                progressBar.setVisibility(View.VISIBLE);

                circleImageView = findViewById(R.id.set_image);

                String setNamestr = dataSnapshot.child("adname").getValue().toString();
                String setRolestr = dataSnapshot.child("adrole").getValue().toString();
                String setCountrystr = dataSnapshot.child("adctry").getValue().toString();
                String setStatestr = dataSnapshot.child("adstate").getValue().toString();
                String setCitystr = dataSnapshot.child("adcity").getValue().toString();
                String setClgstr = dataSnapshot.child("adclg").getValue().toString();
                String setEmailstr = dataSnapshot.child("ademail").getValue().toString();
                String setPhonestr = dataSnapshot.child("adphone").getValue().toString();
                String setImagestr = dataSnapshot.child("adimage").getValue().toString();

                setName.setText(setNamestr);
                setEmail.setText(setEmailstr);
                setCountry.setText(setCountrystr);
                setState.setText(setStatestr);
                setCity.setText(setCitystr);
                setClg.setText(setClgstr);
                setRole.setText(setRolestr);
                setPhone.setText(setPhonestr);

                Picasso.get().load(setImagestr).placeholder(R.drawable.profilepic).into(circleImageView);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editName = setName.getText().toString();
                editEmail = setEmail.getText().toString().trim();
                editRole = setRole.getText().toString();
                editCountry = setCountry.getText().toString();
                editState = setState.getText().toString();
                editCity = setCity.getText().toString();
                editClg = setClg.getText().toString();
                editPhone = setPhone.getText().toString().trim();

                if (TextUtils.isEmpty(editName)) {
                    Snackbar.make(v, "Name is required!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();                }
                else if (TextUtils.isEmpty(editEmail)) {
                    Snackbar.make(v, "Email is required!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();                }
                else if (TextUtils.isEmpty(editCountry)) {
                    Snackbar.make(v, "Put ! in Country field", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();                }
                else if (TextUtils.isEmpty(editState)) {
                    Snackbar.make(v, "Put ! in State field", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();                }
                else if (TextUtils.isEmpty(editCity)) {
                    Snackbar.make(v, "Put ! in City field", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();                }
                else if (TextUtils.isEmpty(editClg)) {
                    Snackbar.make(v, "Put ! in College field", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();                }
                else if (TextUtils.isEmpty(editPhone)) {
                    Snackbar.make(v, "Put ! in Phone field", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();                }
                else {
                    updateProfile();
                }
            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(ProfileActivity.this);
            }
        });
    }

    private void updateProfile() {

        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setTitle("Updating your profile");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

                    if (TextUtils.isEmpty(editRole)) {
                        editRole = " ";
                    }

                    try {
                        HashMap<String, Object> updMap = new HashMap<>();
                        updMap.put("adname", editName);
                        updMap.put("adrole", editRole);
                        updMap.put("adctry", editCountry);
                        updMap.put("adstate", editState);
                        updMap.put("adcity", editCity);
                        updMap.put("adclg", editClg);
                        updMap.put("ademail", editEmail);
                        updMap.put("adphone", editPhone);
                        databaseReference.updateChildren(updMap);
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Profile is updated!", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                progressBar.setVisibility(View.VISIBLE);
                final Uri resultUri = result.getUri();

                final StorageReference filePath = storageReference;

                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                databaseReference.child("adimage").setValue(String.valueOf(uri));
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(ProfileActivity.this, "Profile image updated!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }
    }
}
