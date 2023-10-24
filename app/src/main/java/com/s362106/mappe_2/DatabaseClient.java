package com.s362106.mappe_2;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {
    private Context mCTx;
    private static DatabaseClient mInstance;

    private AppDatabase appDatabase;

    private DatabaseClient(Context mCTx) {
        this.mCTx = mCTx;
        appDatabase = Room.databaseBuilder(mCTx, AppDatabase.class, "MyAppData").build();
    }

    public static synchronized DatabaseClient getInstance(Context mCTx) {
        if (mInstance == null) {
            mInstance = new DatabaseClient(mCTx);
        }
        return mInstance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
