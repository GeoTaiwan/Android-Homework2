package com.example.homework2;
import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// 宣告裡面有哪幾張表 (entities)，目前版本號是 1
@Database(entities = {DataItem.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // 綁定剛剛寫好的 DAO
    public abstract MrtDao mrtDao();

    private static volatile AppDatabase INSTANCE;

    // 單例模式：確保全域共用同一個實體
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "kaohsiung_mrt_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}