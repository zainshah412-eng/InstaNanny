package com.instantnannies.user.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.instantnannies.user.Fragments.NanniesBooking;
import com.instantnannies.user.Models.Nannies;
import com.instantnannies.user.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NanniesAdapter extends RecyclerView.Adapter<NanniesAdapter.MyViewHolder> {

    private Context context;
    List<Nannies> nanniesList;
    public int currPosition = 0;
    NanniesBooking fragment;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        View view_;
        TextView nanniesDesc, price;
        ImageView imageView;
        RelativeLayout main_layout;
        public MyViewHolder(View view) {
            super(view);
            view_ = view.findViewById(R.id.view_);
            nanniesDesc = view.findViewById(R.id.nannies_desc);
            price = view.findViewById(R.id.price);
            imageView = view.findViewById(R.id.image);
            main_layout = view.findViewById(R.id.main_layout);

        }
    }

    public NanniesAdapter(Context context, List<Nannies> nanniesList, NanniesBooking fragment) {
      this.nanniesList = nanniesList;
      this.context = context;
      this.fragment = fragment;
    }

    @Override
    public NanniesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nannies_li, parent, false);
        context = parent.getContext();
        return new NanniesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final NanniesAdapter.MyViewHolder holder,final int position)
    {

        if (position == currPosition)
        {
            holder.view_.setVisibility(View.VISIBLE);
            holder.main_layout.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary_low_opacity));
        }
        else
        {
            holder.view_.setVisibility(View.INVISIBLE);
            holder.main_layout.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        holder.nanniesDesc.setText(nanniesList.get(position).getNanniesDesc());
        holder.price.setText("$"+nanniesList.get(position).getPrice()+"/hour");
        Picasso.get()
                .load(nanniesList.get(position).getChilds_image())
                .placeholder(R.drawable.user_profile)
                .error(R.drawable.user_profile)
                .into(holder.imageView);
        holder.main_layout.setTag(position);
        holder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currPosition = position;
                if (position == currPosition){
                    holder.main_layout.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary_low_opacity));
                    fragment.nannies_desc.setText(nanniesList.get(position).getNanniesDesc());
                    fragment.price.setText("$"+nanniesList.get(position).getPrice()+"/hour");
                    fragment.id_of_nany=nanniesList.get(position).getId();
                    Picasso.get()
                            .load(nanniesList.get(position).getChilds_image())
                            .placeholder(R.drawable.user_profile)
                            .error(R.drawable.user_profile)
                            .into(fragment.image);
                  //  fragment.image.setImageResource(nanniesList.get(position).getChilds_image());

                    fragment.nannies_rv.setVisibility(View.GONE);
                    fragment.nannies_cv.setVisibility(View.VISIBLE);
                    fragment.confirm_location.setVisibility(View.VISIBLE);
                }else{

                    holder.main_layout.setBackgroundColor(context.getResources().getColor(R.color.white));

                }

                notifyDataSetChanged();

            }
        });

    }

    @Override
    public int getItemCount() {
        return nanniesList.size();
    }




}
