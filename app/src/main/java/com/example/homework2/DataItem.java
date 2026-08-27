package com.example.homework2;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "mrt_stations")
public class DataItem {

    @PrimaryKey
    @SerializedName("Seq")
    private int seq;

    @SerializedName("車站編號")
    private String no;

    @SerializedName("車站中文名稱")
    private String name;

    @SerializedName("車站英文名稱")
    private String englishName;

    @SerializedName("車站緯度")
    private String lat;

    @SerializedName("車站經度")
    private String lng;

    // --- 以下為 Getter ---
    public int getSeq() { return seq; }
    public String getNo() { return no; }
    public String getName() { return name; }
    public String getEnglishName() { return englishName; }
    public String getLat() { return lat; }
    public String getLng() { return lng; }

    // --- 以下為 Setter ---
    public void setSeq(int seq) { this.seq = seq; }
    public void setNo(String no) { this.no = no; }
    public void setName(String name) { this.name = name; }
    public void setEnglishName(String englishName) { this.englishName = englishName; }
    public void setLat(String lat) { this.lat = lat; }
    public void setLng(String lng) { this.lng = lng; }

    // --- 組合經緯度的客製化方法 ---
    public String getGeo() {
        return lat + ", " + lng;
    }
}