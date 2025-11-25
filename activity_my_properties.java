package com.example.maskan;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class activity_my_properties extends AppCompatActivity {

    private RecyclerView rvMyProperties;
    private EditText etSearch;
    private ImageButton btnBack, btnFilter;
    private TextView tvPropertiesCount;
    private LinearLayout layoutEmpty;
    private Button btnAddFirstProperty;

    private DatabaseHelper databaseHelper;
    private PropertyAdapter propertyAdapter;
    private List<Property> allProperties = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_properties);

        initializeViews();
        databaseHelper = new DatabaseHelper(this);

        setupRecyclerView();
        setupClickListeners();
        loadProperties();
    }

    private void initializeViews() {
        rvMyProperties = findViewById(R.id.rvMyProperties);
        etSearch = findViewById(R.id.etSearch);
        btnBack = findViewById(R.id.btnBack);
        btnFilter = findViewById(R.id.btnFilter);
        tvPropertiesCount = findViewById(R.id.tvPropertiesCount);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        btnAddFirstProperty = findViewById(R.id.btnAddFirstProperty);
    }

    private void setupRecyclerView() {
        // ✅ استخدام الـ Adapter المحدث
        propertyAdapter = new PropertyAdapter(allProperties);
        rvMyProperties.setLayoutManager(new LinearLayoutManager(this));
        rvMyProperties.setAdapter(propertyAdapter);

        // ✅ إضافة مستمع للنقر على العقار
        propertyAdapter.setOnItemClickListener(new PropertyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Property property) {
                showPropertyDetails(property);
            }
        });
    }

    private void setupClickListeners() {
        // زر العودة
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // زر الفلتر
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterOptions();
            }
        });

        // زر إضافة أول عقار
        btnAddFirstProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_my_properties.this, add_property.class);
                startActivity(intent);
            }
        });

        // شريط البحث
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProperties(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadProperties() {
        allProperties = databaseHelper.getAllProperties();

        // ✅ تسجيل للمراقبة - تحقق من البيانات المسترجعة
        android.util.Log.d("MyProperties", "عدد العقارات المسترجعة: " + allProperties.size());

        for (int i = 0; i < allProperties.size(); i++) {
            Property property = allProperties.get(i);
            android.util.Log.d("MyProperties", "العقار " + i + ": " + property.getTitle());
            android.util.Log.d("MyProperties", "عدد الصور: " + (property.hasImages() ? property.getImagePaths().size() : 0));
            if (property.hasImages()) {
                android.util.Log.d("MyProperties", "أول صورة: " + property.getFirstImagePath());
            }
        }

        updateUI();
    }

    private void updateUI() {
        // ✅ استخدام دالة updateList الجديدة
        propertyAdapter.updateList(allProperties);

        // تحديث العدد
        tvPropertiesCount.setText(allProperties.size() + " عقار");

        // إظهار/إخفاء رسالة عدم وجود عقارات
        if (allProperties.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvMyProperties.setVisibility(View.GONE);
            tvPropertiesCount.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvMyProperties.setVisibility(View.VISIBLE);
            tvPropertiesCount.setVisibility(View.VISIBLE);
        }
    }

    private void filterProperties(String query) {
        List<Property> filteredList = new ArrayList<>();

        if (query.isEmpty()) {
            filteredList.addAll(allProperties);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Property property : allProperties) {
                if (property.getTitle() != null && property.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        property.getLocation() != null && property.getLocation().toLowerCase().contains(lowerCaseQuery) ||
                        property.getPrice() != null && property.getPrice().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(property);
                }
            }
        }

        // ✅ استخدام دالة updateList الجديدة
        propertyAdapter.updateList(filteredList);
        tvPropertiesCount.setText(filteredList.size() + " عقار");

        // ✅ تسجيل للمراقبة
        android.util.Log.d("MyProperties", "نتائج البحث: " + filteredList.size() + " عقار");
    }


    private void showPropertyDetails(Property property) {
        // ✅ عرض تفاصيل العقار مع معلومات الصور
        String details = "عقار: " + property.getTitle() +
                "\nالموقع: " + property.getLocation() +
                "\nالسعر: " + property.getPrice() + " ر.س" +
                "\nالغرف: " + property.getBedrooms() +
                "\nالحمامات: " + property.getBathrooms() +
                "\nعدد الصور: " + (property.hasImages() ? property.getImagePaths().size() : 0);

        Toast.makeText(this, details, Toast.LENGTH_LONG).show();

        // لاحقاً يمكنك فتح صفحة تفاصيل العقار:
        // Intent intent = new Intent(this, PropertyDetailsActivity.class);
        // intent.putExtra("property_id", property.getId());
        // startActivity(intent);
    }

    private void showFilterOptions() {
        Toast.makeText(this, "خيارات الفلتر - قيد التطوير", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // تحديث البيانات عند العودة للنشاط
        loadProperties();
    }
}