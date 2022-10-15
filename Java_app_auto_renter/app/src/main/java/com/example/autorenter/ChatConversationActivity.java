package com.example.autorenter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.autorenter.ui.myProfile.CircleTransform;
import com.squareup.picasso.Picasso;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class ChatConversationActivity extends AppCompatActivity {


    String TAG = "flow_ChatConversationActivity";

    private ArrayList<HashMap<String, String>> MsgArrayList = new ArrayList<>();
    private int Mssg_group_id;
    private String userID;
    private Context context;
    private Button sendBtn;
    private RecyclerView mRecyclerView;
    private CustomAdapter myListAdapter;
    private TextView ChatUserName;
    private Integer OpponentUserID;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Start");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_conversation);
        GlobalVariable gv = (GlobalVariable)getApplicationContext();
        context = getApplicationContext();
        ChatUserName = findViewById(R.id.text_userName);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Mssg_group_id = Integer.parseInt(extras.getString("Mssg_group_id"));
            OpponentUserID =Integer.parseInt(extras.getString("opponentUserID"));
            ChatUserName.setText(extras.getString("opponentName"));
        }
        userID = gv.getLogInUsrID();


        try {
            SQLHandler sql = new SQLHandler();
            ImageView userIcon = findViewById(R.id.ChatConvsationUserIcon);
            Picasso.get().load(sql.sqlGetUserIcon(OpponentUserID)).transform(new CircleTransform()).into(userIcon);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        updateCarInfo();
        updateChat();

        sendBtn = findViewById(R.id.Btn_send);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
                updateChat();
            }
        });


        // Set mSwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.chatConversationRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(()->{
            mSwipeRefreshLayout.setRefreshing(true);
            updateChat();
            myListAdapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        });


        updateCarInfo();



        //myListAdapter.notifyDataSetChanged();
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


    private void updateCarInfo(){
        TextView carName = findViewById(R.id.ChatCarName);
        String carID;
        SQLHandler sql = new SQLHandler();
        try {
            ResultSet r = sql.sqlSelect("SELECT Car_id FROM MSSG_GROUP WHERE Mssg_group_id = "+Mssg_group_id);
            r.next();
            carID = r.getString("Car_id");

            ResultSet r2 = sql.sqlSelect("SELECT Car_brand, Car_model FROM CAR WHERE Car_id = "+carID);
            r2.next();
            carName.setText(r2.getString("Car_brand")+"  "+r2.getString("Car_model"));

            // Get images url
            ArrayList<String>  images = sql.sqlCarImage(Integer.parseInt(carID));
            // Put it into imageView
            ImageView image = (ImageView) findViewById(R.id.ChatCarImage);
            Picasso.get().load(images.get(0)).into(image);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void updateChat(){
        MsgArrayList.clear();
        MsgArrayList = CrateMsgList(Mssg_group_id,userID,100);
        mRecyclerView = (RecyclerView) findViewById(R.id.ChatConversationContent);
        myListAdapter = new CustomAdapter();
        mRecyclerView.setAdapter(myListAdapter);
    }

    private void sendMessage(){
        int Messg_id;
        int updateState = -1;
        TextView MssgInput = findViewById(R.id.message_input);
        String MessgText = String.valueOf(MssgInput.getText());
        if(MessgText.equals(""))return;
        SQLHandler s = new SQLHandler();

        try {
            ResultSet resultMaxCarID = s.sqlSelect("SELECT MAX(Mssg_id) FROM MESSAGE");
            resultMaxCarID.first();
            Messg_id = Integer.parseInt(resultMaxCarID.getString(1));
            Messg_id++;
            updateState = s.sqlUpdate("INSERT INTO MESSAGE VALUES ('"+Messg_id+"', '"+userID+"', '"+MessgText+"', null, null, '"+Mssg_group_id+"')");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d("Error", String.valueOf(e));
        }
        if(updateState!=1){
            CharSequence ToastTextMsg = "Error!";
            Toast toast = Toast.makeText(context, ToastTextMsg, Toast.LENGTH_SHORT);
            toast.show();
        }
        if(updateState==1)MssgInput.setText("");
    }

    private ArrayList<HashMap<String, String>> CrateMsgList(int Mssg_group_id, String userID , int NumOfMssg){
        ArrayList<HashMap<String, String>> MsgList = new ArrayList<>();
        SQLHandler s = new SQLHandler();
        ResultSet r = null;
        try {
            r = s.sqlSelect("SELECT * from MESSAGE WHERE Mssg_group_id = "+Mssg_group_id+" order by Mssg_time DESC limit "+NumOfMssg);
            while(r.next()){
                HashMap<String, String> tempHashMap = new HashMap<>();
                tempHashMap.put("chatMsg",r.getString("Mssg_txt"));
                if(userID.equals(r.getString("Mssg_sender_id"))){
                    tempHashMap.put("user","sender");
                }else{
                    tempHashMap.put("user","receiver");
                }
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
            private final TextView chatMsgBubbleLeft;
            private final TextView chatMsgBubbleRight;
            //private final TextView carModelView;

            public ViewHolder(View view) {
                super(view);
                // Step 1: Initialize all the things inside the card. And create its get method.
                chatMsgBubbleLeft = (TextView) view.findViewById(R.id.show_message_left);
                chatMsgBubbleRight = (TextView) view.findViewById(R.id.show_message_right);
            }
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_msg_bubble, viewGroup, false);

            return new ViewHolder(view);
        }

        // Step 3: Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            if(MsgArrayList.get(position).get("user").equals("receiver")){
                viewHolder.chatMsgBubbleLeft.setText(MsgArrayList.get(position).get("chatMsg"));
                viewHolder.chatMsgBubbleLeft.setVisibility(View.VISIBLE);
            }else{
                viewHolder.chatMsgBubbleRight.setText(MsgArrayList.get(position).get("chatMsg"));
                viewHolder.chatMsgBubbleRight.setVisibility(View.VISIBLE);
            }

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return MsgArrayList.size();
        }

        public void clear() {
            int size = MsgArrayList.size();
            MsgArrayList.clear();
            notifyItemRangeRemoved(0, size);
        }
    }
}