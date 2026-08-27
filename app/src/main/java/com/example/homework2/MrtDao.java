package com.example.homework2;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface MrtDao {

    // 【新增】一次塞入整個 List。如果主鍵重複，直接覆蓋 (REPLACE)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<DataItem> stations);

    // 【查詢】撈出所有捷運站資料
    @Query("SELECT * FROM mrt_stations")
    List<DataItem> getAllStations();

    // 【刪除】清空資料表
    @Query("DELETE FROM mrt_stations")
    void deleteAll();
}