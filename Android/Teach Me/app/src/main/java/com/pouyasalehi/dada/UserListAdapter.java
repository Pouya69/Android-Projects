package com.pouyasalehi.dada;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {

    ArrayList<UserObject> userList;

    public UserListAdapter(ArrayList<UserObject> userList){
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        UserListViewHolder rcv = new UserListViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final UserListViewHolder holder, final int position) {
        holder.memail.setVisibility(View.GONE);
        holder.mName.setText(userList.get(position).getName());
        holder.memail.setText(userList.get(position).getemail());
        //emaulshow
        holder.showemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.showemail.setVisibility(View.GONE);
                holder.memail.setVisibility(View.VISIBLE);
            }
        });

        holder.mAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userList.get(holder.getAdapterPosition()).setSelected(isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }



    class UserListViewHolder extends RecyclerView.ViewHolder{
        TextView mName, memail;
        LinearLayout mLayout;
        CheckBox mAdd;
        Button showemail;
        UserListViewHolder(View view){
            super(view);
            mName = view.findViewById(R.id.name);
            memail = view.findViewById(R.id.email3);
            mAdd = view.findViewById(R.id.add);
            mLayout = view.findViewById(R.id.layout);
            showemail=view.findViewById(R.id.showemailbtn);
        }
    }
}