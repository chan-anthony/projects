package com.example.autorenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.gridlayout.widget.GridLayout;
import com.example.autorenter.ui.myProfile.CircleTransform;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class SearchResultActivity extends AppCompatActivity {

    private SearchView searchView;
    private ImageButton btnReturn;
    private String searchString;
    private String searchSQLQuery;
    private String originalSearchSQLQuery;
    private BottomNavigationView bottomNavigationView;
    private GridLayout gridLayout;
    private MaterialCardView cardViewDummy;
    private ViewGroup.LayoutParams dummyLayoutParams;
    private MaterialCardView cardView;
    private GridLayout.LayoutParams layoutParams;

    private SQLHandler sqlHandler = new SQLHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        searchView = (SearchView) findViewById(R.id.MainSearchBarResult);
        searchString = getIntent().getStringExtra("SearchBar");

        if (getIntent().hasExtra("searchSQLQuery")) {
            searchSQLQuery = getIntent().getStringExtra("searchSQLQuery");
        } else {
            searchSQLQuery = "SELECT Car_id FROM CAR WHERE (Car_brand LIKE \"%" + searchString +"%\" OR Car_model LIKE \"%"+ searchString +"%\")";
            originalSearchSQLQuery = searchSQLQuery;
        }

        searchView.setQuery(searchString, false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchString = searchView.getQuery().toString();
                Intent newIntent = getIntent();
                newIntent.putExtra("SearchBar", searchView.getQuery().toString());
                finish();
                startActivity(newIntent);
                /*
                Intent i = new Intent(MainActivity.this, SearchResultActivity.class);
                //SearchView searchObj = (SearchView) findViewById(R.id.MainSearchBar);
                i.putExtra("SearchBar", searchView.getQuery().toString());
                startActivity(i);
                return true;

                 */
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        btnReturn = findViewById(R.id.ReturnButton);
        btnReturn.setOnClickListener(view -> finish());


        try {
            Log.d("Query3",searchString);
            ArrayList<Integer> listOfCarID = getMatchedIDQuery(searchString);
            generateMaterialCardViewFromIDs(listOfCarID);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        //Bottom Bar
        bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                if (item.getItemId() == R.id.ic_home){
                    intent = new Intent(SearchResultActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.ic_addCar){
                    intent = new Intent(SearchResultActivity.this, AddCarActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.ic_profile){
                    GlobalVariable gv = (GlobalVariable)getApplicationContext();
                    if(gv.isLogInStatus()){
                        intent = new Intent(SearchResultActivity.this, MyProfileActivity.class);
                        startActivity(intent);
                    }else{
                        Context context = getApplicationContext();
                        CharSequence ToastTextMsg = "You are not logged in, Please login first!";
                        Toast toast = Toast.makeText(context, ToastTextMsg, Toast.LENGTH_SHORT);
                        toast.show();
                        Intent i = new Intent(SearchResultActivity.this, LoginActivity.class);
                        startActivity(i);
                    }

                }

                return false;
            }
        });

    }

    public void openSearchFilter(View v){
        Intent i = new Intent(SearchResultActivity.this, SearchFilter.class);
        i.putExtra("originalSearchString", searchString);
        i.putExtra("searchSQLQuery", originalSearchSQLQuery);
        startActivityForResult(i, 1);
    }

    public void openChatActivity(View v) {
        Intent i = new Intent(SearchResultActivity.this, ChatActivity.class);
        startActivity(i);
    }

    public ArrayList<Integer> getMatchedIDQuery(String searchString) throws SQLException {
        ArrayList<Integer> listOfCarID = new ArrayList<Integer>();
        Log.d("Query", searchSQLQuery);
        ResultSet resultSet = sqlHandler.sqlSelect(searchSQLQuery);


        //resultSet.first();
        while(resultSet.next()){
            String temp = resultSet.getString("Car_id");
            listOfCarID.add(Integer.valueOf(temp));
        }

        return listOfCarID;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                searchString = data.getStringExtra("SearchBar");
                searchSQLQuery = data.getStringExtra("searchSQLQuery");

                Intent newIntent = getIntent();
                newIntent.putExtra("SearchBar", searchString);
                newIntent.putExtra("searchSQLQuery", searchSQLQuery);
                finish();
                startActivity(newIntent);
            }
        }
    }

    public void generateMaterialCardViewFromIDs(ArrayList<Integer> matchedIDQuery) throws SQLException {

        for (int i = 0; i < matchedIDQuery.size(); i++) {
            //Create materialCardView core view object.
            gridLayout = (GridLayout) findViewById(R.id.SearchCardsGridLayout);
            //cardViewDummy = (MaterialCardView)findViewById(R.id.searchResultCard1);
            //dummyLayoutParams = cardViewDummy.getLayoutParams();
            cardView = (MaterialCardView) LayoutInflater.from(this).inflate(R.layout.searchresultmaterialcardview, gridLayout, false);
            //layoutParams = new GridLayout.LayoutParams();

            int finalI = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SearchResultActivity.this, itemListingActivity.class);
                    intent.putExtra("carID",matchedIDQuery.get(finalI).toString());
                    startActivity(intent);
                    finish();
                }
            });


            //layoutParams.height = dummyLayoutParams.height;
            //layoutParams.width = dummyLayoutParams.width;
            //layoutParams.setLayoutDirection(LinearLayout.HORIZONTAL);

            //cardView.setLayoutParams(layoutParams);

            //Modify the image first
            ImageView imageView = (ImageView)cardView.findViewById(R.id.search_result_card_1_image);
            ArrayList<String> images = sqlHandler.sqlCarImage(matchedIDQuery.get(i));
            Picasso.get().load(images.get(0)).into(imageView);

            //Modify the car model info
            TextView modelTextView = (TextView)cardView.findViewById(R.id.search_result_card_1_text);
            ResultSet carModelNames = sqlHandler.sqlSelect("SELECT Car_model FROM CAR WHERE Car_id = "+matchedIDQuery.get(i));
            carModelNames.first();
            String modelName = carModelNames.getString("Car_model");
            modelTextView.setText(modelName);

            //Modify the car brand info
            TextView brandTextView = (TextView)cardView.findViewById(R.id.search_result_card_1_subtitle);
            ResultSet carBrandNames = sqlHandler.sqlSelect("SELECT Car_brand FROM CAR WHERE Car_id = "+matchedIDQuery.get(i));
            carBrandNames.first();
            String brandName = carBrandNames.getString("Car_brand");
            brandTextView.setText(brandName);

            //Modify the price info
            TextView priceTextView = (TextView)cardView.findViewById(R.id.search_result_card_1_recent_price);
            ResultSet carPriceValues = sqlHandler.sqlSelect("SELECT Car_price, Car_period_unit FROM CAR WHERE Car_id = "+matchedIDQuery.get(i));
            carPriceValues.first();
            String priceValue = carPriceValues.getString("Car_price");
            String priceUnit = carPriceValues.getString("Car_period_unit");
            priceTextView.setText("$"+priceValue+"/"+priceUnit);

            //Modify the owner name info
            TextView ownerTextView = (TextView)cardView.findViewById(R.id.search_result_card_1_owner);
            ResultSet ownerIDs = sqlHandler.sqlSelect("SELECT Car_owner_id FROM CAR WHERE Car_id = "+matchedIDQuery.get(i));
            ownerIDs.first();
            String ownerID = ownerIDs.getString("Car_owner_id");
            ResultSet ownerNames = sqlHandler.sqlSelect("SELECT Username FROM USER WHERE User_id = "+ownerID);
            ownerNames.first();
            String ownerNameText = ownerNames.getString("Username");
            ownerTextView.setText(ownerNameText);


            //Modify the car owner profile pic
            ImageView profilePicView = (ImageView)cardView.findViewById(R.id.search_result_card_1_car_owner_img);

            String profilePicURL = sqlHandler.sqlGetUserIcon(Integer.valueOf(ownerID));
            if (profilePicURL != null)
                Picasso.get().load(profilePicURL).transform(new CircleTransform()).into(profilePicView);

            gridLayout.addView(cardView);

        }

    }


}