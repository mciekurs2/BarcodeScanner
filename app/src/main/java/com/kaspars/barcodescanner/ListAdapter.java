package com.kaspars.barcodescanner;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.CoreViewHolder>{
    private ArrayList<BarcodeData> list;

    ListAdapter(ArrayList<BarcodeData> list){
        this.list = list;
    }

    @NonNull
    @Override
    public CoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_layout, parent, false);
        return new CoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoreViewHolder holder, int position) {
        BarcodeData data = list.get(position);
        holder.rawValue.setText("RawValue: " + data.getRawData());
        holder.type.setText("ValueType: " + data.getType());
        holder.timeAdded.setText(data.getTimeAdded());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class CoreViewHolder extends RecyclerView.ViewHolder {
        private TextView rawValue;
        private TextView type;
        private TextView timeAdded;
        CoreViewHolder(View itemView) {
            super(itemView);
            rawValue = itemView.findViewById(R.id.textView_rawValue);
            type = itemView.findViewById(R.id.textView_type);
            timeAdded = itemView.findViewById(R.id.textView_timeAdded);


        }
    }


}

