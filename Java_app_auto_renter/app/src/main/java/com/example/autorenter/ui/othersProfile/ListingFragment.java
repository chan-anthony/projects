// Reference: https://developer.android.com/guide/topics/ui/layout/recyclerview#java
// https://thumbb13555.pixnet.net/blog/post/311803031
// https://stackoverflow.com/questions/44454797/pull-to-refresh-recyclerview-android

package com.example.autorenter.ui.othersProfile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.autorenter.GlobalVariable;
import com.example.autorenter.R;
import com.example.autorenter.SQLHandler;
import com.example.autorenter.itemListingActivity;
import com.squareup.picasso.Picasso;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ListingFragment extends Fragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

    Boolean gotWatchedUsrID = false;
    String userID;

    private final String TAG = "flow_MyProfileListingFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Start");

        arrayList.clear();
        putDataIntoArrayList();

        Log.d(TAG, "onCreateView: End");
        return inflater.inflate(R.layout.fragment_others_profile_listing, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: Start");
        super.onViewCreated(view, savedInstanceState);

        // Set RecycleView
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.othersProfileListingRecyclerView);
        CustomAdapter myListAdapter = new CustomAdapter();
        mRecyclerView.setAdapter(myListAdapter);

        // Set mSwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.othersProfileListingRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(()->{
            arrayList.clear();
            mSwipeRefreshLayout.setRefreshing(true);
            putDataIntoArrayList();
            myListAdapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        });
    }

    private void putDataIntoArrayList() {
        //<editor-fold desc="Put data into arrayList.">
            GlobalVariable gv = (GlobalVariable) getActivity().getApplicationContext();
        if(!gotWatchedUsrID) {
            userID = gv.getWatchedUsrID();
            gotWatchedUsrID = true;
        }
            SQLHandler s = new SQLHandler();
            ResultSet rCarCard;
            try {
                rCarCard = s.sqlSelect("SELECT Car_id, Car_brand, Car_model, Car_price FROM CAR WHERE Car_owner_id = " + userID);
                rCarCard.first();

                // Put cars data into arrayList
                do {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("CarID", rCarCard.getString("Car_id"));
                    hashMap.put("carImg", s.sqlCarImage(Integer.parseInt(rCarCard.getString(1))).get(0));
                    hashMap.put("carBrand", rCarCard.getString("Car_brand"));
                    hashMap.put("carModel", rCarCard.getString("Car_model"));
                    hashMap.put("carPrice", rCarCard.getString("Car_price"));
                    arrayList.add(hashMap);
                } while (rCarCard.next());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        //</editor-fold>
    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView carImgView;
            private final TextView carBrandView;
            private final TextView carModelView;
            private final TextView carPriceView;
            private final View mView;

            public ViewHolder(View view) {
                super(view);
                // Step 1: Initialize all the things inside the card. And create its get method.
                carImgView = (ImageView) view.findViewById(R.id.car_card_image);
                carBrandView = (TextView) view.findViewById(R.id.car_card_title);
                carModelView = (TextView) view.findViewById(R.id.car_card_subtitle);
                carPriceView = (TextView) view.findViewById(R.id.car_card_price);
                mView = view;
            }
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Step 2: Change the view to your card view.
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.shr_car_card, viewGroup, false);

            return new ViewHolder(view);
        }

        // Step 3: Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            String carID = arrayList.get(position).get("CarID");
            Picasso.get().load(arrayList.get(position).get("carImg")).into(viewHolder.carImgView);
            viewHolder.carBrandView.setText(arrayList.get(position).get("carBrand"));
            viewHolder.carModelView.setText(arrayList.get(position).get("carModel"));
            String carPriceStr = "$" + arrayList.get(position).get("carPrice") + "/day";
            viewHolder.carPriceView.setText(carPriceStr);

            // When card on click
            viewHolder.mView.setOnClickListener((v) -> {
                Intent i = new Intent(getActivity(), itemListingActivity.class);
                i.putExtra("carID",carID);
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