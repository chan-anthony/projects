package com.example.autorenter.ui.othersProfile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.autorenter.GlobalVariable;
import com.example.autorenter.R;
import com.example.autorenter.SQLHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AboutFragment extends Fragment {

    private final String TAG = "flow_MyProfileAboutFragment";

    TextView email;
    TextView country;
    TextView bio;
    Boolean gotWatchedUsrID = false;
    String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_others_profile_about, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: Start");
        super.onViewCreated(view, savedInstanceState);

        email = view.findViewById(R.id.othersProfileAboutEmailText);
        country = view.findViewById(R.id.othersProfileAboutCountryText);
        bio = view.findViewById(R.id.othersProfileAboutBioText);

        // Query
        GlobalVariable gv = (GlobalVariable) getActivity().getApplicationContext();
        if(!gotWatchedUsrID) {
            userID = gv.getWatchedUsrID();
            gotWatchedUsrID = true;
        }
        SQLHandler s = new SQLHandler();
        ResultSet rUserInfo;
        try {
            rUserInfo = s.sqlSelect("SELECT Email_address, Country, About_me_descr FROM USER WHERE User_id =" + userID);
            rUserInfo.first();
            email.setText(rUserInfo.getString("Email_address"));
            country.setText(rUserInfo.getString("Country"));
            bio.setText(rUserInfo.getString("About_me_descr"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "onViewCreated: End");
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: Start");
        super.onStart();

    }
}