package com.example.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class DealAdaptor extends RecyclerView.Adapter<DealAdaptor.DealViewHolder> {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;
    private ImageView imageDeal;

    ArrayList<TravelDeal> deals;

    //    public Constructor
    public DealAdaptor() {

        // Firebase Instance
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;

//        Connection to database
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        deals = FirebaseUtil.mDeals;

        //        ChildEventListener
        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded( DataSnapshot dataSnapshot, @Nullable String s) {

                TravelDeal td = dataSnapshot.getValue(TravelDeal.class);
                Log.d("Deal: ", td.getTitle());
                td.setId(dataSnapshot.getKey());
                deals.add(td);
                notifyItemInserted(deals.size() - 1);
            }

            @Override
            public void onChildChanged( DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved( DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved( DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        };

//        adding ChildEventListener to the database Reference
        mDatabaseReference.addChildEventListener(mChildListener);
    }



    @Override
    public DealViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.rv_row, parent, false);
        return new DealViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder( DealViewHolder holder, int position) {
//      get the Position of a deal and bind it to the holder
        TravelDeal deal = deals.get(position);
        holder.bind(deal);
    }


    @Override
    public int getItemCount() {
//        returns the size of the area
        return deals.size();
    }

    //    ViewHolder Class
    public class DealViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{


        TextView tvTitle;
        TextView tvPrice;
        TextView tvDesc;


        public DealViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            itemView.setOnClickListener(this);
            imageDeal = itemView.findViewById(R.id.imageDeal);
        }

        public void bind(TravelDeal deal) {

//            this put the title set into the constructor
            tvTitle.setText(deal.getTitle());
//            this put the price set into the constructor
            tvPrice.setText(deal.getPrice());
//            this put the description into the constructor
            tvDesc.setText(deal.getDescription());
            showImage(deal.getImageUrl());

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            TravelDeal selectedDeal = deals.get(position);
            Intent intent = new Intent(view.getContext(), MainActivity.class);
//            this pass the travelDeal to the MainActivity using putExtra
            intent.putExtra("Deal", selectedDeal);
//            start to call deal Activity on the current view
            view.getContext().startActivity(intent);
        }

        private void showImage(String url){
            if (url != null && url.isEmpty() == false){
                Picasso.with(imageDeal.getContext()).load(url).resize(160, 160).centerCrop().into(imageDeal);
            }
        }
    }

}