package com.s362106.mappe_2;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.ForeignKey;

@Database(entities = {Contact.class, Appointment.class}, version = 1)

public abstract class AppDatabase extends RoomDatabase {

    public abstract ContactDao contactDao();

    public abstract AppointmentDao appointmentDao();
}
