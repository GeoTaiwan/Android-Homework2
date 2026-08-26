package com.example.homework2;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class KaohsiungMRTStationsActivity extends AppCompatActivity {
    ProgressBar pb_spinner;
    TextView tv_main;
    Button btn_get_data, btn_clear_data;
    ListView lv_api_data;
    ArrayList<String> MRT_station_arrListOfString;
    ArrayAdapter<String> MRT_station_arrAdapter;
    String strAPI_URL = "https://data.kcg.gov.tw/Json/Get/4278fc6a-c3ea-4192-8ce0-40f00cdb40dd";
    String strTitle = "高雄市捷運站資料";
    String strStationName, strLat, strLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kaohsiung_mrtstations);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        tv_main = findViewById(R.id.tv_kaohsiung_mrt_station_title);
        btn_get_data = findViewById(R.id.btn_get_data_mrt);
        btn_clear_data = findViewById(R.id.btn_clear_data_mrt);
        lv_api_data = findViewById(R.id.lv_kaohsiung_mrt_station);
        pb_spinner=findViewById(R.id.pb_spinner);

        MRT_station_arrListOfString = new ArrayList<String>();

        btn_get_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, strAPI_URL, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObjectResponse) {
                            int i;
                            try {
                                JSONArray jsonArrayData = jsonObjectResponse.getJSONArray("Data");
                                JSONObject jsonObjectMRTStation = new JSONObject();
                                for (i = 0; i < jsonArrayData.length(); i++) {
                                    jsonObjectMRTStation = jsonArrayData.getJSONObject(i);
                                    strStationName = jsonObjectMRTStation.getString("車站中文名稱");
                                    strLat = jsonObjectMRTStation.getString("車站緯度");
                                    strLng = jsonObjectMRTStation.getString("車站經度");
                                    MRT_station_arrListOfString.add(jsonObjectMRTStation.getInt("Seq") + "." + strStationName +
                                            "\n座標：" + strLat + ", " + strLng);
                                }
                                MRT_station_arrAdapter = new ArrayAdapter<String>(KaohsiungMRTStationsActivity.this,
                                        android.R.layout.simple_list_item_1, MRT_station_arrListOfString);
                                lv_api_data.setAdapter(MRT_station_arrAdapter);
                                pb_spinner.setVisibility(View.INVISIBLE);
                                tv_main.setText(strTitle + "\nAPI資料讀取完成，解析成功！！");
                            } catch (JSONException ex) {
                                pb_spinner.setVisibility(View.INVISIBLE);
                                tv_main.setText(strTitle + "\nAPI資料解析失敗，錯誤訊息：\n" + ex.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            pb_spinner.setVisibility(View.INVISIBLE);
                            tv_main.setText(strTitle + "\n向伺服器取回API資料時發生 VolleyError，錯誤訊息：\n" + volleyError.getMessage());
                        }
                    });
                    Volley.newRequestQueue(KaohsiungMRTStationsActivity.this).add(request);
                    tv_main.setText(strTitle + "\nAPI 資料正在讀取中…");
                    pb_spinner.setVisibility(View.VISIBLE);
                } catch (Exception ex) {
                    tv_main.setText(strTitle + "\n嘗試向伺服器發送API請求失敗，錯誤訊息：\n" + ex.getMessage());
                    pb_spinner.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private void resetTextView(int millisecond) {
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_main.setText(strTitle);
            }
        }, millisecond);
    }
}