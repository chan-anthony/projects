package com.example.autorenter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.chip.Chip;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchFilter extends AppCompatActivity {

    private SQLHandler sqlHandler = new SQLHandler();

    String originalSearchString;
    String oldSearchSQLQuery;
    String newSearchSQLQuery;

    Chip filterChipBestMatched;
    Chip filterChipRecentlyPosted;
    Chip filterChipPriceLowToHigh;
    Chip filterChipPriceHighToLow;

    EditText minimumPriceText;
    EditText maximumPriceText;
    EditText minimumCarMilageText;
    EditText maximumCarMilageText;
    EditText minimumVolume;
    EditText maximumVolume;
    EditText minimumAgeOfCar;
    EditText maximumAgeOfCar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_filter);

        originalSearchString = getIntent().getStringExtra("originalSearchString");
        oldSearchSQLQuery = "SELECT Car_id FROM CAR WHERE (Car_brand LIKE \"%" + originalSearchString +"%\" OR Car_model LIKE \"%"+ originalSearchString +"%\")";
        newSearchSQLQuery = oldSearchSQLQuery;

        ImageButton BtnReturn = findViewById(R.id.SearchFilterReturnButton);
        BtnReturn.setOnClickListener(view -> finish());

        filterChipBestMatched = findViewById(R.id.FilterChip_BestMatched);
        filterChipRecentlyPosted = findViewById(R.id.FilterChip_RecentlyPosted);
        filterChipPriceLowToHigh = findViewById(R.id.FilterChip_PriceLowtoHigh);
        filterChipPriceHighToLow = findViewById(R.id.FilterChip_PriceHightoLow);

        minimumPriceText = findViewById(R.id.MinPriceText);
        maximumPriceText = findViewById(R.id.MaxPriceText);
        minimumCarMilageText = findViewById(R.id.MinCarMilageText);;
        maximumCarMilageText = findViewById(R.id.MaxCarMilageText);;
        minimumVolume = findViewById(R.id.MinVolumeText);
        maximumVolume = findViewById(R.id.MaxVolumeText);
        minimumAgeOfCar = findViewById(R.id.MinAgeText);
        maximumAgeOfCar = findViewById(R.id.MaxAgeText);


    }

    public void resetFilter(View view) {
        filterChipBestMatched.setChecked(true);
        filterChipRecentlyPosted.setChecked(false);
        filterChipPriceLowToHigh.setChecked(false);
        filterChipPriceHighToLow.setChecked(false);

        minimumPriceText.setText("");
        maximumPriceText.setText("");
        minimumCarMilageText.setText("");
        maximumCarMilageText.setText("");
        minimumVolume.setText("");
        maximumVolume.setText("");
        minimumAgeOfCar.setText("");
        maximumAgeOfCar.setText("");
    }

    public void returnToMainSearch(View view) throws SQLException {

        //ResultSet resultSet = sqlHandler.sqlSelect("SELECT MAX(Car_price), MIN(Car_Price), MAX(Car_millage), MIN(Car_millage), MIN(Car_eng_displacement), MAX(Car_eng_displacement), MIN(Car_age_num), MAX(Car_age_num) FROM CAR WHERE (Car_brand LIKE \"%" + originalSearchString +"%\" OR Car_model LIKE \"%"+ originalSearchString +"%\")");
        //resultSet.first();

        int minPrice;
        int maxPrice;
        int minMillage;
        int maxMillage;
        int minVolume;
        int maxVolume;
        int minAge;
        int maxAge;


        if (!minimumPriceText.getText().toString().isEmpty()) {
            minPrice = Integer.valueOf(minimumPriceText.getText().toString());
            newSearchSQLQuery = newSearchSQLQuery + " AND Car_price >= " + minPrice;
        }
        if (!maximumPriceText.getText().toString().isEmpty()) {
            maxPrice = Integer.valueOf(maximumPriceText.getText().toString());
            newSearchSQLQuery = newSearchSQLQuery + " AND Car_price <= " + maxPrice;
        }

        if (!minimumCarMilageText.getText().toString().isEmpty()) {
            minMillage = Integer.valueOf(minimumCarMilageText.getText().toString());
            newSearchSQLQuery = newSearchSQLQuery + " AND Car_millage <= " + minMillage;
        }
        if (!maximumCarMilageText.getText().toString().isEmpty()) {
            maxMillage = Integer.valueOf(maximumCarMilageText.getText().toString());
            newSearchSQLQuery = newSearchSQLQuery + " AND Car_millage >= " + maxMillage;
        }

        if (!minimumVolume.getText().toString().isEmpty()) {
            minVolume = Integer.valueOf(minimumVolume.getText().toString());
            newSearchSQLQuery = newSearchSQLQuery + " AND Car_eng_displacement <= " + minVolume;
        }
        if (!maximumVolume.getText().toString().isEmpty()) {
            maxVolume = Integer.valueOf(maximumVolume.getText().toString());
            newSearchSQLQuery = newSearchSQLQuery + " AND Car_end_displacement >= " + maxVolume;
        }

        if (!minimumAgeOfCar.getText().toString().isEmpty()) {
            minAge = Integer.valueOf(minimumAgeOfCar.getText().toString());
            newSearchSQLQuery = newSearchSQLQuery + " AND Car_age_num >= " + minAge;
        }
        if (!maximumAgeOfCar.getText().toString().isEmpty()) {
            maxAge = Integer.valueOf(maximumAgeOfCar.getText().toString());
            newSearchSQLQuery = newSearchSQLQuery + " AND Car_age_num <= " + maxAge;
        }



        if (filterChipBestMatched.isChecked() == true) {

        }
        else if (filterChipRecentlyPosted.isChecked()) {
            newSearchSQLQuery = newSearchSQLQuery + " ORDER BY Car_upload_date DESC";
        }
        else if (filterChipPriceLowToHigh.isChecked()) {
            newSearchSQLQuery = newSearchSQLQuery + " ORDER BY Car_price";
        }
        else if (filterChipPriceHighToLow.isChecked()) {
            newSearchSQLQuery = newSearchSQLQuery + " ORDER BY Car_price DESC";
        }



        Intent intent = new Intent(SearchFilter.this, SearchResultActivity.class);
        intent.putExtra("SearchBar",originalSearchString);
        intent.putExtra("searchSQLQuery",newSearchSQLQuery);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}