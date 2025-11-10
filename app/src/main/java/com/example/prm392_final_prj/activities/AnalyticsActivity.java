package com.example.prm392_final_prj.activities;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.dao.AppDatabase;
import com.example.prm392_final_prj.dao.MonthlyBookingStat;
import com.example.prm392_final_prj.dao.MonthlyRevenueStat;
import com.example.prm392_final_prj.repository.AnalyticsRepository;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AnalyticsActivity extends AdminNavBaseActivity {

    private TextView tvTotalTours, tvTotalCustomers;
    private EditText etFromDate, etToDate;
    private Button btnFilter;
    private BarChart barChartBookings;
    private LineChart lineChartRevenue;
    private AnalyticsRepository repository;

    private Date fromDate, toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_analytics);
        setupBottomNavigation(R.id.nav_analytics);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        initView();
    }

    private void initView() {
        repository = new AnalyticsRepository(this);

        tvTotalTours = findViewById(R.id.tvTotalTours);
        tvTotalCustomers = findViewById(R.id.tvTotalCustomers);
        etFromDate = findViewById(R.id.etFromDate);
        etToDate = findViewById(R.id.etToDate);
        btnFilter = findViewById(R.id.btnFilter);
        barChartBookings = findViewById(R.id.barChartBookings);
        lineChartRevenue = findViewById(R.id.lineChartRevenue);

        loadSummary();
        setupDatePickers();

        btnFilter.setOnClickListener(v -> {
            if (fromDate == null || toDate == null) {
                Toast.makeText(this, "Please select both From and To dates", Toast.LENGTH_SHORT).show();
                return;
            }
            loadCharts();
        });
    }

    private void loadSummary() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            int tours = repository.getTotalTours();
            int customers = repository.getTotalCustomers();
            runOnUiThread(() -> {
                tvTotalTours.setText(String.valueOf(tours));
                tvTotalCustomers.setText(String.valueOf(customers));
            });
        });
    }

    private void setupDatePickers() {
        etFromDate.setOnClickListener(v -> showDatePicker(true));
        etToDate.setOnClickListener(v -> showDatePicker(false));
    }

    private void showDatePicker(boolean isFrom) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, day);
            if (isFrom) {
                fromDate = selected.getTime();
                etFromDate.setText(day + "/" + (month + 1) + "/" + year);
            } else {
                toDate = selected.getTime();
                etToDate.setText(day + "/" + (month + 1) + "/" + year);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadCharts() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<MonthlyBookingStat> bookings = repository.getMonthlyBookingCounts(fromDate, toDate);
            List<MonthlyRevenueStat> revenues = repository.getMonthlyRevenue(fromDate, toDate);

            runOnUiThread(() -> {
                displayBarChart(bookings);
                displayLineChart(revenues);
            });
        });
    }

    private void displayBarChart(List<MonthlyBookingStat> data) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            MonthlyBookingStat stat = data.get(i);
            entries.add(new BarEntry(i, stat.count));
            labels.add(getMonthName(stat.month) + "-" + stat.year);
        }

        BarDataSet dataSet = new BarDataSet(entries, "Bookings");
        dataSet.setColor(Color.parseColor("#2196F3"));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barChartBookings.setData(barData);

        XAxis xAxis = barChartBookings.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        barChartBookings.getDescription().setEnabled(false);
        barChartBookings.animateY(1000);
        barChartBookings.invalidate();
    }

    private void displayLineChart(List<MonthlyRevenueStat> data) {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            MonthlyRevenueStat stat = data.get(i);
            entries.add(new Entry(i, (float) stat.revenue));
            labels.add(getMonthName(stat.month) + "-" + stat.year);
        }

        LineDataSet dataSet = new LineDataSet(entries, "Revenue");
        dataSet.setColor(Color.parseColor("#4CAF50"));
        dataSet.setCircleColor(Color.parseColor("#4CAF50"));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        LineData lineData = new LineData(dataSet);
        lineChartRevenue.setData(lineData);

        XAxis xAxis = lineChartRevenue.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        lineChartRevenue.getDescription().setEnabled(false);
        lineChartRevenue.animateY(1000);
        lineChartRevenue.invalidate();
    }

    private String getMonthName(int monthNumber) {
        // monthNumber = 1–12
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        if (monthNumber >= 1 && monthNumber <= 12) {
            return months[monthNumber - 1];
        }
        return "N/A";
    }
}