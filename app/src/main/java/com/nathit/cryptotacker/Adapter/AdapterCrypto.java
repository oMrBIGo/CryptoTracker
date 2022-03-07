package com.nathit.cryptotacker.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nathit.cryptotacker.Model.ModelCrypto;
import com.nathit.cryptotacker.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AdapterCrypto extends RecyclerView.Adapter<AdapterCrypto.ViewHolder> {

    private ArrayList<ModelCrypto> modelCryptos;
    private Context context;
    private static DecimalFormat df2 = new DecimalFormat("#.##");

    public AdapterCrypto(ArrayList<ModelCrypto> modelCryptos, Context context) {
        this.modelCryptos = modelCryptos;
        this.context = context;
    }

    public void filterList(ArrayList<ModelCrypto> filteredList) {
        modelCryptos = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdapterCrypto.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.crypto_rv_item, parent, false);
        return new AdapterCrypto.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCrypto.ViewHolder holder, int position) {
        ModelCrypto modelCrypto = modelCryptos.get(position);
        holder.currencyNameTv.setText(modelCrypto.getName());
        holder.symbolTv.setText(modelCrypto.getSymbol());
        holder.rateTv.setText("$ " + df2.format(modelCrypto.getPrice()));
    }

    @Override
    public int getItemCount() {
        return modelCryptos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView currencyNameTv, symbolTv, rateTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            currencyNameTv = itemView.findViewById(R.id.idTVCurrencyName);
            symbolTv = itemView.findViewById(R.id.idTVSymbol);
            rateTv = itemView.findViewById(R.id.idTVCurrencyRate);
        }
    }
}
