package com.example.autorenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.autorenter.ui.myProfile.CircleTransform;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import javax.xml.transform.Result;
import java.security.acl.Owner;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class itemListingActivity extends AppCompatActivity {

    //region Init variable
    public int carID;
    public String ownerID;

    //Show Text label
    public TextView textBrand;
    public TextView textCarModel;
    public TextView textCarPrice;
    public TextView textNumOfLikes;
    public TextView textDurOfRental;
    public TextView textLocation;
    public TextView textCreatedAt;
    public TextView textAvailable;
    public TextView textDescription;

    //Below will be hide (seeMore)
    public TextView labelNumOfSeat;
    public TextView labelCarType;
    public TextView labelCarColor;
    public TextView labelCarTypeOfGas;
    public TextView labelCarMillage;
    public TextView labelCarAge;
    public TextView labelCarTrans;
    public TextView labelEngDis;
    public TextView textNumOfSeat;
    public TextView textCarType;
    public TextView textCarColor;
    public TextView textCarTypeOfGas;
    public TextView textCarMillage;
    public TextView textCarAge;
    public TextView textCarTrans;
    public TextView textEngDis;

    //Info of Owner
    public TextView textNameOfOwner;

    //Control Element
    public TextView BtnSeeMore;
    public Boolean seeMoreFlag = false;
    public ImageButton BtnLike;
    public Button ContactOwner;
    GlobalVariable gv;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_item_listing);
        //region Init variable
        textBrand = findViewById(R.id.textBrand);
        textCarModel = findViewById(R.id.text_carModel);
        textCarPrice = findViewById(R.id.text_carPrice);
        textNumOfLikes = findViewById(R.id.text_numOfLike);
        textDurOfRental = findViewById(R.id.text_durationOfRental);
        textLocation = findViewById(R.id.text_location);
        textCreatedAt = findViewById(R.id.text_createdAt);
        textAvailable = findViewById(R.id.text_available);
        textDescription = findViewById(R.id.text_description);

        //Below will be hide (seeMore)
        textNumOfSeat = findViewById(R.id.text_NumOfSeat);
        textCarType = findViewById(R.id.text_CarType);
        textCarColor = findViewById(R.id.text_CarColor);
        textCarTypeOfGas = findViewById(R.id.text_CarTypeOfGas);
        textCarMillage = findViewById(R.id.text_carMillage);
        textCarAge = findViewById(R.id.text_carAge);
        textCarTrans = findViewById(R.id.text_carTrans);
        textEngDis = findViewById(R.id.text_engDis);
        labelNumOfSeat = findViewById(R.id.label_NumOfSeat);
        labelCarType = findViewById(R.id.label_CarType);
        labelCarColor = findViewById(R.id.label_CarColor);
        labelCarTypeOfGas = findViewById(R.id.label_CarTypeOfGas);
        labelCarMillage = findViewById(R.id.label_carMillage);
        labelCarAge = findViewById(R.id.label_carAge);
        labelCarTrans = findViewById(R.id.label_carTrnas);
        labelEngDis = findViewById(R.id.label_engDis);

        //Info of Owner
        textNameOfOwner = findViewById(R.id.text_nameOfOwner);

        //Control Element
        BtnSeeMore = findViewById(R.id.btn_seeMore);
        BtnLike = findViewById(R.id.btn_like);
        ContactOwner = findViewById(R.id.btn_contactOwner);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            carID = Integer.parseInt(extras.getString("carID"));
        }

        //endregion

        gv = (GlobalVariable)getApplicationContext();

        try {
            getInfoDB(carID);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d("SQL_Error:", String.valueOf(e));
        }

        checkHvShowMore();

        picHandler(carID);

        NumOfLikeDataHandler(carID);

        starHandler();

        OwnerIconHandler();

        ownerInfoHandler();

        iconHandleLike(gv.isLogInStatus(), gv.getLogInUsrID());

        if(gv.getLogInUsrID() == null) gv.setLogInUsrID("-1");

        if(gv.getLogInUsrID().equals(ownerID)){
            ContactOwner.setText("Toggle Availability");
            ContactOwner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SQLHandler sql = new SQLHandler();
                    CharSequence ToastTextMsg = "Car availability updated";
                    int result;
                    try {
                        result = sql.sqlUpdate("UPDATE CAR SET Car_available = NOT Car_available WHERE Car_id = "+carID);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        ToastTextMsg = "Updated failed";
                    }
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, ToastTextMsg, Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
                }
            });
        }else{
            ContactOwner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!gv.isLogInStatus()){
                        Context context = getApplicationContext();
                        CharSequence ToastTextMsg = "You are not logged in, Please login first!";
                        Toast toast = Toast.makeText(context, ToastTextMsg, Toast.LENGTH_SHORT);
                        toast.show();
                        Intent i = new Intent(itemListingActivity.this, LoginActivity.class);
                        startActivity(i);
                    }else{
                        int NextRoomID;
                        String ExistRoomID;
                        SQLHandler sql = new SQLHandler();
                        ResultSet r;
                        try {
                            r = sql.sqlSelect("SELECT COUNT(Mssg_group_id),Mssg_group_id FROM MSSG_GROUP WHERE Renter_id ="+ gv.getLogInUsrID()+" AND Owner_id = "+ownerID+" AND Car_id = "+carID);
                            r.first();
                            ExistRoomID = r.getString("Mssg_group_id");
                            if(r.getString("COUNT(Mssg_group_id)").equals("0")){
                                r = sql.sqlSelect("SELECT MAX(Mssg_group_id) FROM MSSG_GROUP");
                                r.first();
                                if(r.getString(1)==null) {
                                    NextRoomID = 0;
                                } else{
                                    NextRoomID = Integer.parseInt(r.getString(1));
                                    NextRoomID++;
                                }

                                sql.sqlUpdate("INSERT INTO MSSG_GROUP VALUES( "+NextRoomID+", "+ownerID+", "+gv.getLogInUsrID()+", "+carID+" )");
                                Intent i = new Intent(itemListingActivity.this, ChatConversationActivity.class);
                                i.putExtra("Mssg_group_id",String.valueOf(NextRoomID));
                                i.putExtra("opponentUserID",ownerID);
                                i.putExtra("opponentName", getUserName(ownerID));

                                startActivity(i);
                            }else{
                                Intent i = new Intent(itemListingActivity.this, ChatConversationActivity.class);
                                i.putExtra("Mssg_group_id",ExistRoomID);
                                i.putExtra("opponentUserID",ownerID);
                                startActivity(i);
                            }

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }



                }
            });
        }

        if(gv.isLogInStatus()){
            iconHandleLike(gv.isLogInStatus(),gv.getLogInUsrID());
        }



    }


    private String getUserName(String CurrentUserID) throws SQLException {
        String result= "";
        SQLHandler sql = new SQLHandler();
        ResultSet r = sql.sqlSelect("SELECT Username FROM USER WHERE User_id ="+CurrentUserID);
        r.next();
        result = r.getString(1);
        return result;
    }

    private void ownerInfoHandler(){
        TextView text_nameOfOwner = findViewById(R.id.text_nameOfOwner);
        SQLHandler sql = new SQLHandler();
        ResultSet r;
        try {
            r = sql.sqlSelect("SELECT Username FROM USER WHERE User_id = "+ownerID);
            r.first();
            text_nameOfOwner.setText(r.getString(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void GotoOtherProfile(View view){
        gv.setWatchedUsrID(ownerID);
        Intent i = new Intent(itemListingActivity.this, OthersProfileActivity.class);
        startActivity(i);
    }

    private void getInfoDB(int carID) throws SQLException {
        SQLHandler s = new SQLHandler();
        ResultSet r = s.sqlSelect("SELECT * from CAR WHERE Car_id = "+carID);
        r.first();
        textBrand.setText(r.getString("Car_brand"));
        textCarModel.setText(r.getString("Car_model"));
        textCarPrice.setText("$"+r.getString("Car_price")+ " /day");
        textDurOfRental.setText(r.getString("Car_period_num"));
        textDurOfRental.setText(textDurOfRental.getText()+" "+r.getString("Car_period_unit"));
        textLocation.setText(r.getString("Car_location"));
        textCreatedAt.setText(r.getString("Car_upload_date"));
        ownerID = r.getString("Car_owner_id");
        if(r.getString("Car_available").equals("0")){
            textAvailable.setText("Rented");
        }else{
            textAvailable.setText("Available");
        }
        if(r.getString("Car_description")!=null)textDescription.setText(r.getString("Car_description"));
        if(r.getString("Car_num_of_seat")!=null)textNumOfSeat.setText(r.getString("Car_num_of_seat"));
        if(r.getString("Car_type")!=null)textCarType.setText(r.getString("Car_type"));
        if(r.getString("Car_color")!=null)textCarColor.setText(r.getString("Car_color"));
        if(r.getString("Car_type_of_gas")!=null)textCarTypeOfGas.setText(r.getString("Car_type_of_gas"));
        if(r.getString("Car_millage")!=null)textCarMillage.setText(r.getString("Car_millage")+" km");
        if(r.getString("Car_age_num")!=null){
            textCarAge.setText(r.getString("Car_age_num"));
            textCarAge.setText(r.getString(textCarAge.getText()+" "+r.getString("Car_age_unit")));
        }
        if(r.getString("Car_transmission")!=null)textCarTrans.setText(r.getString("Car_transmission"));
        if(r.getString("Car_eng_displacement")!=null)textEngDis.setText(r.getString("Car_eng_displacement"));

    }

    public void BtnSeeMoreClicked(View v){
        if(!seeMoreFlag){
            showSeeMore();
            seeMoreFlag = true;
            BtnSeeMore.setText("View Less");
        }else{
            hideSeeMore();
            seeMoreFlag = false;
            BtnSeeMore.setText("View More");
        }

    }

    private void showSeeMore(){
        if(textNumOfSeat.getText()!=""){
            labelNumOfSeat.setVisibility(View.VISIBLE);
            textNumOfSeat.setVisibility(View.VISIBLE);
        }
        if(textCarType.getText()!=""){
            labelCarType.setVisibility(View.VISIBLE);
            textCarType.setVisibility(View.VISIBLE);
        }
        if(textCarColor.getText()!=""){
            labelCarColor.setVisibility(View.VISIBLE);
            textCarColor.setVisibility(View.VISIBLE);
        }
        if(textCarTypeOfGas.getText()!=""){
            labelCarTypeOfGas.setVisibility(View.VISIBLE);
            textCarTypeOfGas.setVisibility(View.VISIBLE);
        }
        if(textCarMillage.getText()!=""){
            labelCarMillage.setVisibility(View.VISIBLE);
            textCarMillage.setVisibility(View.VISIBLE);
        }
        if(textCarAge.getText()!=""){
            labelCarAge.setVisibility(View.VISIBLE);
            textCarAge.setVisibility(View.VISIBLE);
        }
        if(textCarTrans.getText()!=""){
            labelCarTrans.setVisibility(View.VISIBLE);
            textCarTrans.setVisibility(View.VISIBLE);
        }
        if(textEngDis.getText()!=""){
            labelEngDis.setVisibility(View.VISIBLE);
            textEngDis.setVisibility(View.VISIBLE);
        }

    }

    private void hideSeeMore(){
        labelNumOfSeat.setVisibility(View.GONE);
        labelCarType.setVisibility(View.GONE);
        labelCarColor.setVisibility(View.GONE);
        labelCarTypeOfGas.setVisibility(View.GONE);
        labelCarMillage.setVisibility(View.GONE);
        labelCarAge.setVisibility(View.GONE);
        labelCarTrans.setVisibility(View.GONE);
        labelEngDis.setVisibility(View.GONE);
        textNumOfSeat.setVisibility(View.GONE);
        textCarType.setVisibility(View.GONE);
        textCarColor.setVisibility(View.GONE);
        textCarTypeOfGas.setVisibility(View.GONE);
        textCarMillage.setVisibility(View.GONE);
        textCarAge.setVisibility(View.GONE);
        textCarTrans.setVisibility(View.GONE);
        textEngDis.setVisibility(View.GONE);
    }

    private void checkHvShowMore(){
        //This function wil check if there are any "showMore" information, if no then it will hide the "ShowMore" Button
        Boolean flag = false;
        flag = flag || (textNumOfSeat.getText()!="");
        flag = flag || (textCarType.getText()!="");
        flag = flag || (textCarColor.getText()!="");
        flag = flag || (textCarTypeOfGas.getText()!="");
        flag = flag || (textCarMillage.getText()!="");
        flag = flag || (textCarAge.getText()!="");
        flag = flag || (textCarTrans.getText()!="");
        flag = flag || (textEngDis.getText()!="");

        if(!flag)BtnSeeMore.setVisibility(View.GONE);
    }

    private void picHandler(int carID){

        SQLHandler s = new SQLHandler();

        // we are creating array list for storing our image urls.
        ArrayList<SliderData> sliderDataArrayList = s.sqlSliderData(carID);

        // initializing the slider view.
        SliderView sliderView = findViewById(R.id.slider);

        // passing this array list inside our adapter class.
        SliderAdapter adapter = new SliderAdapter(this, sliderDataArrayList);

        // below method is used to set auto cycle direction in left to
        // right direction you can change according to requirement.
        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);

        // below method is used to
        // setadapter to sliderview.
        sliderView.setSliderAdapter(adapter);

        // below method is use to set
        // scroll time in seconds.
        //sliderView.setScrollTimeInSec(3);

        // to set it scrollable automatically
        // we use below method.
        //sliderView.setAutoCycle(true);

        // to start autocycle below method is used.
        //sliderView.startAutoCycle();
    }

    private String getCountNumOfLike(int CarID) {
        SQLHandler s = new SQLHandler();
        ResultSet r = null;
        try {
            r = s.sqlSelect("SELECT count(Car_id) from CAR_LIKE WHERE Car_id = "+carID);
            r.first();
            return r.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }

    private void NumOfLikeDataHandler(int CarID){
        textNumOfLikes.setText(getCountNumOfLike(CarID));
    }

    private boolean checkUserLiked(String userId){
        SQLHandler s = new SQLHandler();
        ResultSet r = null;
        try {
            r = s.sqlSelect("SELECT count(Car_id) from CAR_LIKE WHERE Liked_user_id = "+userId+" AND Car_id = "+carID);
            r.first();
            if (r.getString(1).equals("1"))
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void iconHandleLike(boolean LogInStatus, String userID){
        if(LogInStatus){
            int likedImageResource = getResources().getIdentifier("@drawable/icon_btn_liked", null, getPackageName());
            int likeImageResource = getResources().getIdentifier("@drawable/icon_btn_like", null, getPackageName());
            Drawable icLiked = getResources().getDrawable(likedImageResource);
            Drawable icLike = getResources().getDrawable(likeImageResource);
            if(checkUserLiked(userID)){
                BtnLike.setImageDrawable(icLiked);
            }else{
                BtnLike.setImageDrawable(icLike);
            }

        }
    }

    private boolean checkLogin(){
        if(!gv.isLogInStatus()){
            Context context = getApplicationContext();
            CharSequence ToastTextMsg = "You are not logged in, Please login first!";
            Toast toast = Toast.makeText(context, ToastTextMsg, Toast.LENGTH_SHORT);
            toast.show();
            Intent i = new Intent(itemListingActivity.this, LoginActivity.class);
            startActivity(i);
            return false;
        }else{
            return true;
        }
    }

    public void BtnLikeClicked(View view){
        if(checkLogin()){
            SQLHandler s = new SQLHandler();
            int result;
            try {
                if(checkUserLiked(gv.getLogInUsrID())){
                        result = s.sqlUpdate("DELETE FROM CAR_LIKE WHERE Liked_user_id = "+gv.getLogInUsrID());
                        if(result!=1){
                            Context context = getApplicationContext();
                            CharSequence ToastTextMsg = "Fail!";
                            Toast toast = Toast.makeText(context, ToastTextMsg, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                }else{
                    result = s.sqlUpdate("INSERT INTO CAR_LIKE VALUES ("+carID+", "+gv.getLogInUsrID()+" )");
                    if(result!=1){
                        Context context = getApplicationContext();
                        CharSequence ToastTextMsg = "Fail!";
                        Toast toast = Toast.makeText(context, ToastTextMsg, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                iconHandleLike(true,gv.getLogInUsrID());
                NumOfLikeDataHandler(carID);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void starHandler(){
        SQLHandler s = new SQLHandler();
        ResultSet r;
        int avgRating;
        try {
            // Get star

            ImageView star1 = (ImageView) findViewById(R.id.carOwnerStar1);
            ImageView star2 = (ImageView) findViewById(R.id.carOwnerStar2);
            ImageView star3 = (ImageView) findViewById(R.id.carOwnerStar3);
            ImageView star4 = (ImageView) findViewById(R.id.carOwnerStar4);
            ImageView star5 = (ImageView) findViewById(R.id.carOwnerStar5);
            ImageView[] stars = new ImageView[]{star1, star2, star3, star4, star5};

            r = s.sqlSelect("SELECT AVG(Rating) FROM USER_RATING WHERE Target_user_id = " + ownerID);
            r.first();
            avgRating = Math.round(r.getFloat(1));

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
            r = s.sqlSelect("SELECT COUNT(*) FROM USER_RATING WHERE Target_user_id = " + ownerID + " GROUP BY Reviewer_user_id");
            r.first();
            // Set
            String NumberOfRatingStr = "(" + r.getString("COUNT(*)") + ")";
            TextView carOwnerRateNo = findViewById(R.id.carOwnerRateNo);
            carOwnerRateNo.setText(NumberOfRatingStr);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void OwnerIconHandler(){
        ImageView OwnerIcon = findViewById(R.id.ownerProfileIcon);
        SQLHandler s = new SQLHandler();
        try {
            Picasso.get().load(s.sqlGetUserIcon(Integer.parseInt(ownerID))).transform(new CircleTransform()).into(OwnerIcon);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}


