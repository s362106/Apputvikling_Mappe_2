package com.s362106.mappe_2;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Contact {
    @PrimaryKey(autoGenerate = true)
    public int ContactId;
    @ColumnInfo(name = "first_name")
    public String firstName;
    @ColumnInfo(name = "last_name")
    public String lastName;
    @ColumnInfo(name = "phone_number")
    public String phoneNumber;

    public int getContactId() {
        return ContactId;
    }

    public void setContactId(int contactId) {
        ContactId = contactId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        String ut = "ID: " + getContactId() + "\nFirst Name: " + getFirstName();
        if (!getLastName().isEmpty()) {
            ut += "\nLast Name: " + getLastName();
        }
         ut += "\nPhone Number: " + getPhoneNumber() + "\n";
        return ut;
    }
}
