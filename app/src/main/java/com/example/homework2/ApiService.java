package com.example.homework2;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    // 填入 Base URL 之後的相對路徑
    @GET("Json/Get/4278fc6a-c3ea-4192-8ce0-40f00cdb40dd")
    Call<ApiResponse> getKaohsiungData();
}
