package com.android.viba;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class CheckConnectionActivity {
    Context context;

    CheckConnectionActivity() {

    }

    CheckConnectionActivity(Context c) {
        context = c;
    }

    public void checkConnection(View view) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (null == activeNetwork) {

            Snackbar.make(view, "No Internet Connection!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
}
