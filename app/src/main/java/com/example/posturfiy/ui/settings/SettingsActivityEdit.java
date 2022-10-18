package com.example.posturfiy.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.posturfiy.R;
import com.example.posturfiy.databinding.FragmentHomeBinding;
import com.example.posturfiy.ui.database.Place;
import com.example.posturfiy.ui.database.SQLiteManager;

public class SettingsActivityEdit extends AppCompatActivity {

    private FragmentHomeBinding binding;
    private View root;
    private EditText nameEditText, coordinatesEditText;
    private Place selectedPlace;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel homeViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        initWidgets();
        checkForEditPlace();
        return root;
    }

    private void checkForEditPlace() {
        Intent previousIntent = this.getIntent();
        int passedPlaceId = previousIntent.getIntExtra(Place.PLACE_EDIT_EXTRA, -1);
        selectedPlace = Place.getPlaceForId(passedPlaceId);

        if (selectedPlace != null) {
            nameEditText.setText(selectedPlace.getName());
            coordinatesEditText.setText(selectedPlace.getCoordinates());
        }
    }

    private void initWidgets() {
        nameEditText = root.findViewById(R.id.nameEditText);
        coordinatesEditText = root.findViewById(R.id.coordinatesEditText);
    }

    public void savePlace(View view) {
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);

        String name = String.valueOf(nameEditText.getText());
        String coords = String.valueOf(coordinatesEditText.getText());

        if (selectedPlace == null) {
            int id = Place.arrayList.size();
            Place newPlace = new Place(id, name, coords);
            Place.arrayList.add(newPlace);
            sqLiteManager.addPlaceToDatabase(newPlace);
            finish();
        } else {
            selectedPlace.setName(name);
            selectedPlace.setCoordinates(coords);
            sqLiteManager.updatePlaceInDB(selectedPlace);
        }


    }
}
