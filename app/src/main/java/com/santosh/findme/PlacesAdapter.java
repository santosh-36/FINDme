package com.santosh.findme;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    public List<Places> placesList;
    Context context;


    public PlacesAdapter(List<Places> placesList) {
        this.placesList = placesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        context = viewGroup.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {
        holder.tv1.setText(placesList.get(i).getTitle());
        holder.tv2.setText(placesList.get(i).getAddress());
        holder.tv3.setText(placesList.get(i).getDist());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MapsActivity.class)
                        .putExtra("longitude", placesList.get(i).getLongitude())
                        .putExtra("latitude", placesList.get(i).getLatitude());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv1, tv2, tv3;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            tv1 = view.findViewById(R.id.item_title);
            tv2 = view.findViewById(R.id.item_address);
            tv3 = view.findViewById(R.id.item_distance);
        }
    }

    public void updateList(List<Places> newlist){
        if (newlist.size() == 0){
            Toast.makeText(context, "No Results Found!!", Toast.LENGTH_SHORT).show();
        }
        placesList = new ArrayList<>();
        placesList.addAll(newlist);
        notifyDataSetChanged();
    }
}