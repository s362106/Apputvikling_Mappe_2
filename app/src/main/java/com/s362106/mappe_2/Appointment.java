package com.s362106.mappe_2;

import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Entity(foreignKeys = @ForeignKey(entity = Contact.class, parentColumns = "ContactId", childColumns = "contactId", onDelete = ForeignKey.CASCADE))
public class Appointment {
    @PrimaryKey(autoGenerate = true)
    public int appointmentId;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "time")
    public String time;

    @ColumnInfo(name = "contactId")
    private int ContactId;

    public int getUid() {
        return appointmentId;
    }

    public void setUid(int id) {
        this.appointmentId = id;
    }

    public int getContactId() {
        return ContactId;
    }

    public void setContactId(int id) {
        this.ContactId = id;
    }

    public int[] getDate() {
        int[] dayMonthYear = new int[3];

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date parsedDate = dateFormat.parse(date);

            if (parsedDate != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(parsedDate);
                dayMonthYear[0] = calendar.get(Calendar.DAY_OF_MONTH);
                dayMonthYear[1] = calendar.get(Calendar.MONTH) + 1;
                dayMonthYear[2] = calendar.get(Calendar.YEAR);

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dayMonthYear;
    }

    public void setDate(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(day, month, year);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String stringDate = dateFormat.format(calendar.getTime());
        this.date = stringDate;
    }

    public void setDateString(String date) {
        this.date = date;
    }

    public void setTimeString(String time) {
        this.time = time;
    }

    public int[] getTime() {
        int[] hourMinute = new int[2];

        try {
             SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
             Date parsedTime = timeFormat.parse(time);

             if (parsedTime != null) {
                 Calendar calendar = Calendar.getInstance();
                 calendar.setTime(parsedTime);
                 hourMinute[0] = calendar.get(Calendar.HOUR_OF_DAY);
                 hourMinute[1] = calendar.get(Calendar.MINUTE);
             }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return hourMinute;
    }

    public void setTime(TimePicker timePicker) {
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        String formattedTime = String.format("%02d:%02d", hour, minute);
        this.time = formattedTime;
    }

    @Override
    public String toString() {
        String ut = "ID: " + getUid() + "\nDate: " + getDate();
        ut += "\nLast Name: " + getTime();
        return ut;
    }
}
