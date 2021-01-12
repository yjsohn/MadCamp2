package com.example.camp_proj1;

//리사이클러 뷰 하나에 들어갈 컨텐츠 설정해준은 거

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter3 extends RecyclerView.Adapter<RecyclerViewAdapter3.MyViewHolder> {
    private ArrayList<MoneyInfo> mPersons;
    private LayoutInflater mInflate;
    private Context mContext;


    public RecyclerViewAdapter3(Context context, ArrayList<MoneyInfo> persons) {
        this.mContext = context;
        this.mInflate = LayoutInflater.from(context);
        this.mPersons = persons;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView writer,participants, date, money, account;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public MyViewHolder(View itemView) {
            super(itemView);

            writer = itemView.findViewById(R.id.writer_name);
            participants =  itemView.findViewById(R.id.participant_name);
            date = itemView.findViewById(R.id.date);
            money = itemView.findViewById(R.id.dollar_amount);
            account = itemView.findViewById(R.id.account_string);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflate.inflate(R.layout.recyclerviewmoney, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter3.MyViewHolder holder, int position) {
        holder.writer.setText(mPersons.get(position).writer);
        holder.participants.setText(mPersons.get(position).participants.toString());
        holder.date.setText(mPersons.get(position).date);
        holder.money.setText(mPersons.get(position).money);
        holder.account.setText(mPersons.get(position).account);

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            //ArrayList<UserInfo> list = new ArrayList<UserInfo>();
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RecyclerViewClickActivity.class);
                intent.putExtra("writer",mPersons.get(position).writer);
                intent.putExtra("participants", mPersons.get(position).participants);
                intent.putExtra("date", mPersons.get(position).date);
                intent.putExtra("account", mPersons.get(position).account);
                intent.putExtra("money", mPersons.get(position).money);
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPersons.size();
    }

}
