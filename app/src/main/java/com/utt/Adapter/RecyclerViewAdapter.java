package com.utt.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.utt.model.Notify;
import com.utt.potholes.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    //implements Filterable

    private List<Notify> dataList;
    private List<Notify> dataListFiltered;
    private RecyclerViewAdapter recyclerViewAdapter;
    Context context;
    private Boolean aBoolean = true;

    public RecyclerViewAdapter(List<Notify> data, Context context) {
        this.dataList = data;
        this.context = context;
        this.dataListFiltered = data;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_listview_notify, parent, false);
        return new RecyclerViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        holder.name.setText( dataList.get(position).getName());
        holder.date.setText(dataList.get(position).getDate());
        holder.message.setText( dataList.get(position).getMessage());

//        if (dataList.get(position).getHasbusstop_home() == aBoolean){
//            holder.imageView.setImageResource(R.drawable.success);
//        }else {
//            holder.imageView.setImageResource(R.drawable.success);
//        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
    private static OnItemClickListener listener;

//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence charSequence) {
//                String charString = charSequence.toString();
//                if (charString.isEmpty()) {
//                    dataListFiltered = dataList;
//                } else {
//                    List<Data> filteredList = new ArrayList<>();
//                    for (Data row : dataList) {
//                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getName().contains(charSequence)) {
//                            filteredList.add(row);
//                        }
//                    }
//                    dataListFiltered = filteredList;
//                }
//                FilterResults filterResults = new FilterResults();
//                filterResults.values = dataListFiltered;
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//                dataList.clear();
//                dataList = (ArrayList<Data>) filterResults.values;
//                dataList.addAll(dataListFiltered);
//                Log.d("aaa",filterResults+" ");
//
//
//                // refresh the list with filtered DataPothole
//                notifyDataSetChanged();
//            }
//        };
//    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView date;
        TextView message;
        public RecyclerViewHolder(final View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.txtNameofNotify);
            date = (TextView) itemView.findViewById(R.id.txtDateofNotify);
            message = (TextView) itemView.findViewById(R.id.txtMessageNotify);

        }
    }
}
