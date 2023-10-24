package com.s362106.mappe_2;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactDao {
    @Insert
    void newContact(Contact contact);

    @Delete
    void deleteContact(Contact contact);

    @Update
    void updateContact(Contact contact);

    @Query("SELECT * FROM Contact")
    List<Contact> getAllContacts();

    @Query("SELECT * FROM Contact WHERE ContactId=:contactId")
    Contact getContact(final int contactId);

    @Query("SELECT COUNT(*) FROM contact WHERE ContactId <= :contactId")
    int getPositionByContactId(int contactId);
}
