package com.example.homework2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    SwipeRefreshLayout srl_main;
    ArrayList<String> MRT_station_arrListOfString;
    ArrayAdapter<String> MRT_station_arrAdapter;
    String strAPI_URL = "https://data.kcg.gov.tw/Json/Get/4278fc6a-c3ea-4192-8ce0-40f00cdb40dd";
    String strTitle = "高雄市捷運站資料";
    String strStationName, strLat, strLng;
    // 以下為SQLite用
    private SQLiteDatabase db;
    private SqliteStationDbHelper stationDbHelper;
    private static final String DATATABLE_NAME = "stations";


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
        pb_spinner = findViewById(R.id.pb_spinner);
        srl_main = findViewById(R.id.srl_main);

        MRT_station_arrListOfString = new ArrayList<String>();

        // SQLite資料庫實作
        stationDbHelper = new SqliteStationDbHelper(KaohsiungMRTStationsActivity.this);
        db = stationDbHelper.getWritableDatabase();

        // 下拉重整
        srl_main.setColorSchemeColors(0x0000ff);
        srl_main.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (MRT_station_arrListOfString != null && MRT_station_arrAdapter != null) {
                    MRT_station_arrListOfString.clear();
                    MRT_station_arrAdapter.notifyDataSetChanged();
                }
                btn_get_data.callOnClick();//等同按下更新
                //關閉更新動畫
                srl_main.setRefreshing(false);
            }
        });

        btn_clear_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((MRT_station_arrListOfString != null) && (MRT_station_arrAdapter != null)) {
                    MRT_station_arrListOfString.clear();
                    MRT_station_arrAdapter.notifyDataSetChanged();
                    tv_main.setText(strTitle + "\nAPI 資料已清空…");
                    resetTextView(5000);
                }
            }
        });

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
                                // 儲存至SQLite資料庫用
                                ContentValues cv;
                                for (i = 0; i < jsonArrayData.length(); i++) {
                                    jsonObjectMRTStation = jsonArrayData.getJSONObject(i);
                                    strStationName = jsonObjectMRTStation.getString("車站中文名稱");
                                    strLat = jsonObjectMRTStation.getString("車站緯度");
                                    strLng = jsonObjectMRTStation.getString("車站經度");
                                    MRT_station_arrListOfString.add(jsonObjectMRTStation.getInt("Seq") + "." + strStationName +
                                            "捷運站\n座標：" + strLat + ", " + strLng);
                                    // 儲存至SQLite資料庫
                                    cv = new ContentValues();
                                    cv.put("station_id", jsonObjectMRTStation.getInt("Seq"));
                                    cv.put("station_name", strStationName);
                                    cv.put("station_lat", strLat);
                                    cv.put("station_lng", strLng);
                                    db.insert(DATATABLE_NAME, null, cv);
                                }
                                MRT_station_arrAdapter = new ArrayAdapter<String>(KaohsiungMRTStationsActivity.this,
                                        android.R.layout.simple_list_item_1, MRT_station_arrListOfString);
                                lv_api_data.setAdapter(MRT_station_arrAdapter);
                                pb_spinner.setVisibility(View.GONE);
//                                SystemClock.sleep(5000);
                                showAlert("\nAPI資料讀取完成，解析成功！！",3000);
//                                tv_main.setText(strTitle + "\nAPI資料讀取完成，解析成功！！");
//                                resetTextView(5000);
                                lv_api_data.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        String itemString = MRT_station_arrListOfString.get(position);
                                        int intStartOfGeo = itemString.indexOf("座標");
                                        int intStartOfName = itemString.indexOf(".");
                                        int intEndOfName = itemString.indexOf("\n");
                                        String strNameOfStation = itemString.substring(intStartOfName + 1, intEndOfName);
                                        String strGeoPosition = itemString.substring(intStartOfGeo + 3);
                                        String strArrLatLng[] = strGeoPosition.split(",");
                                        String strLabel = strArrLatLng[0] + "," + strArrLatLng[1] + "(" + strNameOfStation + ")";
                                        String strQuery = Uri.decode(strLabel);
                                        Intent intentGeo = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + strGeoPosition
                                                + "?q=" + strQuery + "z=32"));
                                        startActivity(intentGeo);

                                    }
                                });
                            } catch (JSONException ex) {
                                pb_spinner.setVisibility(View.GONE);
                                tv_main.setText(strTitle + "\nAPI資料解析失敗，錯誤訊息：\n" + ex.getMessage());
//                                resetTextView(5000);
                                dbToListView();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            pb_spinner.setVisibility(View.GONE);
                            tv_main.setText(strTitle + "\n向伺服器取回API資料時發生 VolleyError，錯誤訊息：\n" + volleyError.getMessage());
//                            resetTextView(5000);
                            dbToListView();
                        }
                    });

                    Volley.newRequestQueue(KaohsiungMRTStationsActivity.this).add(request);
                    tv_main.setText(strTitle + "\nAPI 資料正在讀取中…");
                    pb_spinner.setVisibility(View.VISIBLE);

                } catch (Exception ex) {
                    tv_main.setText(strTitle + "\n嘗試向伺服器發送API請求失敗，錯誤訊息：\n" + ex.getMessage());
//                    resetTextView(5000);
                    dbToListView();
                    pb_spinner.setVisibility(View.GONE);
                }
            }
        });

    }


    private void dbToListView() {
        int i, j;
        String[] columnNames;
        Cursor c = db.rawQuery("SELECT * FROM " + DATATABLE_NAME, null);
        columnNames = c.getColumnNames();
        c.moveToFirst();
        for (i = 0; i < c.getCount(); i++) {

            MRT_station_arrListOfString.add(c.getInt(0) + "." +
                    c.getString(1) + "捷運站\n座標：" +
                    c.getString(2) + "," + c.getString(3));
            c.moveToNext();
        }
        MRT_station_arrAdapter = new ArrayAdapter<String>(KaohsiungMRTStationsActivity.this,
                android.R.layout.simple_list_item_1, MRT_station_arrListOfString);
        lv_api_data.setAdapter(MRT_station_arrAdapter);
        showAlert("\n使用SQLite取回離線資料成功！",3000);

    }

    private void showAlert(String msg, int millisec) {
        tv_main.setText(strTitle + msg);
        resetTextView(millisec);
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