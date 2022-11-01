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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class StatisticsFragment extends Fragment {
    // Create the object of TextView and PieChart class
    TextView tvStraight, tvLeft, tvRight, name;
    PieChart pieChart;

    private LineChart lineChart;
    private LineData lineData;
    private LineDataSet lineDataSet;
    private ArrayList entries;

    private int straight;
    private int left;
    private  int right;
    private String placeName;

    private FragmentStatisticsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        placeName = HomeFragment.placeChosen;
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
        name = root.findViewById(R.id.loc_stat_name);
        pieChart = root.findViewById(R.id.piechart);

        setData();
        addPieSlice();
        // To animate the pie chart
        //pieChart.startAnimation();

        lineChart = root.findViewById(R.id.activity_main_linechart);
        configureLineChart();
        getEntriesLeft();
        configureLeft();
        lineChart.animateXY(2000, 2000);

        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void setData(){
        tvStraight.setText(straight + "");
        tvRight.setText(right + "");
        tvLeft.setText(left + "");
        name.setText(placeName);
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

    private void configureLineChart() {
        Description desc = new Description();
        desc.setText("Your poses over time");
        desc.setTextColor(Color.WHITE);
        desc.setTextSize(14);
        lineChart.setDescription(desc);
        //lineChart.setBackgroundColor(Color.rgb(134, 187, 252));
        lineChart.setTouchEnabled(false);
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd MM", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {
                long millis = (long) value * 1000L;
                return mFormat.format(new Date(millis));
            }
        });
    }
    private void configureLeft(){
        lineDataSet = new LineDataSet(entries, "");
        lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        lineDataSet.setValueTextColor(Color.WHITE);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setValueTextSize(20);
    }
    private void getEntriesLeft() {
        entries = new ArrayList<>();
        entries.add(new Entry(1f, 9f));
        entries.add(new Entry(2f, 16f));
        entries.add(new Entry(3f, 12f));
        entries.add(new Entry(4f, 21f));
        entries.add(new Entry(5f, 24f));
        entries.add(new Entry(6f, 18f));
        entries.add(new Entry(7f, 10f));
    }
}