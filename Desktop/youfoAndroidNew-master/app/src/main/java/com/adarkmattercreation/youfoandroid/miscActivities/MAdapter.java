package com.adarkmattercreation.youfoandroid.miscActivities;


import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adarkmattercreation.youfoandroid.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Danilo on 1/17/18.
 */

class MAdapter extends RecyclerView.Adapter<MAdapter.ViewHolder> {
    public ArrayList<Mailmessage> message;
    HashMap<String,ArrayList<Object>> map;
    private  Context context;

    public MAdapter(ArrayList mailmessage, HashMap<String, ArrayList<Object>> hashMap, Context context) {
        this.context = context;
        message = mailmessage;
        map = hashMap;
    }

    @Override
    public MAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.inboxrow, parent, false);
        ViewHolder holder = new ViewHolder(contactView);
        return holder;

    }

    @Override
    public void onBindViewHolder(MAdapter.ViewHolder holder, int position) {
        Mailmessage mailmessage = message.get(position);

        ArrayList<Object> list =    map.get(mailmessage.getUsertalking());
        if(list != null) {
            String count = String.valueOf(list.size());

            holder.constraintLayout.setBackgroundResource(R.color.colorAccent);
            holder.unreadcount.setText("unread message "+ count);
        }

        holder.messagetext.setText(mailmessage.getMessage());
        holder.textname.setText(mailmessage.getUsername());
        holder.time.setText(mailmessage.getTime());
        Glide.with(holder.imageView.getContext()).load(mailmessage.getProfilepicture()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return message.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView textname;
        public TextView messagetext;
        public TextView time;
        public TextView unreadcount;
        public ConstraintLayout constraintLayout;

        public ViewHolder(View itemView) {

            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textname = (TextView) itemView.findViewById(R.id.textView2);
            messagetext = (TextView) itemView.findViewById(R.id.tv_long);
            time = (TextView) itemView.findViewById(R.id.textView4);
            unreadcount = (TextView) itemView.findViewById(R.id.textView7);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.background);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,MessagesFrag.class);
                    intent.putExtra("id",message.get(getAdapterPosition()).getUsertalking());
                    intent.putExtra("profile",message.get(getAdapterPosition()).getProfilepicture());
                    intent.putExtra("name",message.get(getAdapterPosition()).getUsername());
                    context.startActivity(intent);



                }
            });

        }

    }
    }

