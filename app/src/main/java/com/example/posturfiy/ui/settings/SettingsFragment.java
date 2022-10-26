package com.example.posturfiy.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.posturfiy.R;
import com.example.posturfiy.databinding.FragmentSettingsBinding;
import com.example.posturfiy.ui.database.place.Place;
import com.example.posturfiy.ui.database.place.PlaceAdapter;
import com.example.posturfiy.ui.database.SQLiteManager;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private ListView placeListView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button button = (Button) root.findViewById(R.id.addNewPlace);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), SettingsFragmentEdit.class);
                startActivity(intent);
            }
        });

        initWidgets(root);
        setPlaceAdapter();
        loadFromDBToMemory();
        setOnClickListener();

        System.out.println("======================");
        Place.arrayList.forEach(e -> {
            System.out.println(e.getId() + ", " + e.getName() + ", " + e.getLatitude() + ", " + e.getLongitude() + "\n");
        });
        System.out.println("======================");

        return root;
    }

    private void setOnClickListener() {
        placeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Place selectedPlace = (Place) placeListView.getItemAtPosition(position);
                Intent editPlaceIntent = new Intent(getActivity(), SettingsFragmentEdit.class);
                editPlaceIntent.putExtra(Place.PLACE_EDIT_EXTRA, selectedPlace.getId());
                startActivity(editPlaceIntent);
            }
        });
    }

    private void loadFromDBToMemory() {
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(getActivity());
        sqLiteManager.populatePlaceListArray();
    }

    private void initWidgets(View view) {
        placeListView = (ListView) view.findViewById(R.id.placeListView);
    }

    private void setPlaceAdapter() {
        PlaceAdapter placeAdapter = new PlaceAdapter(getActivity(), Place.arrayList);
        placeListView.setAdapter(placeAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        setPlaceAdapter();
    }
}
