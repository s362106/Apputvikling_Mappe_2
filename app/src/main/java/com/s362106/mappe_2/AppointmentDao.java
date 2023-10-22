package com.s362106.mappe_2;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AppointmentDao {
    @Query("SELECT * FROM Appointment")
    List<Appointment> getAll();
    @Query("SELECT * FROM Appointment WHERE AppointmentId = :uid")
    Appointment getObjectByUID(String uid);
    @Insert
    void newAppointment(Appointment appointment);
    @Delete
    void delete(Appointment appointment);
    @Update
    void update(Appointment appointment);

}