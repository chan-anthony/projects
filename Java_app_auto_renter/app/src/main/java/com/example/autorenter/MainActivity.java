package com.example.autorenter;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.autorenter.ui.myProfile.CircleTransform;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;
import android.widget.SearchView.OnQueryTextListener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    String TAG = "flow_MainActivity";
    private SearchView searchView;
    private ArrayList<HashMap<String, String>> MostRecentArrayList = new ArrayList<>();
    private ArrayList<HashMap<String, String>> MostLikedArrayList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private CustomAdapter myListAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Start");

        // Get global variable
        GlobalVariable gv = (GlobalVariable)getApplicationContext();

        if(!gv.isLogInStatus())gv.setLogInStatus(false);

        // Allowing thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        //Main logic Start
        updateRecyclerViewMostRecentList();
        updateRecyclerViewLikeRecentList();

        // Set mSwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.MainActRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(()->{
            mSwipeRefreshLayout.setRefreshing(true);
            updateRecyclerViewMostRecentList();
            updateRecyclerViewLikeRecentList();
            myListAdapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        });

        //<editor-fold desc="Anthony: Switch activities with bottom navigation bar">
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                if (item.getItemId() == R.id.ic_addCar){
                    intent = new Intent(MainActivity.this, AddCarActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.ic_profile){

                    if(gv.isLogInStatus()){
                        intent = new Intent(MainActivity.this, MyProfileActivity.class);
                    }else{
                        intent = new Intent(MainActivity.this, LoginActivity.class);
                    }
                    finish();
                    startActivity(intent);
                }
                return false;
            }
        });
        //</editor-fold>

        searchView = (SearchView) findViewById(R.id.MainSearchBar);
        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent i = new Intent(MainActivity.this, SearchResultActivity.class);
                //SearchView searchObj = (SearchView) findViewById(R.id.MainSearchBar);
                i.putExtra("SearchBar", searchView.getQuery().toString());
                startActivity(i);
                return true;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Change the selected button
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);


        ImageButton msgButton = findViewById(R.id.MainMessage);
        msgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gv.isLogInStatus()){
                    Context context = getApplicationContext();
                    CharSequence ToastTextMsg = "You are not logged in, Please login first!";
                    Toast toast = Toast.makeText(context, ToastTextMsg, Toast.LENGTH_SHORT);
                    toast.show();
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                }else{
                    Intent i = new Intent(MainActivity.this, ChatActivity.class);
                    startActivity(i);
                }

            }
        });


        Log.d(TAG, "onCreate: End");
    }


    //<editor-fold desc="Anthony: Other stages">
    public void onStart() {
        Log.d(TAG, "onStart: Start");
        super.onStart();

        Log.d(TAG, "onStart: End");
    }

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

    private void updateRecyclerViewMostRecentList(){
        MostRecentArrayList.clear();
        MostRecentArrayList = addMostRecent();
        mRecyclerView = (RecyclerView) findViewById(R.id.MostRecentRecyclerView);
        myListAdapter = new MainActivity.CustomAdapter();
        mRecyclerView.setAdapter(myListAdapter);
    }

    private void updateRecyclerViewLikeRecentList(){
        MostRecentArrayList.clear();
        MostRecentArrayList = addMostRecent();
        mRecyclerView = (RecyclerView) findViewById(R.id.MostLikedRecyclerView);
        myListAdapter = new MainActivity.CustomAdapter();
        mRecyclerView.setAdapter(myListAdapter);
    }

    private ArrayList<HashMap<String, String>> addMostRecent(){
        ArrayList<HashMap<String, String>> itemsList = new ArrayList<>();
        SQLHandler sql = new SQLHandler();
        ResultSet r;
        try {
            r = sql.sqlSelect("SELECT Car_id, Car_brand, Car_model, Car_price, Car_owner_id FROM CAR ORDER BY Car_upload_date DESC LIMIT 10");
            while(r.next()){
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("CarId",r.getString("Car_id"));
                hashMap.put("CarBrand",r.getString("Car_brand"));
                hashMap.put("CarModel",r.getString("Car_model"));
                hashMap.put("CarPrice","$"+r.getString("Car_price")+" /day");
                hashMap.put("CarOwnerID",r.getString("Car_owner_id"));
                hashMap.put("CarOwnerName", getUserName(r.getString("Car_owner_id")));
                hashMap.put("CarOwnerPic", getUserProfilePic(r.getString("Car_owner_id")));
                hashMap.put("carImageUrl", sql.sqlCarImage(Integer.parseInt(r.getString("Car_id"))).get(0));
                itemsList.add(hashMap);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return itemsList;
    }

    private String getUserName(String CurrentUserID) throws SQLException {
        String result= "";
        SQLHandler sql = new SQLHandler();
        ResultSet r = sql.sqlSelect("SELECT Username FROM USER WHERE User_id ="+CurrentUserID);
        r.next();
        result = r.getString(1);
        return result;
    }

    private String getUserProfilePic(String CurrentUserID) throws SQLException {
        String result= "";
        SQLHandler sql = new SQLHandler();
        ResultSet r = sql.sqlSelect("SELECT Profile_pic FROM USER WHERE User_id ="+CurrentUserID);
        r.next();
        result = r.getString(1);
        return result;
    }

    public void openListingActivity(View v){
        Intent i = new Intent(MainActivity.this, itemListingActivity.class);
        i.putExtra("carID","37");
        startActivity(i);
    }


    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView main_car_card_image;
            private final TextView main_car_card_title;
            private final TextView main_car_card_subtitle;
            private final TextView main_car_card_price;
            private final ImageView main_car_card_Owner_icon;
            private final TextView main_car_card_Owner_name;
            private final MaterialCardView mainCarCard;


            public ViewHolder(View view) {
                super(view);
                // Step 1: Initialize all the things inside the card. And create its get method.
                main_car_card_image = (ImageView) view.findViewById(R.id.main_car_card_image);
                main_car_card_title = (TextView) view.findViewById(R.id.main_car_card_title);
                main_car_card_subtitle = (TextView) view.findViewById(R.id.main_car_card_subtitle);
                main_car_card_price = (TextView) view.findViewById(R.id.main_car_card_price);
                main_car_card_Owner_icon = (ImageView) view.findViewById(R.id.main_car_card_Owner_icon);
                main_car_card_Owner_name = (TextView) view.findViewById(R.id.main_car_card_Owner_name);
                mainCarCard = (MaterialCardView) view.findViewById(R.id.mainActCarCard);
            }
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_car_card, viewGroup, false);
            return new ViewHolder(view);
        }

        // Step 3: Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            Picasso.get().load(MostRecentArrayList.get(position).get("carImageUrl")).into(viewHolder.main_car_card_image);
            viewHolder.main_car_card_title.setText(MostRecentArrayList.get(position).get("CarModel"));
            viewHolder.main_car_card_subtitle.setText(MostRecentArrayList.get(position).get("CarBrand"));
            viewHolder.main_car_card_price.setText(MostRecentArrayList.get(position).get("CarPrice"));
            Picasso.get().load("http://note.dyndns.org/autoRenter/uploads/"+MostRecentArrayList.get(position).get("CarOwnerPic")).transform(new CircleTransform()).into(viewHolder.main_car_card_Owner_icon);
            viewHolder.main_car_card_Owner_name.setText(MostRecentArrayList.get(position).get("CarOwnerName"));

            viewHolder.mainCarCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(MainActivity.this, itemListingActivity.class);
                    i.putExtra("carID",MostRecentArrayList.get(position).get("CarId"));
                    startActivity(i);
                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return MostRecentArrayList.size();
        }

        public void clear() {
            int size = MostRecentArrayList.size();
            MostRecentArrayList.clear();
            notifyItemRangeRemoved(0, size);
        }
    }

}
