package com.android.viba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceFragmentCompat.OnPreferenceStartFragmentCallback;

import android.companion.CompanionDeviceManager;
import android.icu.text.CaseMap;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;
class SettingsActivity extends AppCompatActivity implements OnPreferenceStartFragmentCallback {

    SlidrInterface slidrInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_preference, new Fragment())
                .commit();

        slidrInterface = Slidr.attach(this);


        if(savedInstanceState==null) {
            supportFragmentManager().beginTransaction()
                    .replace(R.id.content_preference, new MainPreference())
                    .commit();
        }
        else{
            getTitle()=savedInstanceState.getCharSequence(TAG_TITLE);

        }
        supportFragmentManager().addOnBackStackChangedListener() {
            if(supportFragmentManager().backStackEntryCount == 0){
                setTitle(R.string.action_settings);
            }
        }
        setUpToolbar();{

        }

    }

    private FragmentManager supportFragmentManager() {
        return null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putCharSequence(TAG_TITLE,getTitle());

    }

    @Override
    public boolean onSupportNavigateUp() {
        if(supportFragmentManager().popBackStackImmediate()){
            return true;
        }
        return super.onSupportNavigateUp();
    }

    private void setUpToolbar() {
        getSupportActionBar()?.setTitle(R.string.action_settings);
        getSupportActionBar()?.setDiplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowEnabled(true);
    }




    class MainPreference extends PreferenceFragmentCompat{
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
           setPreferencesFromResource(R.xml.main_preference,rootKey);
        }

    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
     return false;
     //Initiate the new fragment
        val args =pref ?. extras;
        val fragment = pref?.fragment?.let ;{
            it String;
            supportFragmentManager().getFragmentFactory().instantiate(
                    getClassLoader(),
                    it
            ).apply; {
                this:Fragment
                        arguments = args
                setTargetFragment(caller, requestCode:0);
            }
        }
        fragment?.let ;/{
            it Fragment;
                    supportFragmentManager().beginTransaction()
                            .replace(R.id.content_preference,it)
                            .addToBackStack(null)
                            .commit();

        }
        title = pref?.title;
        return true;
    }



    CompanionDeviceManager object{
        private val TAG_TITLE = "SETTINGS_ACTIVITY";
    }
}






