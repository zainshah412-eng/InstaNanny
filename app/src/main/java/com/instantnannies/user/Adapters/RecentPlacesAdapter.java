package com.instantnannies.user.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.instantnannies.user.Activities.AddressActivity;
import com.instantnannies.user.Models.RecentAddressData;
import com.instantnannies.user.R;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class RecentPlacesAdapter extends RecyclerView.Adapter<RecentPlacesAdapter.MyViewHolder> {

    ArrayList<RecentAddressData> lstRecentList;
    AddressActivity addressActivity;

    public RecentPlacesAdapter( AddressActivity addressActivity ,ArrayList<RecentAddressData> lstRecentList) {
        this.addressActivity = addressActivity;
        this.lstRecentList = lstRecentList;
    }

    @Override
    public RecentPlacesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.autocomplete_row, parent, false);
        return new RecentPlacesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecentPlacesAdapter.MyViewHolder holder, int position) {
            /*String[] name = lstRecentList.get(position).address.split(",");
            if (name.length > 0) {
                holder.name.setText(name[0]);
            } else {
                holder.name.setText(lstRecentList.get(position).address);
            }*/

        holder.name.setText(lstRecentList.get(position).recentLocationName);
        holder.location.setText(lstRecentList.get(position).address);

        holder.imgRecent.setImageResource(R.drawable.recent_search);

        holder.lnrLocation.setTag(position);

        holder.lnrLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = Integer.parseInt(view.getTag().toString());

                Intent intent = new Intent();
                intent.putExtra("address",lstRecentList.get(position).address);
                intent.putExtra("address_name",lstRecentList.get(position).recentLocationName);
                intent.putExtra("lat",lstRecentList.get(position).latitude+"");
                intent.putExtra("lng",lstRecentList.get(position).longitude+"");
                addressActivity.setResult(RESULT_OK, intent);
                addressActivity.finish();



            }
        });
    }

    @Override
    public int getItemCount() {
        return lstRecentList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, location;

        LinearLayout lnrLocation;

        ImageView imgRecent;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.place_name);
            location =  itemView.findViewById(R.id.place_detail);
            lnrLocation = (LinearLayout) itemView.findViewById(R.id.lnrLocation);
            imgRecent = (ImageView) itemView.findViewById(R.id.imgRecent);
        }
    }
}
