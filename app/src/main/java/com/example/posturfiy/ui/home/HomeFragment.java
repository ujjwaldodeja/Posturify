package com.example.posturfiy.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.posturfiy.R;
import com.example.posturfiy.databinding.FragmentHomeBinding;
import com.example.posturfiy.ui.database.SQLiteManager;
import com.example.posturfiy.ui.database.place.Place;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final int CAPTURE_IMAGE = 0;
    private FragmentHomeBinding binding;
    private ImageView imageView;
    private Context mContext;


    private Spinner spinner;
    public static String nameChosenByUser;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(getContext());
        sqLiteManager.populatePlaceListArray();
        sqLiteManager.populateRecordListArray();

        ArrayAdapter<String> adapter = null;

        spinner = (Spinner) root.findViewById(R.id.spinner);
        if (Place.getPlacesNames().size() == 0) {
            adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1);
        } else {
            adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1,
                    Place.getPlacesNames());
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Button submit = binding.getRoot().findViewById(R.id.submit_button);
        submit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            binding.buttonStat.setVisibility(View.VISIBLE);
            nameChosenByUser = spinner.getSelectedItem().toString();
            System.out.println("=================== " + nameChosenByUser + " ===================");
        }
    });

        //imageView = (ImageView) root.findViewById(R.id.my_avatar_imageview);
        binding.buttonStat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Picture taken");
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                startActivity(intent);
            }
        });


        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        nameChosenByUser = spinner.getSelectedItem().toString();
        System.out.println(nameChosenByUser + "====================================");
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}