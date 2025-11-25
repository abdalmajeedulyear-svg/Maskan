package com.example.maskan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {

    private List<Property> properties;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Property property);
    }

    public PropertyAdapter(List<Property> properties) {
        this.properties = properties != null ? properties : new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void updateList(List<Property> newProperties) {
        this.properties = newProperties != null ? newProperties : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_property, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property = properties.get(position);
        holder.bind(property);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(property);
                }

                // ✅ فتح صفحة تفاصيل العقار
                openPropertyDetails(holder.itemView.getContext(), property);
            }
        });
    }

    // ✅ دالة جديدة لفتح صفحة التفاصيل
    private void openPropertyDetails(Context context, Property property) {
        Intent intent = new Intent(context, activity_property_details.class);

        // ✅ تمرير propertyId بدلاً من الاعتماد على العنوان فقط
        intent.putExtra("property_id", property.getId());
        intent.putExtra("property_title", property.getTitle());
        intent.putExtra("property_location", property.getLocation());
        intent.putExtra("property_price", property.getPrice());
        intent.putExtra("property_bedrooms", property.getBedrooms());
        intent.putExtra("property_bathrooms", property.getBathrooms());
        intent.putExtra("property_type", property.getType());

        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    static class PropertyViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProperty;
        private TextView tvTitle, tvLocation, tvPrice, tvBedrooms, tvBathrooms;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProperty = itemView.findViewById(R.id.ivProperty);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvBedrooms = itemView.findViewById(R.id.tvBedrooms);
            tvBathrooms = itemView.findViewById(R.id.tvBathrooms);
        }

        public void bind(Property property) {
            if (property == null) return;

            tvTitle.setText(property.getTitle() != null ? property.getTitle() : "لا يوجد عنوان");
            tvLocation.setText(property.getLocation() != null ? property.getLocation() : "لا يوجد موقع");

            String priceText = property.getPrice() != null ? property.getPrice() + " ر.س" : "السعر غير محدد";
            tvPrice.setText(priceText);

            String bedroomsText = "غير محدد";
            if (property.getBedrooms() != null && !property.getBedrooms().equals("-") && !property.getBedrooms().equals("0")) {
                bedroomsText = property.getBedrooms() + " غرف";
            }

            String bathroomsText = "غير محدد";
            if (property.getBathrooms() != null && !property.getBathrooms().equals("-") && !property.getBathrooms().equals("0")) {
                bathroomsText = property.getBathrooms() + " حمام";
            }

            tvBedrooms.setText(bedroomsText);
            tvBathrooms.setText(bathroomsText);

            // ✅ تحميل وعرض الصور المحفوظة مع تسجيل للمراقبة
            loadPropertyImage(property);
        }

        // ✅ دالة جديدة لتحميل وعرض الصور المحفوظة
        private void loadPropertyImage(Property property) {
            // ✅ التحقق من وجود صور في قائمة imagePaths أولاً
            if (property.hasImages()) {
                String firstImagePath = property.getFirstImagePath();
                if (firstImagePath != null && !firstImagePath.isEmpty()) {
                    android.util.Log.d("PropertyAdapter", "جاري تحميل الصورة: " + firstImagePath);
                    loadImageFromStorage(firstImagePath);
                    return;
                }
            }

            // ✅ إذا لم توجد صور، تحقق من imageUrl القديم (للتوافق)
            if (property.getImageUrl() != null && property.getImageUrl().equals("has_images")) {
                ivProperty.setImageResource(R.drawable.ic_property_with_images);
                android.util.Log.d("PropertyAdapter", "استخدام صورة has_images");
            } else {
                // ✅ الصورة الافتراضية إذا لم توجد صور
                ivProperty.setImageResource(R.drawable.ic_placeholder);
                android.util.Log.d("PropertyAdapter", "استخدام الصورة الافتراضية");
            }
        }

        // ✅ دالة لتحميل الصورة من التخزين الداخلي
        private void loadImageFromStorage(String imagePath) {
            try {
                File imageFile = new File(imagePath);
                android.util.Log.d("PropertyAdapter", "التحقق من وجود الملف: " + imagePath + " - موجود: " + imageFile.exists());

                if (imageFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    if (bitmap != null) {
                        ivProperty.setImageBitmap(bitmap);
                        android.util.Log.d("PropertyAdapter", "تم تحميل الصورة بنجاح");
                    } else {
                        android.util.Log.d("PropertyAdapter", "خطأ في فك ترميز الصورة");
                        ivProperty.setImageResource(R.drawable.ic_placeholder);
                    }
                } else {
                    android.util.Log.d("PropertyAdapter", "الملف غير موجود");
                    ivProperty.setImageResource(R.drawable.ic_placeholder);
                }
            } catch (Exception e) {
                android.util.Log.e("PropertyAdapter", "خطأ في تحميل الصورة: " + e.getMessage());
                ivProperty.setImageResource(R.drawable.ic_placeholder);
            }
        }
    }
}