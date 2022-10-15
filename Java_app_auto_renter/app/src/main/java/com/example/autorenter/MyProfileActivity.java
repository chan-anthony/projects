// For opening the gallery - Reference: https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/

package com.example.autorenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.autorenter.ui.myProfile.CircleTransform;
import com.example.autorenter.ui.myProfile.SectionsPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import com.example.autorenter.databinding.ActivityMyProfileBinding;
import com.squareup.picasso.Picasso;
import okhttp3.*;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.BlockingDeque;


public class MyProfileActivity extends AppCompatActivity {

    private ActivityMyProfileBinding binding;
    String TAG = "flow_MyProfileActivity:";

    GlobalVariable gv;
    SQLHandler s;
    // part 1:
    String userID;
    TextView usernameView;
    TextView joined_dateView;
    ImageButton profile_picView;
    // Part 2:
    TextView listingNum;
    // Part 3:
    ImageView star1;
    ImageView star2;
    ImageView star3;
    ImageView star4;
    ImageView star5;
    ImageView[] stars;
    TextView NumberOfRating;

    ImageButton setting;

    // Upload profile user icon
    // constant to compare
    // the activity result code
    int SELECT_PICTURE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Start");
        super.onCreate(savedInstanceState);
        // Allowing thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_my_profile);

        //<editor-fold desc="Tab. Important: This section must be on the top. Otherwise cannot setText()">
        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        //</editor-fold>

        //<editor-fold desc="Init variables">
        gv = (GlobalVariable)getApplicationContext();
        s = new SQLHandler();
        userID = gv.getLogInUsrID();
        usernameView = (TextView) findViewById(R.id.myProfileUsrName);
        joined_dateView = (TextView) findViewById(R.id.myProfileJoinDate);
        profile_picView = (ImageButton) findViewById(R.id.myProfileIcon);

        listingNum = (TextView) findViewById(R.id.myProfileListNo);

        star1 = (ImageView) findViewById(R.id.myProfileStar1);
        star2 = (ImageView) findViewById(R.id.myProfileStar2);
        star3 = (ImageView) findViewById(R.id.myProfileStar3);
        star4 = (ImageView) findViewById(R.id.myProfileStar4);
        star5 = (ImageView) findViewById(R.id.myProfileStar5);
        stars = new ImageView[]{star1, star2, star3, star4, star5};
        NumberOfRating = (TextView) findViewById(R.id.myProfileRateNo);

        setting = findViewById(R.id.myProfileSettingBtn);

        //</editor-fold>

        //<editor-fold desc="Anthony: Switch activities with bottom navigation bar">
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                if (item.getItemId() == R.id.ic_home){
                    intent = new Intent(MyProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.ic_addCar){
                    intent = new Intent(MyProfileActivity.this, AddCarActivity.class);
                    startActivity(intent);
                }

                return false;
            }
        });
        // Change the selected button
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        //</editor-fold>

        Log.d(TAG, "onCreate: End");
    }

    //<editor-fold desc="Anthony: Other stages">
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart: Start");

        // Start getting user info.
        //<editor-fold desc="Part 1: Get and set username, joined_date, Profile_pic">
        ResultSet r;
        Log.d("InSQL", "onCreate: username - " + userID);
        try {
            r = s.sqlSelect("SELECT Username, Joined_date, Profile_pic FROM USER WHERE User_id = " + userID);
            r.first();

            Log.d("InSQL", "onCreate: Joined_date - " + r.getString("Joined_date"));
            usernameView.setText(r.getString("Username"));
            joined_dateView.setText(r.getString("Joined_date"));
            if (r.getString("Profile_pic") != null)
                Picasso.get().load("http://note.dyndns.org/autoRenter/uploads/" + r.getString("Profile_pic")).transform(new CircleTransform()).into(profile_picView);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //</editor-fold>

        //<editor-fold desc="Part 2: Get and set number of listing of user.">
        r = null;
        Bundle bundle = new Bundle();
        try {
            r = s.sqlSelect("SELECT COUNT(*) FROM CAR WHERE Car_owner_id = " + userID + " GROUP BY Car_owner_id");
            r.first();

            // Put it to bundle. Then fragment can get this variable.
            bundle.putString("numberOfListing", r.getString(1));

            listingNum.setText(r.getString(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //</editor-fold>

        //<editor-fold desc="Part 3: Get and set Stars and total number of rating on a user.">
        r = null;
        int avgRating;
        try {
            // Get star
            r = s.sqlSelect("SELECT AVG(Rating) FROM USER_RATING WHERE Target_user_id = " + userID);
            r.first();
            avgRating = Math.round(r.getFloat(1));
            Log.d(TAG, "onStart: avgRating-" + String.valueOf(avgRating));

            // Set star
            int count = 0;
            for (ImageView i : stars) {
                if (count == avgRating)
                    break;
                i.setImageResource(R.drawable.profile_star);
                count++;
            }

            // Get total number of rating on a user.
            r = null;
            r = s.sqlSelect("SELECT COUNT(*) FROM USER_RATING WHERE Target_user_id = " + userID + " GROUP BY Reviewer_user_id");
            r.first();
            // Set
            String NumberOfRatingStr = "(" + r.getString("COUNT(*)") + ")";
            NumberOfRating.setText(NumberOfRatingStr);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        //</editor-fold>

        // Upload profile user icon
        profile_picView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        // Setting button
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyProfileActivity.this, SettingActivity.class);
                startActivity(i);
            }
        });
        Log.d(TAG, "onStart: End");
    }

    /**
     *  Triggered this method when user click the icon.
     */
    private void imageChooser() {
        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    /**
     * this function is triggered when user selects the image from the imageChooser.
     * Also decide what the system would do after selecting image from gallery. (Update and upload)
     * @param requestCode - requestCode
     * @param resultCode - resultCode
     * @param data - data (image)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: Start");
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                Log.d(TAG, "onActivityResult: URL from the gallery -" + data.getData());
                if (null != selectedImageUri) {
                    // update the icon in the layout
                    // profile_picView.setImageURI(null);
                    profile_picView.setImageURI(selectedImageUri);
                    // Upload
                    try {
                        uploadImage(selectedImageUri, userID);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Log.d(TAG, "onActivityResult: End");
    }

    //<editor-fold desc="Uploading image">
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private final OkHttpClient client = new OkHttpClient();

    public void uploadImage(Uri filePath, String userID) throws Exception {
        String filePathConverted = getPathFromInputStreamUri(getApplicationContext(), filePath,"temp_pic");
        File file = new File(filePathConverted);
        Log.d(TAG, "uploadImage: filePathConverted-"+filePathConverted);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("UserID", userID)
                .addFormDataPart("file", "something.png", RequestBody.create(MEDIA_TYPE_PNG, file))
                .build();

        Request request = new Request.Builder()
                .url("http://note.dyndns.org/autoRenter/uploadProfilePic.php")
                .post(requestBody)
                .build();

        // The Upload part
        try (okhttp3.Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // If there is response, print out.
            System.out.println(response.body().string());
        }
    }

    /**
     * Private method which is for uploadImage().
     * used to transform Url type to String url. (Url type is not an url that we can understand, but String url can.)
     * @param context - context
     * @param uri - uri
     * @param fileName - fileName
     * @return - String url
     */
    private static String getPathFromInputStreamUri(Context context, Uri uri, String fileName) {
        InputStream inputStream = null;
        String filePath = null;

        if (uri.getAuthority() != null) {
            try {
                inputStream = context.getContentResolver().openInputStream(uri);
                File file = createTemporalFileFrom(context, inputStream, fileName);
                filePath = file.getPath();

            } catch (Exception e) {
                Log.d("error", String.valueOf(e));
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    Log.d("error", String.valueOf(e));
                }
            }
        }

        return filePath;
    }

    /**
     * Private method which is for getPathFromInputStreamUri().
     * @param context - context
     * @param inputStream - inputStream
     * @param fileName - fileName
     * @return - File
     * @throws IOException - IOException
     */
    private static File createTemporalFileFrom(Context context, InputStream inputStream, String fileName)
            throws IOException {
        File targetFile = null;

        if (inputStream != null) {
            int read;
            byte[] buffer = new byte[8 * 1024];
            targetFile = new File(context.getCacheDir(), fileName);
            if (targetFile.exists()) {
                targetFile.delete();
            }
            OutputStream outputStream = new FileOutputStream(targetFile);

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();

            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return targetFile;

    }
    //</editor-fold>

    public void onResume() {
        Log.d(TAG, "onResume: Start");
        super.onResume();

        Log.d(TAG, "onResume: End");
    }

    public void onPause() {
        Log.d(TAG, "onPause: Start");
        super.onPause();

        Log.d(TAG, "onPause: End");
    }

    public void onStop() {
        Log.d(TAG, "onStop: Start");
        super.onStop();

        Log.d(TAG, "onStop: End");
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy: Start");

        super.onDestroy();
        Log.d(TAG, "onDestroy: End");
    }

    public void onRestart() {
        Log.d(TAG, "onRestart: Start");

        super.onRestart();
        Log.d(TAG, "onRestart: End");
    }
    //</editor-fold>
}