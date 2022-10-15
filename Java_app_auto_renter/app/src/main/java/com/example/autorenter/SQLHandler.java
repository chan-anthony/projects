package com.example.autorenter;

import android.net.Uri;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import static com.example.autorenter.AddCarActivity.getPathFromInputStreamUri;

public class SQLHandler {

    public static final String username ="autoRenter";   // SQL Account ID
    public static final String password ="ihkwTFdZ1NbcWXCi";   // Password
    public static final String url ="jdbc:mariadb://note.dyndns.org/autoRenter";   // Password


    /**
     * Used for sql statement with SELECT
     *
     * @param sqlStatement - SQL Statement. E.g. "SELECT id FROM HAHA WHERE id = 8"
     * @return - a resultSet.
     * @throws SQLException - Just having a try catch, and you will be fine.
     */
    public ResultSet sqlSelect(String sqlStatement) throws SQLException {
        // Connection
        Connection conn = (Connection) DriverManager.getConnection(url, username, password);

        //Query
        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery(sqlStatement);

        //close Connection
        conn.close();

        return result;
    }

    /**
     * Used for sql statement with UPDATE
     *
     * @param sqlStatement - SQL Statement. E.g. "UPDATE test SET test=1111 WHERE id = 8"
     * @return - integer with the number of effective update. E.g., No update -> 0, 1 update -> 1.
     * @throws SQLException - Just having a try catch, and you will be fine.
     */
    public int sqlUpdate(String sqlStatement) throws SQLException {
        // Connection
        Connection conn = (Connection) DriverManager.getConnection(url, username, password);

        //Query
        Statement stmt = conn.createStatement();
        int result =  stmt.executeUpdate(sqlStatement);

        //close Connection
        conn.close();

        return result;
    }

    /**
     * Input the car_id and output the car image urls in ArrayList
     * @param carId - car id
     * @return - car image urls in String type. E.g., ["http://note.dyndns.org/autoRenter/uploads/618ca6950024e4.86692705.png"
     * , "http://note.dyndns.org/autoRenter/uploads/618ce2461f96c3.08927629.png"]
     * @throws SQLException - Just having a try catch, and you will be fine.
     */
    public ArrayList<String> sqlCarImage(int carId) throws SQLException {
        final String domain = "http://note.dyndns.org/autoRenter/uploads/";
        ArrayList<String> output = new ArrayList<String>();

        // Connection
        Connection conn = (Connection) DriverManager.getConnection(url, username, password);

        //Query
        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT Car_image_url FROM CAR_IMAGE WHERE Car_id= "+carId);



        while (result.next()){
            output.add(domain + result.getString(1));
        }

        //close Connection
        conn.close();
        return output;
    }

    /**
     *  Input UserID and get user Icon url.
     * @param userID - UserID
     * @return -  user Icon url. Return null if no URL specified (User have not created profile pic upon user account creation.
     * @throws SQLException - Just having a try catch, and you will be fine.
     *
     */
    public String sqlGetUserIcon(int userID) throws SQLException {
        String domain = "http://note.dyndns.org/autoRenter/uploads/";

        // Connection
        Connection conn = (Connection) DriverManager.getConnection(url, username, password);

        //Query
        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT Profile_pic FROM USER WHERE User_id = " + userID);

        //close Connection
        conn.close();

        if (!result.next())
            throw new IllegalStateException("No UserID can be found");

        result.first();
        if (result.getString(1) == null)
            return null;
        else
            domain = domain.concat(result.getString(1));

        return domain;
    }

    public ArrayList<SliderData> sqlSliderData(int carID){
        final String urlPrefix = "http://note.dyndns.org/autoRenter/uploads/";
        ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();
        String imageURL;

        try {

            // Connection
            Connection conn = (Connection) DriverManager.getConnection(url, username, password);

            //Query
            Statement stmt = conn.createStatement();
            ResultSet r = stmt.executeQuery("SELECT Car_image_url from CAR_IMAGE WHERE Car_id = "+carID);

            while(r.next()){
                imageURL = urlPrefix + r.getString(1);
                sliderDataArrayList.add(new SliderData(imageURL));
            }

            //close Connection
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sliderDataArrayList;
    }

}
