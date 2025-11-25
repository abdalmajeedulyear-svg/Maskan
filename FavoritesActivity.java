package com.example.maskan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import com.google.android.material.navigation.NavigationView;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView rvFavorites;
    private ImageButton btnBack;
    private LinearLayout layoutEmpty;
    private TextView tvFavoritesCount;

    private DatabaseHelper databaseHelper;
    private PropertyAdapter propertyAdapter;
    private List<Property> favoriteProperties = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        initializeViews();
        databaseHelper = new DatabaseHelper(this);

        setupRecyclerView();
        setupClickListeners();
        loadFavorites();
        setupBottomNavigation();
    }

    private void initializeViews() {
        rvFavorites = findViewById(R.id.rvFavorites);
        btnBack = findViewById(R.id.btnBack);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        tvFavoritesCount = findViewById(R.id.tvFavoritesCount);
    }

    private void setupRecyclerView() {
        propertyAdapter = new PropertyAdapter(favoriteProperties);
        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        rvFavorites.setAdapter(propertyAdapter);

        propertyAdapter.setOnItemClickListener(new PropertyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Property property) {
                openPropertyDetails(property);
            }
        });
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadFavorites() {
        favoriteProperties.clear();
        favoriteProperties.addAll(databaseHelper.getFavoriteProperties());
        updateUI();
    }

    private void updateUI() {
        propertyAdapter.updateList(favoriteProperties);
        tvFavoritesCount.setText(favoriteProperties.size() + " عقار مفضل");

        if (favoriteProperties.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvFavorites.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvFavorites.setVisibility(View.VISIBLE);
        }
    }

    private void openPropertyDetails(Property property) {
        Intent intent = new Intent(this, activity_property_details.class);
        intent.putExtra("property_id", property.getId());
        intent.putExtra("property_title", property.getTitle());
        intent.putExtra("property_location", property.getLocation());
        intent.putExtra("property_price", property.getPrice());
        intent.putExtra("property_bedrooms", property.getBedrooms());
        intent.putExtra("property_bathrooms", property.getBathrooms());
        intent.putExtra("property_type", property.getType());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_favorites);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_favorites) {
                    // نحن بالفعل في الصفحة المفضلات
                    return true;
                } else if (id == R.id.nav_search) {
                    openSearchActivity();
                    return true;
                } else if (id == R.id.nav_add) {
                    openAddProperty();
                    return true;
                } else if (id == R.id.nav_home) {
                    openMainActivity();
                    return true;
                }
                return false;
            }
        });
    }

    private void openMainActivity() {
        try {
            Intent intent = new Intent(FavoritesActivity.this,MainActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "خطأ في فتح شاشة الرئيسية: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", "Error opening SearchActivity: " + e.getMessage());
        }
    }

    private void openAddProperty() {
        Intent intent = new Intent(FavoritesActivity.this, add_property.class);
        startActivity(intent);
    }

    // ✅ دالة فتح واجهة المفضلات
    private void openSearchActivity() {
        Intent intent = new Intent(FavoritesActivity.this, SearchActivity.class);
        startActivity(intent);
    }

}