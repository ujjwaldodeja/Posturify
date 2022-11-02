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
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


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

    private double straightPerc;
    private double leftPerc;
    private double rightPerc;

    private FragmentStatisticsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        List<Record> filteredByName = setDataForChosenPlace();

        getDataForPieChart(filteredByName);

        // Link those objects with their respective
        // id's that we have given in .XML file
        initWidgets(root);

        setData();
        addPieSlice();
        // To animate the pie chart
        //pieChart.startAnimation();
        configureLineChart();
        getEntriesLeft(filteredByName);
        configureLeft();
        lineChart.animateXY(2000, 2000);

//        lineChart.animateXY(2000, 2000);

        return root;

    }

    public void initWidgets(View root) {
        tvStraight = root.findViewById(R.id.straight);
        tvLeft = root.findViewById(R.id.left);
        tvRight = root.findViewById(R.id.right);
        name = root.findViewById(R.id.loc_stat_name);
        pieChart = root.findViewById(R.id.piechart);
        lineChart = root.findViewById(R.id.activity_main_linechart);
    }

    public List<Record> setDataForChosenPlace() {
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
        return filteredByName;
    }

    public void getDataForPieChart(List<Record> filteredByName) {
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

        int sum = straight + left + right;

        System.out.println("n " + straight / (1.0 * sum) + "    l " + left / (1.0 * sum) + "   r " + right / (1.0 * sum));

        straightPerc = Double.parseDouble(new DecimalFormat("0.00").format(straight / (1.0 * sum))) * 100;
        leftPerc = Double.parseDouble(new DecimalFormat("0.00").format(left / (1.0 * sum))) * 100;
        rightPerc = Double.parseDouble(new DecimalFormat("0.00").format(right / (1.0 * sum))) * 100;

        System.out.println(straightPerc + " s   " + leftPerc + " l    " + rightPerc + " r     ");

        decrease();
    }

    public void decrease() {
        if (straightPerc + leftPerc + rightPerc == 101.00) {
            if (straightPerc > leftPerc && straightPerc > rightPerc) {
                straightPerc = straightPerc - 1.00;
            } else if (leftPerc > straightPerc && leftPerc > rightPerc) {
                leftPerc = leftPerc - 1.00;
            } else {
                rightPerc = rightPerc - 1.00;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void setData(){
        tvStraight.setText(straightPerc + "%");
        tvRight.setText(rightPerc + "%");
        tvLeft.setText(leftPerc + "%");
        name.setText(placeName);
    }

    private void addPieSlice(){
        // Set the data and color to the pie chart
        pieChart.addPieSlice(
                new PieModel(
                        "Straight",
                        straight,
                        Color.parseColor("#FFA726")));
        pieChart.addPieSlice(
                new PieModel(
                        "Right",
                        right,
                        Color.parseColor("#66BB6A")));
        pieChart.addPieSlice(
                new PieModel(
                        "Left",
                        left,
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
        YAxis yAxis = lineChart.getAxisRight();
        yAxis.setEnabled(false);
        xAxis.setEnabled(false);
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {
                long millis = (long) value * 1000L;
                return mFormat.format(new Date(millis));
            }
        });
    }
    private void configureLeft(){
        lineDataSet = new LineDataSet(entries, "Over week");
        lineDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        lineDataSet.setValueTextColor(Color.WHITE);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setValueTextSize(20);
        lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
    }
    private void getEntriesLeft(List<Record> records) {
        entries = new ArrayList<>();

        Timestamp curDay = new Timestamp(new Date().getTime()); //current date: example Wed 18:00

        Calendar curDay12AM = new GregorianCalendar();
        curDay12AM.set(Calendar.HOUR, 0);
        curDay12AM.set(Calendar.MINUTE, 1);
        curDay12AM.set(Calendar.SECOND, 0);
        curDay12AM.set(Calendar.MILLISECOND, 0);
        Timestamp curDay12AMTimestamp = new Timestamp(curDay12AM.getTime().getTime()); //current date: example Wed 12.01am
        float val = 1.0f * getNumRecordsBetweenTwoTimestamps(records, curDay, curDay12AMTimestamp);
        float tim = (1.0f * curDay12AM.getTime().getTime()) / 1000000000000f;
        entries.add(new Entry(1.0f, val));

        setEntry(records, curDay12AMTimestamp, 0, 2);

    }

    public void setEntry(List<Record> records, Timestamp prev, int i, int time) {
        Calendar prevDay = Calendar.getInstance();
        prevDay.setTimeInMillis(prev.getTime());
        prevDay.add(Calendar.HOUR, -24);
        Timestamp prev2 = new Timestamp(prevDay.getTime().getTime());
        if (isPrevDayAvailable(records, prev, i) && i <= 5) {//Tue 12.01
            float val = getNumRecordsBetweenTwoTimestamps(records, prev, prev2) * 1.0f;
            float tim = (prevDay.getTime().getTime() * 1.0f) / 1000000000000f;
            entries.add(new Entry(time * 1.0f, val));
            i++;
            time++;
            setEntry(records, prev2, i, time);
        } else if (i <= 5){
            float tim = (prevDay.getTime().getTime() * 1.0f) / 1000000000000f;
            entries.add(new Entry(time * 1.0f, 0f));
            i++;
            time++;
            setEntry(records, prev2, i, time);
        }
    }

    public boolean isPrevDayAvailable(List<Record> records, Timestamp t1, int i) {
        for (Record r : records) {
            if (t1.compareTo(r.getTimestamp()) > 0 || i == 5) {
                return true;
            }
        }
        return false;
    }

    public int getNumRecordsBetweenTwoTimestamps(List<Record> records, Timestamp cur, Timestamp prev) {
        List<Record> recordsInBetween = new ArrayList<>();
        for (Record r : records) {
            if (cur.compareTo(r.getTimestamp()) > 0 && prev.compareTo(r.getTimestamp()) < 0) {
                recordsInBetween.add(r);
            }
        }
        return recordsInBetween.size();
    }
}