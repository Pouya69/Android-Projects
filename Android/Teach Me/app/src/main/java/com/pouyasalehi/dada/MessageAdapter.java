package com.pouyasalehi.dada;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.picasso.OkHttp3Downloader;

import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    ArrayList<MessageObject> messageList;
    private  String usernameholder,emailholder;
    private Context context;
    private int usertypeholder;
    ImageView img;
    private DatabaseManager mDatabase;
    Things j=new Things();
    public MessageAdapter(ArrayList<MessageObject> messageList,Context context){
        this.messageList = messageList;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        MessageViewHolder rcv = new MessageViewHolder(layoutView);
        rcv.setIsRecyclable(false);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position, List<Object> payloads) {

        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            Bundle o = (Bundle) payloads.get(0);
            for (String key : o.keySet()) {
                if (key.equals("media")) {
                    holder.setIsRecyclable(false);
                    holder.mMessage.setText(messageList.get(position).getMessage());
                    holder.mSender.setText(messageList.get(position).getSenderId());
                    Picasso.Builder builder = new Picasso.Builder(context);
                    builder.downloader(new OkHttp3Downloader(context));
                    builder.build().load(messageList.get(position).getProfilePic())
                            .placeholder((R.drawable.image))
                            .into(holder.imgprofile);
                    if(messageList.get(position).getSender()){
                        holder.msg3.setBackgroundResource(R.drawable.me);
                        holder.msg1.setGravity(Gravity.START);
                        holder.msg3.setGravity(Gravity.START);
                        holder.mMessage.setGravity(Gravity.START);
                        holder.msg3.setPadding(0,1,0,0);
                    }

                    else{
                        holder.msg3.setBackgroundResource(R.drawable.others);
                        holder.msg1.setGravity(Gravity.END);
                        holder.msg3.setGravity(Gravity.END);
                        holder.msg3.setPadding(0,1,0,0);
                        holder.mMessage.setGravity(Gravity.END);
                    }

                    if(!messageList.get(holder.getAdapterPosition()).getMediaUrlList().isEmpty()){
                        holder.mViewMedia.setVisibility(View.VISIBLE);
                        Picasso.Builder builder2 = new Picasso.Builder(context);
                        builder2.downloader(new OkHttp3Downloader(context));
                        builder2.build().load(messageList.get(holder.getAdapterPosition()).getMediaUrlList().get(0))
                                .placeholder((R.drawable.image))
                                .error(R.drawable.ic_launcher_background)
                                .into(holder.mViewMedia);
                       Handler handler = new Handler(); // write in onCreate function

//below piece of code is written in function of class that extends from AsyncTask

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                notifyItemChanged(position);
                            }
                        });


                    }


                    holder.mViewMedia.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new ImageViewer.Builder(v.getContext(), messageList.get(holder.getAdapterPosition()).getMediaUrlList())
                                    .setStartPosition(0)
                                    .show();
                        }
                    });
                    holder.imgprofile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                                Intent i=new Intent(v.getContext(),ProfileAcitivty.class);
                                i.putExtra("USER_NAME",messageList.get(holder.getAdapterPosition()).getSenderId());
                                i.putExtra("USER_UID",messageList.get(holder.getAdapterPosition()).getUid());
                                i.putExtra("PROFILE_IMAGE",messageList.get(holder.getAdapterPosition()).getProfilePic());
                                v.getContext().startActivity(i);

                        }
                    });
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        holder.mMessage.setText(messageList.get(position).getMessage());
        holder.mSender.setText(messageList.get(position).getSenderId());
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OkHttp3Downloader(context));
        builder.build().load(messageList.get(position).getProfilePic())
                .placeholder((R.drawable.image))
                .into(holder.imgprofile);
        if(messageList.get(position).getSender()){
            holder.msg3.setBackgroundResource(R.drawable.me);
            holder.msg1.setGravity(Gravity.START);
            holder.msg3.setGravity(Gravity.START);
            holder.mMessage.setGravity(Gravity.START);
            holder.msg3.setPadding(0,1,0,0);
        }

        else{
            holder.msg3.setBackgroundResource(R.drawable.others);
            holder.msg1.setGravity(Gravity.END);
            holder.msg3.setGravity(Gravity.END);
            holder.msg3.setPadding(0,1,0,0);
            holder.mMessage.setGravity(Gravity.END);
        }

        if(!messageList.get(holder.getAdapterPosition()).getMediaUrlList().isEmpty()){
            holder.mViewMedia.setVisibility(View.VISIBLE);
            Picasso.Builder builder2 = new Picasso.Builder(context);
            builder2.downloader(new OkHttp3Downloader(context));
            builder2.build().load(messageList.get(holder.getAdapterPosition()).getMediaUrlList().get(0))
                    .placeholder((R.drawable.image))
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.mViewMedia);


        }


        holder.mViewMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageViewer.Builder(v.getContext(), messageList.get(holder.getAdapterPosition()).getMediaUrlList())
                        .setStartPosition(0)
                        .show();
            }
        });
        holder.imgprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent i=new Intent(v.getContext(),ProfileAcitivty.class);
                    i.putExtra("USER_NAME",messageList.get(holder.getAdapterPosition()).getSenderId());
                    i.putExtra("USER_UID",messageList.get(holder.getAdapterPosition()).getUid());
                    i.putExtra("PROFILE_IMAGE",messageList.get(holder.getAdapterPosition()).getProfilePic());
                    v.getContext().startActivity(i);

            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }





    class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView    mMessage,
                mSender;
        ImageView imgprofile,mViewMedia;
        LinearLayout mLayout,msg1,msg3,imageLayout;
        MessageViewHolder(View view){
            super(view);
            mLayout = view.findViewById(R.id.layout);
            msg1=view.findViewById(R.id.message1);
            imageLayout=view.findViewById(R.id.layoutImage);
            imgprofile=view.findViewById(R.id.picture);
            msg3=view.findViewById(R.id.msg2);
            mMessage = view.findViewById(R.id.message);
            mViewMedia=view.findViewById(R.id.imageViewPick);
            mSender = view.findViewById(R.id.sender);

        }
    }
    public void setData(ArrayList<MessageObject> newData) {

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffUtilCallBack(newData, messageList));
        diffResult.dispatchUpdatesTo(this);
        messageList.clear();
        this.messageList.addAll(newData);
    }
}
