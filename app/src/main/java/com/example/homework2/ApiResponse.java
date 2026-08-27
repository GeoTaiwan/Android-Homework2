package com.example.homework2;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiResponse {
    // 告訴 Gson 這個變數對應 JSON 裡的 "Data" 陣列
    @SerializedName("Data")
    private List<DataItem> data;

    public List<DataItem> getData() {
        return data;
    }
}