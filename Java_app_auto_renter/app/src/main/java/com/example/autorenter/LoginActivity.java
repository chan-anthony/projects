package com.example.autorenter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {

    String TAG = "flow_LoginActivity:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "onCreate: Start");

        // variables
        TextView username = findViewById(R.id.usernameedittext);
        TextView password = findViewById(R.id.passwordedittext);

        Button continuebutton = findViewById(R.id.continuebutton);
        Button createaccountbutton = findViewById(R.id.createaccountbutton);

        GlobalVariable gv = (GlobalVariable)getApplicationContext();

        //create Account Button clicked
        createaccountbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityCreateAccount();
            }
        });

        //Login Account Button clicked
        continuebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usrname = username.getText().toString();
                String mp = password.getText().toString();
                mp = EncryptPassword(mp);

                if (TextUtils.isEmpty(usrname))username.setError("Username is required");
                if (TextUtils.isEmpty(mp))password.setError("Password is required");
                if(findIndexUsrname(usrname)==-1)username.setError("Username not recognized ");

                SQLHandler s = new SQLHandler();
                try {
                    ResultSet rs = s.sqlSelect("SELECT Count(Password), User_id from USER WHERE Username = '"+usrname+"' AND Password = '"+mp+"'");
                    rs.first();
                    if(rs.getString(1).equals("1")){
                        gv.setLogInStatus(true);
                        gv.setLogInUsrID(rs.getString(2));
                        Context context = getApplicationContext();
                        CharSequence ToastTextMsg = "Successfully Added!";
                        ToastTextMsg = "Login success!";
                        Toast toast = Toast.makeText(context, ToastTextMsg, Toast.LENGTH_SHORT);
                        toast.show();
                        // openActivityMain();
                        finish();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if (!gv.isLogInStatus()){
                    password.setError("Not correct Password");
                    return;
                }

            }
        });
        Log.d(TAG, "onCreate: End");
    }

    public void openActivityMain(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void openActivityCreateAccount(){
        Intent intent = new Intent(this,CreateAccountActivity.class);
        startActivity(intent);
    }

    public int findIndexUsrname(String usrname){
        String user = "";
        int nb_line;
        SQLHandler s = new SQLHandler();
        int index = -1;
        try{
            ResultSet rs1 = s.sqlSelect("SELECT COUNT(*) from USER");
            ResultSet rs2 = s.sqlSelect("SELECT * from USER");
            rs1.first();
            nb_line = rs1.getInt(1);

            for (int i = 1; i<= nb_line;i++){
                rs2.absolute(i);
                user = rs2.getString(2);
                if(usrname.equals(user)){
                    index = i;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return index;
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