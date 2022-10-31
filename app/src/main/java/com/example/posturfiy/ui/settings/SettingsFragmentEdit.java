package com.example.posturfiy.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.posturfiy.R;
import com.example.posturfiy.ui.Map.MapController;
import com.example.posturfiy.ui.database.SQLiteManager;
import com.example.posturfiy.ui.database.place.Place;
import com.google.android.gms.maps.model.LatLng;

public class SettingsFragmentEdit extends AppCompatActivity {

//    private FragmentSettingsEditBinding binding;
//    private View root;
    private EditText nameEditText, coordinatesEditText;
    private Place selectedPlace;
    Button deleteButton;
    Button saveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings_edit);

        initWidgets();

        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(getApplicationContext());

                String name = String.valueOf(nameEditText.getText());
                String address = String.valueOf(coordinatesEditText.getText());
                String latitude = "";
                String longitude = "";

                LatLng place = MapController.getLocationFromAddress(getApplicationContext(), address);
                if (place != null) {
                    latitude = place.latitude + "";
                    longitude = place.longitude + "";
                }

                if (selectedPlace == null) {
                    int id = Place.arrayList.size();
                    Place newPlace = new Place(id, name, latitude, longitude);
                    Place.arrayList.add(newPlace);
                    sqLiteManager.addPlaceToDatabase(newPlace);
                    finish();
                } else {
                    selectedPlace.setName(name);
                    selectedPlace.setLatitude(latitude);
                    selectedPlace.setLongitude(longitude);
                    sqLiteManager.updatePlaceInDB(selectedPlace);
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(getApplicationContext());
                sqLiteManager.deletePlaceFromDB(selectedPlace.getId());
                Place.removeById(selectedPlace.getId());
                finish();
            }
        });

        checkForEditPlace();
    }

//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        SettingsViewModel homeViewModel =
//                new ViewModelProvider(this).get(SettingsViewModel.class);
//
//        binding = FragmentSettingsEditBinding.inflate(inflater, container, false);
//        root = binding.getRoot();

//        Button button = (Button) root.findViewById(R.id.saveButton);
//        button.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(getActivity());
//
//                String name = String.valueOf(nameEditText.getText());
//                String coords = String.valueOf(coordinatesEditText.getText());
//
//                if (selectedPlace == null) {
//                    int id = Place.arrayList.size();
//                    Place newPlace = new Place(id, name, coords);
//                    Place.arrayList.add(newPlace);
//                    sqLiteManager.addPlaceToDatabase(newPlace);
//                    getActivity().finish();
//                } else {
//                    selectedPlace.setName(name);
//                    selectedPlace.setLatitude(coords);
//                    sqLiteManager.updatePlaceInDB(selectedPlace);
//                }
//            }
//        });
//
//        initWidgets();
//        checkForEditPlace();
//        return root;
//    }

    private void checkForEditPlace() {
        Intent previousIntent = getIntent();
        int passedPlaceId = previousIntent.getIntExtra(Place.PLACE_EDIT_EXTRA, -1);
        selectedPlace = Place.getPlaceForId(passedPlaceId);

        if (selectedPlace != null) {
            nameEditText.setText(selectedPlace.getName());
            String address = MapController.getStringAddressFromLatLon(
                    getApplicationContext(),
                    selectedPlace.getLatitude(),
                    selectedPlace.getLongitude());

            coordinatesEditText.setText(address);
        } else {
            deleteButton.setVisibility(View.INVISIBLE);
        }
    }

    private void initWidgets() {
        nameEditText = findViewById(R.id.nameEditText);
        coordinatesEditText = findViewById(R.id.coordinatesEditText);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        saveButton = (Button) findViewById(R.id.saveButton);
    }
}
