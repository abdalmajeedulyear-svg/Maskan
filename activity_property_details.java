package com.example.maskan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class activity_property_details extends AppCompatActivity {

    private ImageButton btnBack, btnFavorite;
    private ViewPager2 viewPagerImages;
    private LinearLayout layoutIndicators;
    private TextView tvImageCounter, tvPropertyTitle, tvPropertyPrice, tvPropertyLocation;
    private TextView tvBedrooms, tvBathrooms, tvArea, tvPropertyType, tvOfferType;
    private TextView tvDescription, tvContactName, tvContactPhone;
    private Button btnContactMain;
    private Button btnCall;

    private DatabaseHelper databaseHelper;
    private Property currentProperty;
    private List<String> imagePaths = new ArrayList<>();
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_details);

        initializeViews();
        databaseHelper = new DatabaseHelper(this);

        // ✅ الحصول على بيانات العقار من الـ Intent
        getPropertyData();

        // ✅ إعداد الواجهة
        setupUI();
        setupClickListeners();

        // ✅ أضف هذا السطر - التحقق من حالة المفضلة
        checkFavoriteStatus();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        btnFavorite = findViewById(R.id.btnFavorite);
        viewPagerImages = findViewById(R.id.viewPagerImages);
        layoutIndicators = findViewById(R.id.layoutIndicators);
        tvImageCounter = findViewById(R.id.tvImageCounter);
        tvPropertyTitle = findViewById(R.id.tvPropertyTitle);
        tvPropertyPrice = findViewById(R.id.tvPropertyPrice);
        tvPropertyLocation = findViewById(R.id.tvPropertyLocation);
        tvBedrooms = findViewById(R.id.tvBedrooms);
        tvBathrooms = findViewById(R.id.tvBathrooms);
        tvArea = findViewById(R.id.tvArea);
        tvPropertyType = findViewById(R.id.tvPropertyType);
        tvOfferType = findViewById(R.id.tvOfferType);
        tvDescription = findViewById(R.id.tvDescription);
        tvContactName = findViewById(R.id.tvContactName);
        tvContactPhone = findViewById(R.id.tvContactPhone);
        btnCall = findViewById(R.id.btnCall);

        // ✅ المتغير الجديد:
        btnContactMain = findViewById(R.id.btnContactMain);
    }

    private void getPropertyData() {
        android.util.Log.d("PropertyDebug", "=== GET PROPERTY DATA STARTED ===");

        try {
            // ✅ الحصول على propertyId من الـ Intent
            int propertyId = getIntent().getIntExtra("property_id", -1);
            android.util.Log.d("PropertyDebug", "📨 Received property_id: " + propertyId);

            if (propertyId != -1) {
                // ✅ جلب بيانات العقار من قاعدة البيانات باستخدام الـ ID
                currentProperty = databaseHelper.getPropertyById(propertyId);

                if (currentProperty != null) {
                    android.util.Log.d("PropertyDebug", "✅ Loaded from DB - ID: " + currentProperty.getId() +
                            ", Title: " + currentProperty.getTitle() +
                            ", Phone: " + currentProperty.getContactPhone());
                } else {
                    android.util.Log.e("PropertyDebug", "❌ Property not found in DB with ID: " + propertyId);
                    createPropertyFromIntent();
                }
            } else {
                android.util.Log.w("PropertyDebug", "⚠️ No property_id in Intent, using direct data");
                createPropertyFromIntent();
            }

        } catch (Exception e) {
            android.util.Log.e("PropertyDebug", "💥 Error in getPropertyData: " + e.getMessage());
            createDefaultProperty();
        }
    }

    // ✅ دالة مساعدة لإنشاء العقار من الـ Intent
    private void createPropertyFromIntent() {
        String propertyTitle = getIntent().getStringExtra("property_title");
        String propertyLocation = getIntent().getStringExtra("property_location");
        String propertyPrice = getIntent().getStringExtra("property_price");
        String propertyBedrooms = getIntent().getStringExtra("property_bedrooms");
        String propertyBathrooms = getIntent().getStringExtra("property_bathrooms");
        String propertyType = getIntent().getStringExtra("property_type");

        currentProperty = new Property(
                propertyTitle != null ? propertyTitle : "عقار",
                propertyLocation != null ? propertyLocation : "موقع غير محدد",
                propertyPrice != null ? propertyPrice : "0",
                propertyBedrooms != null ? propertyBedrooms : "0",
                propertyBathrooms != null ? propertyBathrooms : "0",
                propertyType != null ? propertyType : "إيجار"
        );

        // ✅ إذا لم يكن هناك ID، نستخدم ID افتراضي (مشكلة تحتاج حل)
        if (currentProperty.getId() == 0) {
            // هذه مشكلة - نحتاج للحصول على ID حقيقي من قاعدة البيانات
            android.util.Log.w("PropertyDetails", "تحذير: العقار لا يحتوي على ID");
        }
    }

    // ✅ دالة إنشاء عقار افتراضي
    private void createDefaultProperty() {
        currentProperty = new Property("عقار", "موقع غير محدد", "0", "0", "0", "إيجار");
        currentProperty.setDescription("بيانات العقار غير متوفرة حالياً.");
        currentProperty.setContactName("غير متوفر");
        currentProperty.setContactPhone("0000000000");
    }

    // ✅ الدالة النهائية لتحميل الصور باستخدام propertyId
    private void loadPropertyImages() {
        imagePaths.clear();

        try {
            if (currentProperty != null && currentProperty.getId() > 0) {
                // ✅ البحث عن الصور باستخدام propertyId
                String searchPattern = "property_" + currentProperty.getId() + "_";
                File appDir = getFilesDir();
                File[] files = appDir.listFiles();

                if (files != null) {
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".jpg") &&
                                file.getName().startsWith(searchPattern)) {
                            imagePaths.add(file.getAbsolutePath());
                            android.util.Log.d("PropertyDetails", "تم العثور على صورة: " + file.getName());
                        }
                    }
                }
            }

            // ✅ إذا لم توجد صور، نستخدم الصور المحفوظة في قاعدة البيانات
            if (imagePaths.isEmpty() && currentProperty != null && currentProperty.hasImages()) {
                imagePaths.addAll(currentProperty.getImagePaths());
                android.util.Log.d("PropertyDetails", "تم استخدام الصور من قاعدة البيانات: " + imagePaths.size());
            }

            // ✅ إذا لا تزال لا توجد صور، نستخدم الافتراضية
            if (imagePaths.isEmpty()) {
                android.util.Log.d("PropertyDetails", "لا توجد صور للعقار");
            }

        } catch (Exception e) {
            android.util.Log.e("PropertyDetails", "خطأ في تحميل الصور: " + e.getMessage());
        }

        currentProperty.setImagePaths(imagePaths);
    }

    private void setupUI() {
        // ✅ تعبئة البيانات في الواجهة
        if (currentProperty != null) {
            tvPropertyTitle.setText(currentProperty.getTitle());
            tvPropertyLocation.setText(currentProperty.getLocation());

            // ✅ تحسين عرض السعر
            String priceText = currentProperty.getPrice();
            if (currentProperty.getType().equals("إيجار")) {
                priceText += " ر.س/شهرياً";
            } else {
                priceText += " ر.س";
            }
            tvPropertyPrice.setText(priceText);

            tvBedrooms.setText(currentProperty.getBedrooms() + " غرف");
            tvBathrooms.setText(currentProperty.getBathrooms() + " حمام");
            tvArea.setText("150 م²"); // مؤقتاً
            tvPropertyType.setText("شقة");
            tvOfferType.setText(currentProperty.getType());

            // ✅ تلوين نوع العرض
            if (currentProperty.getType().equals("إيجار")) {
                tvOfferType.setBackgroundResource(R.drawable.tag_background_rent);
                tvOfferType.setTextColor(getResources().getColor(android.R.color.white));
            } else {
                tvOfferType.setBackgroundResource(R.drawable.tag_background_sale);
                tvOfferType.setTextColor(getResources().getColor(android.R.color.white));
            }

            if (currentProperty.getDescription() != null) {
                tvDescription.setText(currentProperty.getDescription());
            } else {
                tvDescription.setText("لا يوجد وصف متاح لهذا العقار.");
            }

            if (currentProperty.getContactName() != null) {
                tvContactName.setText(currentProperty.getContactName());
            } else {
                tvContactName.setText("غير معروف");
            }

            if (currentProperty.getContactPhone() != null) {
                tvContactPhone.setText(currentProperty.getContactPhone());
            } else {
                tvContactPhone.setText("غير متوفر");
            }

            // ✅ إعداد معرض الصور
            setupImageGallery();
        }
    }

    private void setupImageGallery() {
        if (currentProperty.hasImages()) {
            imagePaths = currentProperty.getImagePaths();

            // ✅ إنشاء Adapter للصور
            ImagePagerAdapter adapter = new ImagePagerAdapter(this, imagePaths);
            viewPagerImages.setAdapter(adapter);

            // ✅ إعداد عداد الصور
            updateImageCounter(0);

            // ✅ إعداد مؤشرات الصور
            setupIndicators(imagePaths.size());

            // ✅ مستمع لتغيير الصور
            viewPagerImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    updateImageCounter(position);
                    updateIndicators(position);
                }
            });

        } else {
            // ✅ إذا لم توجد صور، إظهار صورة افتراضية
            tvImageCounter.setVisibility(View.GONE);
            layoutIndicators.setVisibility(View.GONE);
        }
    }

    private void setupIndicators(int count) {
        layoutIndicators.removeAllViews();

        for (int i = 0; i < count; i++) {
            ImageView indicator = new ImageView(this);
            indicator.setImageResource(R.drawable.indicator_dot_inactive);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dpToPx(8), dpToPx(8)
            );
            params.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            indicator.setLayoutParams(params);

            layoutIndicators.addView(indicator);
        }

        updateIndicators(0);
    }

    private void updateIndicators(int position) {
        for (int i = 0; i < layoutIndicators.getChildCount(); i++) {
            ImageView indicator = (ImageView) layoutIndicators.getChildAt(i);
            if (i == position) {
                indicator.setImageResource(R.drawable.indicator_dot_active);
            } else {
                indicator.setImageResource(R.drawable.indicator_dot_inactive);
            }
        }
    }

    private void updateImageCounter(int position) {
        if (imagePaths.size() > 0) {
            tvImageCounter.setText((position + 1) + "/" + imagePaths.size());
            tvImageCounter.setVisibility(View.VISIBLE);
        } else {
            tvImageCounter.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        // ✅ زر العودة
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // ✅ زر المفضلة - تم التحديث
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite();
            }
        });

        // ✅ زر الاتصال الرئيسي
        btnContactMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });

        // ✅ زر الاتصال الصغير
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });
    }

    // ✅ دالة التحقق من حالة المفضلة
    private void checkFavoriteStatus() {
        if (currentProperty != null && currentProperty.getId() > 0) {
            try {
                isFavorite = databaseHelper.isPropertyInFavorites(currentProperty.getId());
                updateFavoriteButton();
                android.util.Log.d("Favorites", "حالة المفضلة: " + isFavorite + " للعقار: " + currentProperty.getId());
            } catch (Exception e) {
                android.util.Log.e("Favorites", "خطأ في التحقق من المفضلة: " + e.getMessage());
            }
        } else {
            android.util.Log.w("Favorites", "لا يمكن التحقق من المفضلة - العقار لا يحتوي على ID صالح");
        }
    }

    // ✅ دالة تحديث شكل زر المفضلة
    private void updateFavoriteButton() {
        if (isFavorite) {
            btnFavorite.setImageResource(R.drawable.ic_favorite);
            btnFavorite.setColorFilter(getResources().getColor(android.R.color.holo_red_light));
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_border);
            btnFavorite.setColorFilter(getResources().getColor(android.R.color.darker_gray));
        }
    }

    private void toggleFavorite() {
        android.util.Log.d("FavoritesDebug", "=== TOGGLE FAVORITE STARTED ===");

        if (currentProperty == null) {
            android.util.Log.e("FavoritesDebug", "❌ currentProperty is NULL");
            Toast.makeText(this, "خطأ: بيانات العقار غير متوفرة", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentProperty.getId() == 0) {
            android.util.Log.e("FavoritesDebug", "❌ Property ID is 0 - Title: " + currentProperty.getTitle());
            Toast.makeText(this, "خطأ: لا يمكن إضافة عقار بدون معرف", Toast.LENGTH_SHORT).show();
            return;
        }

        android.util.Log.d("FavoritesDebug", "🆔 Property ID: " + currentProperty.getId());
        android.util.Log.d("FavoritesDebug", "📝 Property Title: " + currentProperty.getTitle());
        android.util.Log.d("FavoritesDebug", "❤️ Current Favorite Status: " + isFavorite);

        try {
            if (isFavorite) {
                // إزالة من المفضلات
                android.util.Log.d("FavoritesDebug", "🔄 Attempting to remove from favorites...");
                boolean removed = databaseHelper.removeFromFavorites(currentProperty.getId());
                android.util.Log.d("FavoritesDebug", "✅ Remove result: " + removed);

                if (removed) {
                    isFavorite = false;
                    updateFavoriteButton();
                    Toast.makeText(this, "تمت الإزالة من المفضلة", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "خطأ في الإزالة من المفضلة", Toast.LENGTH_SHORT).show();
                }
            } else {
                // إضافة إلى المفضلات
                android.util.Log.d("FavoritesDebug", "🔄 Attempting to add to favorites...");
                boolean added = databaseHelper.addToFavorites(currentProperty.getId());
                android.util.Log.d("FavoritesDebug", "✅ Add result: " + added);

                if (added) {
                    isFavorite = true;
                    updateFavoriteButton();
                    Toast.makeText(this, "تمت الإضافة إلى المفضلة", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "خطأ في الإضافة إلى المفضلة", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            android.util.Log.e("FavoritesDebug", "💥 EXCEPTION: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "حدث خطأ غير متوقع", Toast.LENGTH_SHORT).show();
        }

        android.util.Log.d("FavoritesDebug", "=== TOGGLE FAVORITE COMPLETED ===");
    }

    private void makePhoneCall() {
        android.util.Log.d("PhoneCall", "=== MAKE PHONE CALL STARTED ===");

        if (currentProperty == null) {
            android.util.Log.e("PhoneCall", "❌ currentProperty is null");
            Toast.makeText(this, "بيانات العقار غير متوفرة", Toast.LENGTH_SHORT).show();
            return;
        }

        String phoneNumber = currentProperty.getContactPhone();

        // ✅ تنظيف وتحضير رقم الهاتف
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            android.util.Log.e("PhoneCall", "❌ Phone number is null or empty");
            Toast.makeText(this, "رقم الهاتف غير متوفر", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ إزالة المسافات والأحرف غير المرغوبة
        phoneNumber = phoneNumber.trim().replaceAll("\\s+", "").replaceAll("[^0-9+]", "");

        // ✅ التحقق من صحة رقم الهاتف
        if (phoneNumber.isEmpty()) {
            android.util.Log.e("PhoneCall", "❌ Phone number is invalid after cleaning: " + currentProperty.getContactPhone());
            Toast.makeText(this, "رقم الهاتف غير صالح", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ إضافة رمز الدولة إذا لم يكن موجوداً (افتراضي السعودية +966)
        if (!phoneNumber.startsWith("+") && !phoneNumber.startsWith("00")) {
            if (phoneNumber.startsWith("0")) {
                phoneNumber = "+966" + phoneNumber.substring(1);
            } else {
                phoneNumber = "+966" + phoneNumber;
            }
        }

        android.util.Log.d("PhoneCall", "📞 Prepared phone number: " + phoneNumber);

        try {
            // ✅ إنشاء نية الاتصال
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));

            // ✅ التحقق من وجود تطبيق يمكنه التعامل مع الاتصال
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                android.util.Log.d("PhoneCall", "✅ Phone call intent started successfully");

                // ✅ عرض رسالة تأكيد
                Toast.makeText(this, "جاري الاتصال بـ: " + phoneNumber, Toast.LENGTH_SHORT).show();
            } else {
                android.util.Log.e("PhoneCall", "❌ No app available to handle phone call");
                Toast.makeText(this, "لا يوجد تطبيق للاتصال على جهازك", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            android.util.Log.e("PhoneCall", "💥 Error making phone call: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "خطأ في الاتصال: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        android.util.Log.d("PhoneCall", "=== MAKE PHONE CALL COMPLETED ===");
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }




    // ✅ حل مؤقت للاختبار - أضف هذا في onCreate
    private void testFavoritesManually() {
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // اختبار إضافة مفضلة يدوياً لأول عقار في DB
                List<Property> allProperties = databaseHelper.getAllProperties();
                if (!allProperties.isEmpty() && currentProperty.getId() == 0) {
                    Property firstProperty = allProperties.get(0);
                    android.util.Log.d("TestFavorites", "جاري اختبار إضافة العقار: " + firstProperty.getId());

                    boolean added = databaseHelper.addToFavorites(firstProperty.getId());
                    android.util.Log.d("TestFavorites", "نتيجة الإضافة: " + added);

                    if (added) {
                        // تحديث الواجهة لتعكس الحالة
                        currentProperty = firstProperty;
                        isFavorite = true;
                        updateFavoriteButton();
                        Toast.makeText(activity_property_details.this, "تمت الإضافة للمفضلة (اختبار)", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, 1000);
    }




}