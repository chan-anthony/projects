package com.example.autorenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AddReviewActivity extends AppCompatActivity {

    String TAG = "flow_AddReviewActivity";

    GlobalVariable gv;
    SQLHandler s;
    String userID;
    String targetUserID;

    TextInputLayout addStarLayout;
    AutoCompleteTextView autoCompleteTextView_star;
    TextInputLayout addReviewLayout;
    EditText addReviewEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            targetUserID = extras.getString("TargetUserID");
        }

        gv = (GlobalVariable)getApplicationContext();
        s = new SQLHandler();
        userID = gv.getLogInUsrID();

        addStarLayout = findViewById(R.id.addStarBox);
        autoCompleteTextView_star = findViewById(R.id.addStarAutoComplete);
        String[] optionAvailable = {"1", "2", "3", "4", "5"};
        ArrayAdapter<String> itemAdapterAvailable = new ArrayAdapter(AddReviewActivity.this, R.layout.items_list, optionAvailable);
        autoCompleteTextView_star.setAdapter(itemAdapterAvailable);

        addReviewLayout = findViewById(R.id.addReviewBox);
        addReviewEditText = findViewById(R.id.addReviewText);



    }

    public void onStart() {
        Log.d(TAG, "onStart: Start");
        super.onStart();

        // Save button
        Button save = findViewById(R.id.settingSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String addReview = addReviewEditText.getText().toString();
                String addStar = autoCompleteTextView_star.getText().toString();

                // All fields shall be typed
                boolean inputErrorFlag = false;
                if (addStar.isEmpty()) {
                    addStarLayout.setErrorEnabled(true);
                    addStarLayout.setError("Required");
                    inputErrorFlag = true;
                } else {
                    addStarLayout.setErrorEnabled(false);
                }
                if (addReview.isEmpty()) {
                    addReviewLayout.setErrorEnabled(true);
                    addReviewLayout.setError("Required");
                    inputErrorFlag = true;
                } else {
                    addReviewLayout.setErrorEnabled(false);
                }

                // Find the last Rate ID
                int lastRateID = -1;
                SQLHandler s = new SQLHandler();
                try {
                    ResultSet rs = s.sqlSelect("SELECT MAX(Rate_id) FROM USER_RATING");
                    rs.first();
                    lastRateID = rs.getInt(1);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                int addedRateID = lastRateID + 1;

                // Put Review into the server.
                int haveInsert = -1;
                try {
                    haveInsert = s.sqlUpdate("INSERT INTO USER_RATING (Rate_id, Reviewer_user_id, Target_user_id, Rating, Description) VALUES ('"+addedRateID+"', '"+userID+"', '"+targetUserID+"', '"+addStar+"', '"+addReview+"')");
                    Log.d(TAG, "onClick: haveUpdate-" + String.valueOf(haveInsert));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                // If update is successful, notify the user and go back to profile.
                if (haveInsert == 1) {
                    Context context = getApplicationContext();
                    CharSequence ToastTextMsg = "Saved";
                    Toast toast = Toast.makeText(context, ToastTextMsg, Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
                }
            }
        });
        Log.d(TAG, "onStart: End");
    }
}