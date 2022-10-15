package com.example.autorenter.ui.myProfile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.autorenter.*;
import com.squareup.picasso.Picasso;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


public class MyProfileReviewsFragment extends Fragment {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

    // Reuse Memo: - Need change:
    private final String TAG = "flow_MyProfileReviewsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Start");

        arrayList.clear();
        putDataIntoArrayList();

        Log.d(TAG, "onCreateView: End");
        return inflater.inflate(R.layout.fragment_my_profile_reviews, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: Start");
        super.onViewCreated(view, savedInstanceState);

        // Set RecycleView
        // Reuse Memo: - Need change:
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.myProfileReviewsRecyclerView);

        CustomAdapter myListAdapter = new CustomAdapter();
        mRecyclerView.setAdapter(myListAdapter);

        // Set mSwipeRefreshLayout
        // Reuse Memo: - Need change:
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.myProfileReviewsRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(()->{
            arrayList.clear();
            mSwipeRefreshLayout.setRefreshing(true);
            putDataIntoArrayList();
            myListAdapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        });
    }

    // Reuse Memo: - Need change:
    private void putDataIntoArrayList() {
            GlobalVariable gv = (GlobalVariable) getActivity().getApplicationContext();
            String userID = gv.getLogInUsrID();
            SQLHandler s = new SQLHandler();
            ResultSet rComments;
            try {
                rComments = s.sqlSelect("SELECT * FROM USER_RATING WHERE Target_user_id = " + userID);
                rComments.first();

                // Put cars data into arrayList
                do {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("rateID", rComments.getString("Rate_id"));
                    hashMap.put("reviewerUserID", rComments.getString("Reviewer_user_id"));

                    // Get the userName from user ID
                    ResultSet rUerName = s.sqlSelect("SELECT Username FROM USER WHERE User_id = " + Integer.parseInt(rComments.getString("Reviewer_user_id")));
                    rUerName.first();
                    hashMap.put("reviewerID", rComments.getString("Reviewer_user_id"));
                    Log.d(TAG, "putDataIntoArrayList: reviewerImg-" + rComments.getString("Reviewer_user_id"));
                    hashMap.put("reviewerUserName", rUerName.getString("Username"));
                    hashMap.put("reviewerImg", s.sqlGetUserIcon(rComments.getInt("Reviewer_user_id")));
                    Log.d(TAG, "putDataIntoArrayList: reviewerImg-" + s.sqlGetUserIcon(rComments.getInt("Reviewer_user_id")));
                    hashMap.put("targetUserID", rComments.getString("Target_user_id"));
                    hashMap.put("rating", rComments.getString("Rating"));
                    hashMap.put("description", rComments.getString("Description"));
                    arrayList.add(hashMap);
                } while (rComments.next());
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    // Reuse memo: - Need change: Total three steps.
    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
        GlobalVariable gv = (GlobalVariable) getActivity().getApplicationContext();

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView reviewerImg;
            private final TextView reviewerName;
            private final TextView starNumber;
            private final TextView description;
            private final View mView;

            public ViewHolder(View view) {
                super(view);
                //  Reuse memo: Step 1: Initialize all the things inside the card. And create its get method.
                reviewerImg = (ImageView) view.findViewById(R.id.commentsReviewerImg);
                reviewerName = (TextView) view.findViewById(R.id.commentsReviewerName);
                starNumber = (TextView) view.findViewById(R.id.commentsStarNumber);
                description = (TextView) view.findViewById(R.id.commentsDescription);
                mView = view;
            }
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            //  Reuse memo: Step 2: Change the view to your card view.
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.shr_profile_comments, viewGroup, false);

            return new ViewHolder(view);
        }

        //  Reuse memo: Step 3: Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            Picasso.get().load(arrayList.get(position).get("reviewerImg")).transform(new CircleTransform()).into(viewHolder.reviewerImg);
            viewHolder.reviewerName.setText(arrayList.get(position).get("reviewerUserName"));
            viewHolder.starNumber.setText(arrayList.get(position).get("rating"));
            viewHolder.description.setText(arrayList.get(position).get("description"));

            // When review on click
            viewHolder.mView.setOnClickListener((v) -> {
                gv.setWatchedUsrID(arrayList.get(position).get("reviewerID"));
                Log.d(TAG, "onBindViewHolder: reviewerID-" + gv.getWatchedUsrID());
                Intent i = new Intent(getActivity(), OthersProfileActivity.class);
                startActivity(i);
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public void clear() {
            int size = arrayList.size();
            arrayList.clear();
            notifyItemRangeRemoved(0, size);
        }
    }
}
