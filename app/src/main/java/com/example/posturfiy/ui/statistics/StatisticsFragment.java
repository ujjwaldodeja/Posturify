package com.example.posturfiy.ui.statistics;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.posturfiy.R;
import com.example.posturfiy.databinding.FragmentStatisticsBinding;
import com.example.posturfiy.ui.database.place.Place;
import com.example.posturfiy.ui.database.record.Record;
import com.example.posturfiy.ui.home.HomeFragment;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.List;


public class StatisticsFragment extends Fragment {
    // Create the object of TextView and PieChart class
    TextView tvStraight, tvLeft, tvRight;
    PieChart pieChart;

    private int straight;
    private int left;
    private  int right;

    private FragmentStatisticsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String placeName = HomeFragment.nameChosenByUser;
        if (placeName == null) {
            placeName = "";
        }
        int foreignID = 0;

        for (Place p : Place.arrayList) {
            if (p.getName().equals(placeName)) {
                foreignID = p.getId();
                break;
            }
        }

        List<Record> allRecords = new ArrayList<>(Record.recordsList);
        List<Record> filteredByName = new ArrayList<>();

        for (Record r : allRecords) {
            if (r.getForeign_id() == foreignID) {
                filteredByName.add(r);
            }
        }

        straight = 0;
        left = 0;
        right = 0;

        for (Record r : filteredByName) {
            if (r.getResult().equals("straight")) {
                straight++;
            }
            if (r.getResult().equals("left")) {
                left++;
            }
            if (r.getResult().equals("right")) {
                right++;
            }
        }

        // Link those objects with their respective
        // id's that we have given in .XML file
        tvStraight = root.findViewById(R.id.straight);
        tvLeft = root.findViewById(R.id.left);
        tvRight = root.findViewById(R.id.right);
        pieChart = root.findViewById(R.id.piechart);

        setData();
        addPieSlice();
        // To animate the pie chart
        //pieChart.startAnimation();

        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void setData(){
        tvStraight.setText(straight);
        tvRight.setText(right);
        tvLeft.setText(left);
    }

    private void addPieSlice(){
        // Set the data and color to the pie chart
        pieChart.addPieSlice(
                new PieModel(
                        "Straight",
                        Integer.parseInt(tvStraight.getText().toString()),
                        Color.parseColor("#FFA726")));
        pieChart.addPieSlice(
                new PieModel(
                        "Right",
                        Integer.parseInt(tvRight.getText().toString()),
                        Color.parseColor("#66BB6A")));
        pieChart.addPieSlice(
                new PieModel(
                        "Left",
                        Integer.parseInt(tvLeft.getText().toString()),
                        Color.parseColor("#EF5350")));
    }
}