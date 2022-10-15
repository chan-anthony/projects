package com.example.autorenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingActivity extends AppCompatActivity {

    String TAG = "flow_SettingActivity";

    GlobalVariable gv;
    SQLHandler s;
    String userID;

    TextInputLayout emailLayout;
    TextInputLayout countryLayout;
    TextInputLayout biographyLayout;

    EditText emailEditText;
    EditText countryEditText;
    EditText biographyEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        gv = (GlobalVariable)getApplicationContext();
        s = new SQLHandler();
        userID = gv.getLogInUsrID();

        emailLayout = findViewById(R.id.settingEmailBox);
        countryLayout = findViewById(R.id.settingCountryBox);
        biographyLayout = findViewById(R.id.settingBiographyBox);

        emailEditText = findViewById(R.id.settingEmailText);
        countryEditText = findViewById(R.id.settingCountryText);
        biographyEditText = findViewById(R.id.settingBiographyText);

        //Query to get email, country, biography
        SQLHandler s = new SQLHandler();
        ResultSet rUserInfo;
        try {
            rUserInfo = s.sqlSelect("SELECT Email_address, Country, About_me_descr FROM USER WHERE User_id = " + userID);
            rUserInfo.first();
            emailEditText.setText(rUserInfo.getString("Email_address"), TextView.BufferType.EDITABLE);
            countryEditText.setText(rUserInfo.getString("Country"), TextView.BufferType.EDITABLE);
            biographyEditText.setText(rUserInfo.getString("About_me_descr"), TextView.BufferType.EDITABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onStart() {
        Log.d(TAG, "onStart: Start");
        super.onStart();

        // Save button
        Button save = findViewById(R.id.settingSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailEditText.getText().toString();
                String country = countryEditText.getText().toString();
                String biography = biographyEditText.getText().toString();

                // All fields shall be typed
                boolean inputErrorFlag = false;
                if (email.isEmpty()) {
                    emailLayout.setErrorEnabled(true);
                    emailLayout.setError("Required");
                    inputErrorFlag = true;
                } else {
                    emailLayout.setErrorEnabled(false);
                }
                if (country.isEmpty()) {
                    countryLayout.setErrorEnabled(true);
                    countryLayout.setError("Required");
                    inputErrorFlag = true;
                } else {
                    countryLayout.setErrorEnabled(false);
                }
                if (biography.isEmpty()) {
                    biographyLayout.setErrorEnabled(true);
                    biographyLayout.setError("Required");
                    inputErrorFlag = true;
                } else {
                    biographyLayout.setErrorEnabled(false);
                }

                // Put Bio User info to server.
                int haveUpdate = -1;
                try {
                    haveUpdate = s.sqlUpdate("UPDATE USER SET Email_address = '"+ email +"', Country = '" +country+"', About_me_descr = '"+biography+"' WHERE User_id = " +userID);
                    Log.d(TAG, "onClick: haveUpdate-" + String.valueOf(haveUpdate));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                // If update is successful, notify the user and go back to profile.
                if (haveUpdate == 1) {
                    Context context = getApplicationContext();
                    CharSequence ToastTextMsg = "Saved";
                    Toast toast = Toast.makeText(context, ToastTextMsg, Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
                }
            }
        });

        // Log out button
        Button logOut = findViewById(R.id.settingLogOut);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gv.setLogInStatus(false);
                gv.setLogInUsrID(null);
                // Notification of "Logged out"
                Context context = getApplicationContext();
                CharSequence ToastTextMsg = "Logged out";
                Toast toast = Toast.makeText(context, ToastTextMsg, Toast.LENGTH_SHORT);
                toast.show();
                Intent i = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        Log.d(TAG, "onStart: End");
    }
}