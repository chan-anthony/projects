package com.example.autorenter;

import android.content.Intent;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.example.autorenter.ui.myProfile.CircleTransform;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;
import org.apache.commons.math3.distribution.LogisticDistribution;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    private ArrayList<HashMap<String, String>> ChatItemArrayList = new ArrayList<>();
    private String userID;
    private RecyclerView mRecyclerView;
    private CustomAdapter myListAdapter;
    private String idToQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        GlobalVariable gv = (GlobalVariable)getApplicationContext();
        userID = gv.getLogInUsrID();

        //Logic start below
        updateChatList();


        //<editor-fold desc="Anthony: Switch activities with bottom navigation bar">
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                if (item.getItemId() == R.id.ic_addCar){
                    intent = new Intent(ChatActivity.this, AddCarActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.ic_profile){

                    if(gv.isLogInStatus()){
                        intent = new Intent(ChatActivity.this, MyProfileActivity.class);
                    }else{
                        intent = new Intent(ChatActivity.this, LoginActivity.class);
                    }
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    private void updateChatList(){
        ChatItemArrayList.clear();
        ChatItemArrayList = CrateChatItemList(userID);
        mRecyclerView = (RecyclerView) findViewById(R.id.ChatItems);
        Log.d("Testing",ChatItemArrayList.toString());
        myListAdapter = new CustomAdapter();
        mRecyclerView.setAdapter(myListAdapter);
    }

    private String getUserName(String CurrentUserID) throws SQLException {
        String result= "";
        SQLHandler sql = new SQLHandler();
        ResultSet r = sql.sqlSelect("SELECT Username FROM USER WHERE User_id ="+CurrentUserID);
        r.next();
        result = r.getString(1);
        return result;
    }

    private String getCarName(String CurrentCarID) throws SQLException {
        String result= "";
        SQLHandler sql = new SQLHandler();
        ResultSet r = sql.sqlSelect("SELECT Car_brand, Car_model FROM CAR WHERE Car_id ="+CurrentCarID);
        r.next();
        result = r.getString(1)+"  "+r.getString(2);
        return result;
    }

    private String getLastMsgDate(String CurrentMsg_group_id) throws SQLException {
        String result= "";
        SQLHandler sql = new SQLHandler();
        ResultSet r = sql.sqlSelect("SELECT MAX(Mssg_time) FROM MESSAGE WHERE Mssg_group_id ="+CurrentMsg_group_id);
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

    private ArrayList<HashMap<String, String>> CrateChatItemList(String userID){
        ArrayList<HashMap<String, String>> MsgList = new ArrayList<>();
        SQLHandler s = new SQLHandler();
        ResultSet r;
        try {
            r = s.sqlSelect("SELECT * from MSSG_GROUP WHERE Owner_id = "+userID+" OR Renter_id = "+userID);
            while(r.next()){
                if(userID.equals(r.getString("Owner_id")))
                    idToQuery = r.getString("Renter_id");
                else
                    idToQuery = r.getString("Owner_id");

                HashMap<String, String> tempHashMap = new HashMap<>();
                tempHashMap.put("Msg_gp_id", r.getString("Mssg_group_id"));
                tempHashMap.put("UserName", getUserName(idToQuery));
                tempHashMap.put("CarName",getCarName(r.getString("Car_id")));
                tempHashMap.put("Date",getLastMsgDate(r.getString("Mssg_group_id")));
                tempHashMap.put("Profile_pic",getUserProfilePic(idToQuery));
                MsgList.add(0,tempHashMap);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return MsgList;
    }

    public void backBtn(View view){
        finish();
    }

        private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView chatItemUserName;
            private final TextView chatItemCarName;
            private final TextView chatItemDate;
            private final ImageView chatItemCarIcon;
            private final ConstraintLayout ConstraintLayoutChatItem;


            public ViewHolder(View view) {
                super(view);
                // Step 1: Initialize all the things inside the card. And create its get method.
                chatItemUserName = (TextView) view.findViewById(R.id.chat_item_user_name);
                chatItemCarName = (TextView) view.findViewById(R.id.Chat_item_carName);
                chatItemDate = (TextView) view.findViewById(R.id.Chat_item_date);
                chatItemCarIcon = view.findViewById(R.id.chat_user_icon);
                ConstraintLayoutChatItem = view.findViewById(R.id.ConstraintLayoutChatItem);
            }
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_item, viewGroup, false);

            return new ViewHolder(view);
        }

        // Step 3: Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            viewHolder.chatItemUserName.setText(ChatItemArrayList.get(position).get("UserName"));
            viewHolder.chatItemCarName.setText(ChatItemArrayList.get(position).get("CarName"));
            viewHolder.chatItemDate.setText(ChatItemArrayList.get(position).get("Date"));
            Picasso.get().load("http://note.dyndns.org/autoRenter/uploads/" + ChatItemArrayList.get(position).get("Profile_pic")).transform(new CircleTransform()).into(viewHolder.chatItemCarIcon);
            viewHolder.ConstraintLayoutChatItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ChatActivity.this, ChatConversationActivity.class);
                    i.putExtra("Mssg_group_id",ChatItemArrayList.get(position).get("Msg_gp_id"));
                    i.putExtra("opponentName",ChatItemArrayList.get(position).get("UserName"));
                    i.putExtra("opponentUserID",idToQuery);
                    startActivity(i);
                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return ChatItemArrayList.size();
        }

        public void clear() {
            int size = ChatItemArrayList.size();
            ChatItemArrayList.clear();
            notifyItemRangeRemoved(0, size);
        }
    }

}