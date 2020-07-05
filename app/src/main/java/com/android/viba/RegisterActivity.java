package com.android.viba;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText emailReg, passwordReg, userReg;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private SignInButton signInButton;
    GoogleSignInClient mgoogleSignInClient;
    private static final String TAg = "RegisterActivity";
    private FirebaseAuth mAuth;
    int RC_SIGn_In = 0;
    private DatabaseReference databaseReference;
    private CheckConnectionActivity checkConnection;
    private SlidrInterface slidrInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Admins");
        progressDialog = new ProgressDialog(this);

        emailReg = findViewById(R.id.emailReg);
        passwordReg = findViewById(R.id.passReg);
        userReg = findViewById(R.id.userReg);

        slidrInterface = Slidr.attach(this);

        signInButton = findViewById(R.id.signinbtn);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        mgoogleSignInClient = GoogleSignIn.getClient(this,gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private  void signIn(){
        Intent SignInIntent = mgoogleSignInClient.getSignInIntent();
        startActivityForResult(SignInIntent,RC_SIGn_In);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGn_In)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount acc = task.getResult(ApiException.class);
                FireBaseGoogleOuth(acc);

            }catch (ApiException e){
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
//            FireBaseGoogleOuth(null);
            }
        }
    }

    private void FireBaseGoogleOuth(final GoogleSignInAccount account){

        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    Intent i = new Intent(RegisterActivity.this, HomeActivity.class);
                    i.putExtra("name", account.getDisplayName());
                    i.putExtra("imgUrl", account.getPhotoUrl().toString());
                    startActivity(i);
                }else {
                    Toast.makeText(RegisterActivity.this, "Failed Login", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void regtolog(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }

    public void register(View view) {

        checkConnection = new CheckConnectionActivity(RegisterActivity.this);
        checkConnection.checkConnection(view);

        progressDialog.setTitle("Validating");
        progressDialog.setMessage("Please wait...");

        final String username = userReg.getText().toString();
        final String email = emailReg.getText().toString();
        final String password = passwordReg.getText().toString();

        if (username.isEmpty()) {
            Snackbar.make(view, "Enter your name", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } else if (email.isEmpty()) {
            Snackbar.make(view, "Enter your email", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } else if (password.isEmpty()) {
            Snackbar.make(view, "Create your password", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } else if (password.length() < 6) {
            Toast.makeText(RegisterActivity.this, "Your Password must contain at least 6 characters!",
                    Toast.LENGTH_LONG).show();
        } else {
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                HashMap<String, Object> regmap = new HashMap<>();
                                regmap.put("adname", username);
                                regmap.put("adrole", " ");
                                regmap.put("adctry", "!");
                                regmap.put("adstate", "!");
                                regmap.put("adcity", "!");
                                regmap.put("adclg", "!");
                                regmap.put("adimage", "!");
                                regmap.put("ademail", email);
                                regmap.put("adpass", password);
                                regmap.put("adphone", "!");
                                regmap.put("aduid", firebaseAuth.getCurrentUser().getUid());

                                FirebaseDatabase.getInstance().getReference(databaseReference.getKey())
                                        .child(firebaseAuth.getCurrentUser().getUid())
                                        .setValue(regmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Hi " + username + "... Welcome to Viba!",
                                                Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                        finish();
                                    }
                                });

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Authentication failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
