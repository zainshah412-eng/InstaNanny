package com.instantnannies.user.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.instantnannies.user.CustomFont.CustomTextView;
import com.instantnannies.user.Models.CardDetails;
import com.instantnannies.user.R;

import java.util.List;

public class NewPaymentListAdapter extends ArrayAdapter<CardDetails> {

    List<CardDetails> list;

    Context context;

    Activity activity;

    public NewPaymentListAdapter(Context context, List<CardDetails> list, Activity activity){
        super(context, R.layout.payment_list_item, list);
        this.context=context;
        this.list=list;
        this.activity = activity;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.payment_list_item, parent, false);

        ImageView paymentTypeImg =(ImageView) itemView.findViewById(R.id.paymentTypeImg);

        CustomTextView cardNumber =(CustomTextView) itemView.findViewById(R.id.cardNumber);


        try {

            if(list.get(position).getBrand().equalsIgnoreCase("MASTER")){
                paymentTypeImg.setImageResource(R.drawable.credit_card);
            }else if(list.get(position).getBrand().equalsIgnoreCase("MASTRO")){
                paymentTypeImg.setImageResource(R.drawable.visa_payment_icon);
            }else if(list.get(position).getBrand().equalsIgnoreCase("Visa")){
                paymentTypeImg.setImageResource(R.drawable.visa);
            }
            cardNumber.setText("xxxx - xxxx - xxxx - "+list.get(position).getLast_four());

        } catch (Exception e) {

            e.printStackTrace();

        }
        return itemView;
    }
}
