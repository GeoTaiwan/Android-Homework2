package com.example.homework2;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> {

    private List<DataItem> dataList = new ArrayList<>();

    public void setData(List<DataItem> newData) {
        this.dataList = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_data, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        DataItem item = dataList.get(position);
        holder.tvNo.setText(item.getNo());
        holder.tvSeq.setText(String.valueOf(item.getSeq()));
        holder.tvName.setText(item.getName());
        holder.tvEngName.setText(item.getEnglishName());
        holder.tvGeo.setText(item.getGeo());


        // ==========================================
        // 實作：根據捷運站編號/名稱，動態改變背景顏色
        // ==========================================
        String stationNo = item.getNo();
        String stationName = item.getName();

        // 為了讓白色文字更清楚，我們先將文字設為白色
        holder.tvNo.setTextColor(Color.WHITE);

        if (stationName != null && stationName.contains("美麗島")) {
            // 【美麗島站】：紅 + 橘漸層背景

            GradientDrawable gradientDrawable = new GradientDrawable(
                    GradientDrawable.Orientation.BR_TL, // 漸層方向：右下到左上
                    new int[]{Color.parseColor("#ff0000"), Color.parseColor("#FF9800")} // 紅色, 橘色
            );
            holder.tvNo.setBackground(gradientDrawable);

        } else if (stationNo != null && stationNo.startsWith("R")) {
            // 【紅線 (R開頭)】：紅色背景
            holder.tvNo.setBackgroundColor(Color.parseColor("#E53935"));

        } else if (stationNo != null && stationNo.startsWith("O")) {
            // 【橘線 (O開頭)】：橘色背景
            holder.tvNo.setBackgroundColor(Color.parseColor("#FF9800"));

        } else {
            // 【預設/其他】：輕軌或其他未定義的路線
            // 非常重要！一定要寫 else 把它改回預設顏色，避免 RecyclerView 複用 View 時顏色殘留
            holder.tvNo.setBackgroundColor(Color.parseColor("#E8F5E9"));
            holder.tvNo.setTextColor(Color.parseColor("#2E7D32")); // 預設深綠色字體
        }

        // ==========================================
        // 實作：點擊整個項目 (itemView) 時，開啟 Google Map
        // ==========================================
        holder.itemView.setOnClickListener(v -> {
            // 1. 取得緯度、經度與車站中文名稱
            String lat = item.getLat();
            String lng = item.getLng();
//            String stationName = item.getName();

            // 2. 組合 Google Maps 專用的 URI 字串
            // 格式為： geo:0,0?q=緯度,經度(標籤名稱)
            // 使用 0,0 加上 q= 查詢參數，Google Maps 就會在該座標打上地標並顯示括號內的名稱
            String uriString = "geo:0,0?q=" + lat + "," + lng + "(" + stationName + ")";
            Uri gmmIntentUri = Uri.parse(uriString);

            // 3. 建立隱含意圖 (ACTION_VIEW)
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

            // 4. 指定使用 Google Maps App 開啟 (可選，但推薦加上)
            mapIntent.setPackage("com.google.android.apps.maps");

            // 5. 執行意圖 (需包裝 Try-Catch 避免使用者沒裝 Google Maps 導致閃退)
            try {
                // 在 Adapter 中必須透過 View 取得 Context 才能 startActivity
                v.getContext().startActivity(mapIntent);
            } catch (ActivityNotFoundException e) {
                // 如果手機內沒有安裝 Google Maps App
                Toast.makeText(v.getContext(), "請先安裝 Google Maps", Toast.LENGTH_SHORT).show();

                // 備案：也可以改用網頁版開啟
                 Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + lat + "," + lng));
                 v.getContext().startActivity(webIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class DataViewHolder extends RecyclerView.ViewHolder {
        TextView tvSeq, tvNo, tvName, tvEngName, tvGeo;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNo = itemView.findViewById(R.id.textView_no);
            tvSeq = itemView.findViewById(R.id.textView_seq);
            tvName = itemView.findViewById(R.id.textView_name);
            tvEngName = itemView.findViewById(R.id.textView_english_name);
            tvGeo = itemView.findViewById(R.id.textView_lat_lng);

        }
    }
}