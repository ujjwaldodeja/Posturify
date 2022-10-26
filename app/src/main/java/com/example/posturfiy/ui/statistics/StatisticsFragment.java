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

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;


public class StatisticsFragment extends Fragment {
    // Create the object of TextView and PieChart class
    TextView tvGood, tvBad, tvNormal;
    PieChart pieChart;

    private FragmentStatisticsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Link those objects with their respective
        // id's that we have given in .XML file
        tvGood = root.findViewById(R.id.Good);
        tvBad = root.findViewById(R.id.Bad);
        tvNormal = root.findViewById(R.id.Normal);
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
        tvGood.setText(Integer.toString(33));
        tvNormal.setText(Integer.toString(43));
        tvBad.setText(Integer.toString(24));
    }

    private void addPieSlice(){
        // Set the data and color to the pie chart
        pieChart.addPieSlice(
                new PieModel(
                        "Good",
                        Integer.parseInt(tvGood.getText().toString()),
                        Color.parseColor("#FFA726")));
        pieChart.addPieSlice(
                new PieModel(
                        "Normal",
                        Integer.parseInt(tvNormal.getText().toString()),
                        Color.parseColor("#66BB6A")));
        pieChart.addPieSlice(
                new PieModel(
                        "Bad",
                        Integer.parseInt(tvBad.getText().toString()),
                        Color.parseColor("#EF5350")));
    }
}