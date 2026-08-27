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
import java.util.List;

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

        // 【新增這行】App 開啟時，先秒速載入上次的資料
        loadDataFromDatabase();

        // 然後再發起網路請求，看有沒有最新資料可以覆蓋
        progressBar.setVisibility(View.VISIBLE);
        fetchData();

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
                    List<DataItem> dataList = response.body().getData();
                    // 【關鍵點】這裡就是你想要的：只把 Data[] 抽出來丟給 Adapter
                    adapter.setData(dataList);

                    // 2. 寫入 Room 資料庫 (背景執行緒)
                    new Thread(() -> {
                        // 取得資料庫實體
                        AppDatabase db = AppDatabase.getDatabase(MainActivity.this);

                        // 清空舊資料，存入新資料
                        db.mrtDao().deleteAll();
                        db.mrtDao().insertAll(dataList);

                        Log.d("ROOM", "資料已成功備份到本地端！");
                    }).start();
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

                // 提醒使用者目前沒有網路
                Toast.makeText(MainActivity.this, "目前無網路連線，顯示離線資料", Toast.LENGTH_LONG).show();

                // （選擇性）其實在 onCreate 已經載入過一次了，
                // 但如果是使用者手動按「讀取按鈕」失敗，也可以再呼叫一次確保畫面有資料
                loadDataFromDatabase();
            }
        });
    }

    private void loadDataFromDatabase() {
        // 1. 開啟背景執行緒去讀取資料庫
        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(MainActivity.this);
            // 呼叫我們在 DAO 寫好的 getAllStations()
            List<DataItem> localData = db.mrtDao().getAllStations();

            // 2. 切換回主執行緒 (Main Thread) 來更新畫面
            runOnUiThread(() -> {
                if (localData != null && !localData.isEmpty()) {
                    // 如果資料庫裡有舊資料，就塞給 Adapter
                    adapter.setData(localData);
                    Log.d("ROOM", "成功載入本地離線資料：" + localData.size() + "筆");
                } else {
                    Log.d("ROOM", "本地資料庫是空的，必須連上網路。");
                }
            });
        }).start();
    }

}