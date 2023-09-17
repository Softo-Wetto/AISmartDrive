package com.example.aismartdrive.Utils;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.room.Room;
import com.example.aismartdrive.DB.AppDatabase;

public class MyApp extends Application {
    public static final String PREF_NAME = "MyAppPrefs";
    private static SharedPreferences preferences;

    private static AppDatabase appDatabase;
    private static String DATABASE_NAME =
            "AISMARTDRIVE_DATABASE";

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences(PREF_NAME,
                MODE_PRIVATE);
        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, DATABASE_NAME).build();
    }
    public static SharedPreferences getPreferences() {
        return preferences;
    }

    public static AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
