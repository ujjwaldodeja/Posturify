package com.example.posturfiy.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.posturfiy.R;
import com.example.posturfiy.databinding.FragmentHomeBinding;
import com.example.posturfiy.ui.database.Place;
import com.example.posturfiy.ui.database.PlaceAdapter;
import com.example.posturfiy.ui.database.SQLiteManager;
import com.example.posturfiy.ui.home.HomeViewModel;

public class SettingsActivity extends AppCompatActivity {
    private FragmentHomeBinding binding;
    private ListView placeListView;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        initWidgets();
        setPlaceAdapter();
        loadFromDBToMemory();
        setOnClickListener();

        return root;
    }

    private void setOnClickListener() {
        placeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Place selectedPlace = (Place) placeListView.getItemAtPosition(position);
                Intent editPlaceIntent = new Intent(getApplicationContext(), SettingsActivityEdit.class);
                editPlaceIntent.putExtra(Place.PLACE_EDIT_EXTRA, selectedPlace.getId());
                startActivity(editPlaceIntent);
            }
        });
    }

    private void loadFromDBToMemory() {
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(getApplicationContext());
        sqLiteManager.populateRecordListArray();
    }

    private void initWidgets() {
        placeListView = root.findViewById(R.id.placeListView);
    }

    private void setPlaceAdapter() {
        PlaceAdapter placeAdapter = new PlaceAdapter(getApplicationContext(), Place.arrayList);
        placeListView.setAdapter(placeAdapter);
    }

    public void newPlace(View view) {
        Intent intent = new Intent(this, SettingsActivityEdit.class);
        startActivity(intent);
    }

}
