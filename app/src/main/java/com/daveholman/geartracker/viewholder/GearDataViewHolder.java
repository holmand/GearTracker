package com.daveholman.geartracker.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daveholman.geartracker.R;
import com.daveholman.geartracker.models.GearData;


public class GearDataViewHolder extends RecyclerView.ViewHolder {

    public TextView nameView;
    public TextView manufacturerView;
    public ImageView photoView;
    public TextView yearView;

    public GearDataViewHolder(View itemView) {
        super(itemView);

        nameView = (TextView) itemView.findViewById(R.id.gear_name);
        manufacturerView = (TextView) itemView.findViewById(R.id.gear_manufacturer);
        photoView = (ImageView) itemView.findViewById(R.id.gear_photo);
        yearView = (TextView) itemView.findViewById(R.id.gear_year);
    }

    public void bindToPost(GearData gearData, View.OnClickListener starClickListener) {
        nameView.setText(gearData.getName());
        manufacturerView.setText(gearData.getManufacturer());
        yearView.setText(String.valueOf(gearData.getYear()));
        //photoView.setText(gearData.getImageUrl());

        //starView.setOnClickListener(starClickListener);
    }
}
