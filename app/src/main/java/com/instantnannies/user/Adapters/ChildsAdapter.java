package com.instantnannies.user.Adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.instantnannies.user.Fragments.NanniesBooking;
import com.instantnannies.user.Models.Children;

import com.instantnannies.user.R;

import java.util.List;

public class ChildsAdapter extends RecyclerView.Adapter<ChildsAdapter.MyViewHolder> {

    private Context context;
    List<Children> childrenList;
    public int currPosition = 0;
    NanniesBooking fragment;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView child_num;
        EditText age_of_child;
        public MyViewHolder(View view) {
            super(view);
             child_num = view.findViewById(R.id.child_num);
             age_of_child = view.findViewById(R.id.age_of_child);

        }
    }

    public ChildsAdapter(Context context, List<Children> childrenList) {
        this.childrenList = childrenList;
        this.context = context;

    }

    @Override
    public ChildsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.childs_li, parent, false);
        context = parent.getContext();
        return new ChildsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ChildsAdapter.MyViewHolder holder,final int position) {

        holder.child_num.setText("Child "+childrenList.get(position).getName());
        if(childrenList.get(position).getAge()!=null||!childrenList.get(position).getAge().equalsIgnoreCase("")) {
            holder.age_of_child.setText(childrenList.get(position).getAge());
        }
        holder.age_of_child.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                childrenList.get(position).setAge(editable.toString());
            }
        });


    }

    @Override
    public int getItemCount() {
        return childrenList.size();
    }




}