package com.daveholman.geartracker.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daveholman.geartracker.R;
import com.daveholman.geartracker.models.GearData;


public class GearDataViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView manufacturerView;
    public ImageView photoView;
    public TextView yearView;

    public GearDataViewHolder(View itemView) {
        super(itemView);

        titleView = (TextView) itemView.findViewById(R.id.gear_title);
        manufacturerView = (TextView) itemView.findViewById(R.id.gear_manufacturer);
        photoView = (ImageView) itemView.findViewById(R.id.gear_photo);
        yearView = (TextView) itemView.findViewById(R.id.gear_year);
    }

    public void bindToPost(GearData gearData, View.OnClickListener starClickListener) {
        titleView.setText(gearData.getTitle());
        manufacturerView.setText(gearData.getManufacturer());
        yearView.setText(String.valueOf(gearData.getYear()));
        //photoView.setText(gearData.getImageUrl());

        //starView.setOnClickListener(starClickListener);
    }
}
