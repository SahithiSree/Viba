package com.android.viba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailLog, passwordLog;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private CheckConnectionActivity checkConnection;
    SlidrInterface slidrInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        emailLog = findViewById(R.id.emailLog);
        passwordLog = findViewById(R.id.passLog);

        slidrInterface = Slidr.attach(this);
    }

    public void logtoreg(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    public void login(View view) {

        checkConnection = new CheckConnectionActivity(LoginActivity.this);
        checkConnection.checkConnection(view);

        progressDialog.setTitle("Validating");
        progressDialog.setMessage("Please wait...");

        final String email = emailLog.getText().toString().trim();
        final String password = passwordLog.getText().toString();

         if (email.isEmpty()) {
             Snackbar.make(view, "Enter email", Snackbar.LENGTH_SHORT)
                     .setAction("Action", null).show();
        } else if (password.isEmpty()) {
             Snackbar.make(view, "Enter password", Snackbar.LENGTH_SHORT)
                     .setAction("Action", null).show();
        } else {
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Welcome back!",
                                                Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                        finish();
                                    }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Signin failed! Try again",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
