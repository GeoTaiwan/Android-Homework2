package com.example.homework2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private DataAdapter adapter;
    private static final String BASE_URL = "https://data.kcg.gov.tw/";
    // 宣告畫面元件
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private Button btnGetData, btnClearData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        btnGetData = findViewById(R.id.btn_get_data_mrt);
        btnClearData = findViewById(R.id.btn_clear_data_mrt);
        swipeRefreshLayout = findViewById(R.id.srl_main);
        progressBar = findViewById(R.id.pb_spinner);




        // 1. 設定 RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DataAdapter();
        recyclerView.setAdapter(adapter);
        // 2. 實作功能一：讀取資料按鈕
        btnGetData.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE); // 顯示讀取圈圈
            fetchData();
        });
        // 3. 實作功能二：清空畫面按鈕
        btnClearData.setOnClickListener(v -> {
            adapter.setData(new ArrayList<>()); // 塞入一個空的 ArrayList 即可清空畫面
            Toast.makeText(MainActivity.this, "資料已清空", Toast.LENGTH_SHORT).show();
        });

        // 4. 實作功能三：下拉更新
        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchData(); // 下拉時 SwipeRefreshLayout 會自帶讀取動畫，直接呼叫 fetchData 即可
        });

    }

    private void fetchData() {
        // 建立 Retrofit 實體
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // 非同步發送請求
        apiService.getKaohsiungData().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                // 無論成功或失敗，都隱藏讀取動畫
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false); // 停止下拉更新的轉圈動畫
                if (response.isSuccessful() && response.body() != null) {
                    // 【關鍵點】這裡就是你想要的：只把 Data[] 抽出來丟給 Adapter
                    adapter.setData(response.body().getData());
                } else {
                    Log.e("API_ERROR", "Response Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // 連線失敗時，也要隱藏動畫
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Log.e("API_ERROR", "連線失敗: " + t.getMessage());
            }
        });
    }


}