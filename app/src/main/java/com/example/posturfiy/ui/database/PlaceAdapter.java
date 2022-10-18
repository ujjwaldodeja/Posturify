package com.example.posturfiy.ui.database;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.posturfiy.R;

import java.util.List;

public class PlaceAdapter extends ArrayAdapter<Place> {

    public PlaceAdapter(@NonNull Context context, List<Place> places) {
        super(context, 0, places);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Place place = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.place_cell, parent, false);
        }
        TextView name = convertView.findViewById(R.id.cellName);
        TextView coordinates = convertView.findViewById(R.id.cellCoordinates);

        name.setText(place.getName());
        coordinates.setText(place.getCoordinates());
        return convertView;
    }
}
