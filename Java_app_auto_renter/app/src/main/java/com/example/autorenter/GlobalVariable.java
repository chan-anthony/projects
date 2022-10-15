package com.example.autorenter;

import android.app.Application;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GlobalVariable extends Application {
    private boolean logInStatus;
    private String logInUsrID;
    // Used for othersProfile
    private String watchedUsrID;

    public boolean isLogInStatus() {
        return logInStatus;
    }

    public String getLogInUsrID() {return logInUsrID;}

    public String getWatchedUsrID() {
        return watchedUsrID;
    }

    public void setLogInStatus(boolean logInStatus) {this.logInStatus = logInStatus;}

    public void setLogInUsrID(String logInUsrID) {this.logInUsrID = logInUsrID;}

    public void setWatchedUsrID(String watchedUsrID) {
        this.watchedUsrID = watchedUsrID;
    }
}



