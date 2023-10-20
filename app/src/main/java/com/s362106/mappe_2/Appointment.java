package com.s362106.mappe_2;

import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

public class Appointment {
    @PrimaryKey(autoGenerate = true)
    public int appointmentId;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "time")
    public String time;

    public int getUid() {
        return appointmentId;
    }

    public void setUid(int id) {
        this.appointmentId = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
