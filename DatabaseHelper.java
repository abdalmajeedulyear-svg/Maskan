package com.example.maskan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "maskan.db";
    private static final int DATABASE_VERSION = 2; // ✅ زيادة الإصدار لإضافة جدول المفضلات

    // جدول العقارات
    private static final String TABLE_PROPERTIES = "properties";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_OFFER_TYPE = "offer_type";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_BEDROOMS = "bedrooms";
    private static final String COLUMN_BATHROOMS = "bathrooms";
    private static final String COLUMN_AREA = "area";
    private static final String COLUMN_CONTACT_NAME = "contact_name";
    private static final String COLUMN_CONTACT_PHONE = "contact_phone";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_IMAGES = "images";
    private static final String COLUMN_CREATED_AT = "created_at";

    // ✅ جدول المفضلات الجديد
    private static final String TABLE_FAVORITES = "favorites";
    private static final String COLUMN_FAVORITE_ID = "favorite_id";
    private static final String COLUMN_PROPERTY_ID = "property_id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_ADDED_AT = "added_at";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PROPERTIES_TABLE = "CREATE TABLE " + TABLE_PROPERTIES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_PRICE + " REAL,"
                + COLUMN_TYPE + " TEXT,"
                + COLUMN_OFFER_TYPE + " TEXT,"
                + COLUMN_ADDRESS + " TEXT,"
                + COLUMN_BEDROOMS + " INTEGER,"
                + COLUMN_BATHROOMS + " INTEGER,"
                + COLUMN_AREA + " REAL,"
                + COLUMN_CONTACT_NAME + " TEXT,"
                + COLUMN_CONTACT_PHONE + " TEXT,"
                + COLUMN_LATITUDE + " REAL,"
                + COLUMN_LONGITUDE + " REAL,"
                + COLUMN_IMAGES + " TEXT,"
                + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(CREATE_PROPERTIES_TABLE);

        // ✅ إنشاء جدول المفضلات
        String CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_FAVORITES + "("
                + COLUMN_FAVORITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_PROPERTY_ID + " INTEGER,"
                + COLUMN_USER_ID + " INTEGER DEFAULT 1,"
                + COLUMN_ADDED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(" + COLUMN_PROPERTY_ID + ") REFERENCES " + TABLE_PROPERTIES + "(" + COLUMN_ID + ")"
                + ")";
        db.execSQL(CREATE_FAVORITES_TABLE);

        // ✅ إضافة بعض البيانات التجريبية إذا كان الجدول فارغاً
        addSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // ✅ إنشاء جدول المفضلات إذا كان الترقية من إصدار قديم
            String CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_FAVORITES + "("
                    + COLUMN_FAVORITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_PROPERTY_ID + " INTEGER,"
                    + COLUMN_USER_ID + " INTEGER DEFAULT 1,"
                    + COLUMN_ADDED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY(" + COLUMN_PROPERTY_ID + ") REFERENCES " + TABLE_PROPERTIES + "(" + COLUMN_ID + ")"
                    + ")";
            db.execSQL(CREATE_FAVORITES_TABLE);
            android.util.Log.d("Database", "تم إنشاء جدول المفضلات في الترقية");
        }
    }

    // ✅ إضافة بيانات تجريبية عند إنشاء الجدول لأول مرة
    private void addSampleData(SQLiteDatabase db) {
        try {
            ContentValues values1 = new ContentValues();
            values1.put(COLUMN_TITLE, "شقة فاخرة في الرياض - النخيل");
            values1.put(COLUMN_DESCRIPTION, "شقة جميلة في حي النخيل بمساحة 150م²");
            values1.put(COLUMN_PRICE, 1200.0);
            values1.put(COLUMN_TYPE, "شقة");
            values1.put(COLUMN_OFFER_TYPE, "للإيجار");
            values1.put(COLUMN_ADDRESS, "حي النخيل، الرياض");
            values1.put(COLUMN_BEDROOMS, 3);
            values1.put(COLUMN_BATHROOMS, 2);
            values1.put(COLUMN_AREA, 150.0);
            values1.put(COLUMN_CONTACT_NAME, "أحمد محمد");
            values1.put(COLUMN_CONTACT_PHONE, "0551234567");
            values1.put(COLUMN_LATITUDE, 0.0);
            values1.put(COLUMN_LONGITUDE, 0.0);
            values1.put(COLUMN_IMAGES, "");

            db.insert(TABLE_PROPERTIES, null, values1);

            // ✅ إضافة عقار تجريبي ثاني
            ContentValues values2 = new ContentValues();
            values2.put(COLUMN_TITLE, "فيلا للبيع في جدة - السلام");
            values2.put(COLUMN_DESCRIPTION, "فيلا فاخرة بمساحة 300م² مع حديقة وموقف سيارات");
            values2.put(COLUMN_PRICE, 2500000.0);
            values2.put(COLUMN_TYPE, "فيلا");
            values2.put(COLUMN_OFFER_TYPE, "للبيع");
            values2.put(COLUMN_ADDRESS, "حي السلام، جدة");
            values2.put(COLUMN_BEDROOMS, 4);
            values2.put(COLUMN_BATHROOMS, 3);
            values2.put(COLUMN_AREA, 300.0);
            values2.put(COLUMN_CONTACT_NAME, "محمد علي");
            values2.put(COLUMN_CONTACT_PHONE, "0509876543");
            values2.put(COLUMN_LATITUDE, 0.0);
            values2.put(COLUMN_LONGITUDE, 0.0);
            values2.put(COLUMN_IMAGES, "");

            db.insert(TABLE_PROPERTIES, null, values2);

            android.util.Log.d("Database", "تم إضافة البيانات التجريبية بنجاح");
        } catch (Exception e) {
            android.util.Log.e("Database", "خطأ في إضافة البيانات التجريبية: " + e.getMessage());
        }
    }

    // ✅ دوال إدارة المفضلات

    // إضافة عقار إلى المفضلات
    public boolean addToFavorites(int propertyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROPERTY_ID, propertyId);

        try {
            // التحقق أولاً إذا كان العقار موجوداً في المفضلات
            if (isPropertyInFavorites(propertyId)) {
                android.util.Log.d("Favorites", "العقار موجود بالفعل في المفضلات: " + propertyId);
                return true;
            }

            long result = db.insert(TABLE_FAVORITES, null, values);
            android.util.Log.d("Favorites", "تم إضافة العقار " + propertyId + " إلى المفضلات");
            return result != -1;
        } catch (Exception e) {
            android.util.Log.e("Favorites", "خطأ في إضافة المفضلة: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    // إزالة عقار من المفضلات
    public boolean removeFromFavorites(int propertyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int result = db.delete(TABLE_FAVORITES,
                    COLUMN_PROPERTY_ID + " = ?",
                    new String[]{String.valueOf(propertyId)});
            android.util.Log.d("Favorites", "تم إزالة العقار " + propertyId + " من المفضلات");
            return result > 0;
        } catch (Exception e) {
            android.util.Log.e("Favorites", "خطأ في إزالة المفضلة: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    // التحقق إذا كان العقار في المفضلات
    public boolean isPropertyInFavorites(int propertyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_FAVORITES,
                    new String[]{COLUMN_FAVORITE_ID},
                    COLUMN_PROPERTY_ID + " = ?",
                    new String[]{String.valueOf(propertyId)},
                    null, null, null);

            boolean exists = cursor.getCount() > 0;
            android.util.Log.d("Favorites", "التحقق من المفضلة " + propertyId + ": " + exists);
            return exists;
        } catch (Exception e) {
            android.util.Log.e("Favorites", "خطأ في التحقق من المفضلة: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }

    // جلب جميع العقارات المفضلة
    public List<Property> getFavoriteProperties() {
        List<Property> favorites = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT p.* FROM " + TABLE_PROPERTIES + " p " +
                    "INNER JOIN " + TABLE_FAVORITES + " f ON p." + COLUMN_ID + " = f." + COLUMN_PROPERTY_ID +
                    " ORDER BY f." + COLUMN_ADDED_AT + " DESC";

            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                    String address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
                    int bedrooms = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BEDROOMS));
                    int bathrooms = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BATHROOMS));
                    String propertyType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                    String offerType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OFFER_TYPE));
                    String images = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGES));

                    Property property = new Property();
                    property.setId(id);
                    property.setTitle(title);
                    property.setDescription(description);
                    property.setLocation(address);
                    property.setPrice(String.valueOf(price));
                    property.setBedrooms(String.valueOf(bedrooms));
                    property.setBathrooms(String.valueOf(bathrooms));
                    property.setType(propertyType);
                    property.setOfferType(offerType);

                    // تحميل مسارات الصور
                    if (images != null && !images.isEmpty()) {
                        List<String> imagePaths = Arrays.asList(images.split(","));
                        property.setImagePaths(imagePaths);
                    }

                    favorites.add(property);

                } while (cursor.moveToNext());
            }

            android.util.Log.d("Favorites", "تم جلب " + favorites.size() + " عقار من المفضلات");

        } catch (Exception e) {
            android.util.Log.e("Favorites", "خطأ في جلب المفضلات: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return favorites;
    }

    // ✅ دوال العقارات الحالية (تبقى كما هي)

    public long addProperty(String title, String description, double price, String type,
                            String offerType, String address, int bedrooms, int bathrooms,
                            double area, String contactName, String contactPhone,
                            List<String> imagePaths) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = -1;

        try {
            ContentValues values = new ContentValues();

            values.put(COLUMN_TITLE, title);
            values.put(COLUMN_DESCRIPTION, description);
            values.put(COLUMN_PRICE, price);
            values.put(COLUMN_TYPE, type);
            values.put(COLUMN_OFFER_TYPE, offerType);
            values.put(COLUMN_ADDRESS, address);
            values.put(COLUMN_BEDROOMS, bedrooms);
            values.put(COLUMN_BATHROOMS, bathrooms);
            values.put(COLUMN_AREA, area);
            values.put(COLUMN_CONTACT_NAME, contactName);
            values.put(COLUMN_CONTACT_PHONE, contactPhone);
            values.put(COLUMN_LATITUDE, 0.0);
            values.put(COLUMN_LONGITUDE, 0.0);

            // حفظ مسارات الصور
            String imagesValue = "";
            if (imagePaths != null && !imagePaths.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (String path : imagePaths) {
                    if (path != null && !path.isEmpty()) {
                        if (sb.length() > 0) sb.append(",");
                        sb.append(path);
                    }
                }
                imagesValue = sb.toString();
            }
            values.put(COLUMN_IMAGES, imagesValue);

            id = db.insert(TABLE_PROPERTIES, null, values);

            android.util.Log.d("Database", "تم إضافة عقار جديد - ID: " + id +
                    " - العنوان: " + title + " - الصور: " + imagesValue);

        } catch (Exception e) {
            android.util.Log.e("Database", "خطأ في إضافة العقار: " + e.getMessage());
        } finally {
            db.close();
        }

        return id;
    }

    public List<Property> getAllProperties() {
        List<Property> properties = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String selectQuery = "SELECT * FROM " + TABLE_PROPERTIES + " ORDER BY " + COLUMN_CREATED_AT + " DESC";
            cursor = db.rawQuery(selectQuery, null);

            android.util.Log.d("Database", "جاري استرجاع العقارات، العدد: " + cursor.getCount());

            if (cursor.moveToFirst()) {
                do {
                    try {
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                        String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                        String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                        String address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));
                        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
                        int bedrooms = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BEDROOMS));
                        int bathrooms = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BATHROOMS));
                        String propertyType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                        String offerType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OFFER_TYPE));
                        String images = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGES));

                        Property property = new Property();
                        property.setId(id);
                        property.setTitle(title);
                        property.setDescription(description);
                        property.setLocation(address);
                        property.setPrice(String.valueOf(price));
                        property.setBedrooms(String.valueOf(bedrooms));
                        property.setBathrooms(String.valueOf(bathrooms));
                        property.setType(propertyType);
                        property.setOfferType(offerType);

                        if (images != null && !images.isEmpty()) {
                            List<String> imagePaths = Arrays.asList(images.split(","));
                            property.setImagePaths(imagePaths);
                        }

                        properties.add(property);

                    } catch (Exception e) {
                        android.util.Log.e("Database", "خطأ في معالجة عقار: " + e.getMessage());
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            android.util.Log.e("Database", "خطأ في getAllProperties: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return properties;
    }

    public List<Property> getPropertiesByType(String offerType) {
        List<Property> properties = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_PROPERTIES, null,
                    COLUMN_OFFER_TYPE + " = ?",
                    new String[]{offerType},
                    null, null,
                    COLUMN_CREATED_AT + " DESC");

            if (cursor.moveToFirst()) {
                do {
                    try {
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                        String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                        String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                        String address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));
                        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
                        int bedrooms = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BEDROOMS));
                        int bathrooms = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BATHROOMS));
                        String propertyType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                        String images = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGES));

                        Property property = new Property();
                        property.setId(id);
                        property.setTitle(title);
                        property.setDescription(description);
                        property.setLocation(address);
                        property.setPrice(String.valueOf(price));
                        property.setBedrooms(String.valueOf(bedrooms));
                        property.setBathrooms(String.valueOf(bathrooms));
                        property.setType(propertyType);
                        property.setOfferType(offerType);

                        if (images != null && !images.isEmpty()) {
                            List<String> imagePaths = Arrays.asList(images.split(","));
                            property.setImagePaths(imagePaths);
                        }

                        properties.add(property);

                    } catch (Exception e) {
                        android.util.Log.e("Database", "خطأ في معالجة عقار: " + e.getMessage());
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            android.util.Log.e("Database", "خطأ في getPropertiesByType: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return properties;
    }

    public Property getPropertyById(int propertyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Property property = null;
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_PROPERTIES, null,
                    COLUMN_ID + " = ?",
                    new String[]{String.valueOf(propertyId)},
                    null, null, null);

            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
                int bedrooms = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BEDROOMS));
                int bathrooms = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BATHROOMS));
                String propertyType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                String offerType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OFFER_TYPE));
                String images = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGES));

                property = new Property();
                property.setId(id);
                property.setTitle(title);
                property.setDescription(description);
                property.setLocation(address);
                property.setPrice(String.valueOf(price));
                property.setBedrooms(String.valueOf(bedrooms));
                property.setBathrooms(String.valueOf(bathrooms));
                property.setType(propertyType);
                property.setOfferType(offerType);

                if (images != null && !images.isEmpty()) {
                    List<String> imagePaths = Arrays.asList(images.split(","));
                    property.setImagePaths(imagePaths);
                }
            }
        } catch (Exception e) {
            android.util.Log.e("Database", "خطأ في getPropertyById: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return property;
    }

    public void deleteProperty(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_PROPERTIES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
            android.util.Log.d("Database", "تم حذف العقار: " + id);
        } catch (Exception e) {
            android.util.Log.e("Database", "خطأ في حذف العقار: " + e.getMessage());
        } finally {
            db.close();
        }
    }

    public int getPropertiesCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;

        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PROPERTIES, null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            android.util.Log.e("Database", "خطأ في getPropertiesCount: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return count;
    }

    public boolean updatePropertyImages(long propertyId, List<String> imagePaths) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = false;

        try {
            ContentValues values = new ContentValues();

            String imagesValue = "";
            if (imagePaths != null && !imagePaths.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (String path : imagePaths) {
                    if (path != null && !path.isEmpty()) {
                        if (sb.length() > 0) sb.append(",");
                        sb.append(path);
                    }
                }
                imagesValue = sb.toString();
            }
            values.put(COLUMN_IMAGES, imagesValue);

            int rowsAffected = db.update(TABLE_PROPERTIES, values,
                    COLUMN_ID + " = ?", new String[]{String.valueOf(propertyId)});

            success = (rowsAffected > 0);
            android.util.Log.d("Database", "تم تحديث صور العقار: " + propertyId + " - الصور: " + imagesValue);

        } catch (Exception e) {
            android.util.Log.e("Database", "خطأ في تحديث صور العقار: " + e.getMessage());
        } finally {
            db.close();
        }

        return success;
    }
}