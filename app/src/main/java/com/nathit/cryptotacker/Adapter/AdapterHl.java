package com.nathit.cryptotacker.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nathit.cryptotacker.Model.ModelHl;
import com.nathit.cryptotacker.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterHl extends RecyclerView.Adapter<AdapterHl.MyHolder> {

    Context context;
    List<ModelHl> HlList;

    public AdapterHl(Context context, List<ModelHl> hlList) {
        this.context = context;
        HlList = hlList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_history, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String ip = HlList.get(position).getIp();
        String time = HlList.get(position).getTime();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(time));
        String pTime = DateFormat.format("dd/MM/yyyy HH:mm:ss", calendar).toString();

        holder.ipTv.setText("IP Address  :  " + ip); //show Ip address User
        holder.timeTv.setText("Login Time :  " + pTime); // show Login Time User
    }

    @Override
    public int getItemCount() {
        return HlList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        TextView ipTv, timeTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            ipTv = itemView.findViewById(R.id.ipTv);
            timeTv = itemView.findViewById(R.id.timeTv);
        }
    }
}
