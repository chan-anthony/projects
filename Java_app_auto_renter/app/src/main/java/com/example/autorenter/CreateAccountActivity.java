package com.example.autorenter;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class CreateAccountActivity extends AppCompatActivity {

    String TAG = "flow_CreateAccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createaccount);

        TextView username = findViewById(R.id.usernameedittext1);
        TextView password1 = findViewById(R.id.passwordedittext1);
        TextView password2 = findViewById(R.id.passwordedittext12);
        Button CreateAccountButton = findViewById(R.id.createaccountbutton1);
        Button login = findViewById(R.id.loginbutton1);

        // Switch Activity : 
        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                String usrname = username.getText().toString();
                String mp1 = password1.getText().toString();
                String mp2 = password2.getText().toString();

                // Validation check for user input
                if (TextUtils.isEmpty(usrname))
                    username.setError("Username is required");
                if (TextUtils.isEmpty(mp1))
                    password1.setError("Password is required");
                if (TextUtils.isEmpty(mp2))
                    password2.setError("Password Confirmation is required");
                if (!mp1.equals(mp2))
                    password1.setError("Password and Password Confirmation are not the same");
                if (!newUserName(usrname)) {
                    Log.d(TAG, "onClick: newUserName-" + String.valueOf(newUserName(usrname)));
                    username.setError("Username is already used");
                }


                // Find the last user ID
                int lastUserID = -1;
                SQLHandler s = new SQLHandler();
                try {
                    ResultSet rs = s.sqlSelect("SELECT MAX(User_id) FROM USER");
                    rs.first();
                    lastUserID = rs.getInt(1);

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // openActivityMyProfile();

                writeDatabaseUser(lastUserID + 1, usrname, mp1);
                finish();
            }
        });

        // Login Button
        login.setOnClickListener(v -> openActivityLogin());
    }

    private void openActivityLogin(){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    private void openActivityMyProfile() {
        Intent intent = new Intent(this, MyProfileActivity.class);
        startActivity(intent);
    }
    
    private boolean newUserName(String usrName){
        Log.d(TAG, "newUserName: Start");
        boolean output = false;
        SQLHandler s = new SQLHandler();
        try {
            ResultSet rs = s.sqlSelect("SELECT COUNT(*) FROM USER WHERE Username = \'" + usrName + "\' GROUP BY Username");
            // If resultSet is empty which mean it is new userName, return true
            if (!rs.next()) {
                output = true;
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        Log.d(TAG, "newUserName: End");
        return output;
    }

    private void writeDatabaseUser(int User_id, String usrName, String mp){

        SQLHandler s = new SQLHandler();
        try {
            mp = EncryptPassword(mp);

            //execute query
            s.sqlSelect("INSERT into USER (User_id, Username, Password) VALUES ('" +User_id+"', '"+usrName+"', '"+mp+"')");

        }catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    private String EncryptPassword(String ToProcess){
        String salt = "B@2e817b38";
        return get_SHA_512_SecurePassword(ToProcess,salt);
    }

    private String get_SHA_512_SecurePassword(String passwordToHash,String salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)
                        .substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }


}
