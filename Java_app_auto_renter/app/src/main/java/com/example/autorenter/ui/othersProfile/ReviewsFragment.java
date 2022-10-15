package com.example.autorenter.ui.othersProfile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.autorenter.AddReviewActivity;
import com.example.autorenter.GlobalVariable;
import com.example.autorenter.R;
import com.example.autorenter.SQLHandler;
import com.example.autorenter.ui.myProfile.CircleTransform;
import com.squareup.picasso.Picasso;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


public class ReviewsFragment extends Fragment {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

    Boolean gotWatchedUsrID = false;
    String userID;

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
        return inflater.inflate(R.layout.fragment_others_profile_reviews, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: Start");
        super.onViewCreated(view, savedInstanceState);

        // Set RecycleView
        // Reuse Memo: - Need change:
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.othersProfileReviewsRecyclerView);

        CustomAdapter myListAdapter = new CustomAdapter();
        mRecyclerView.setAdapter(myListAdapter);

        // Set mSwipeRefreshLayout
        // Reuse Memo: - Need change:
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.othersProfileReviewsRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(()->{
            arrayList.clear();
            mSwipeRefreshLayout.setRefreshing(true);
            putDataIntoArrayList();
            myListAdapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        });

        // Add review button
        Button addReview = view.findViewById(R.id.othersProfileReviewsAddReviewBtn);
        addReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AddReviewActivity.class);
                i.putExtra("TargetUserID", userID);
                startActivity(i);
            }
        });
    }

    // Reuse Memo: - Need change:
    private void putDataIntoArrayList() {
        GlobalVariable gv = (GlobalVariable) getActivity().getApplicationContext();
        if(!gotWatchedUsrID) {
            userID = gv.getWatchedUsrID();
            gotWatchedUsrID = true;
        }
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


        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView reviewerImg;
            private final TextView reviewerName;
            private final TextView starNumber;
            private final TextView description;

            public ViewHolder(View view) {
                super(view);
                //  Reuse memo: Step 1: Initialize all the things inside the card. And create its get method.
                reviewerImg = (ImageView) view.findViewById(R.id.commentsReviewerImg);
                reviewerName = (TextView) view.findViewById(R.id.commentsReviewerName);
                starNumber = (TextView) view.findViewById(R.id.commentsStarNumber);
                description = (TextView) view.findViewById(R.id.commentsDescription);
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
