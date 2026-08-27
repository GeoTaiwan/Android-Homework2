package com.example.homework2;

import com.google.gson.annotations.SerializedName;

public class DataItem {
    @SerializedName("Seq")
    private int station_index;
    @SerializedName("車站編號")
    private String station_no;
    @SerializedName("車站中文名稱")
    private String station_name;
    @SerializedName("車站英文名稱")
    private String station_english_name;
    @SerializedName("車站緯度")
    private String station_lat;
    @SerializedName("車站經度")
    private String station_lng;

    public int getSeq() {
        return station_index;
    }

    public String getNo() {
        return station_no;
    }

    public String getName() {
        return station_name;
    }

    public String getEnglishName() {
        return station_english_name;
    }

    public String getLat() {
        return station_lat;
    }

    public String getLng() {
        return station_lng;
    }

    public void setSeq(int seq) {
        station_index = seq;
    }

    public void setNo(String no) {
        station_no = no;
    }

    public void setName(String name) {
        station_name = name;
    }

    public void setEnglishName(String englishName) {
        station_english_name = englishName;
    }

    public void setLat(String lat) {
        station_lat = lat;
    }

    public void setLng(String lng) {
        station_lng = lng;
    }


    public String getGeo() {
        return station_lat + ", " + station_lng;
    }


}
