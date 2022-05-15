package com.example.hairbysilkeapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.hairbysilkeapp.database.DAO.BookingDAO
import com.example.hairbysilkeapp.database.DAO.CustomerDAO
import com.example.hairbysilkeapp.database.DAO.TreatmentDAO
import com.example.hairbysilkeapp.model.BEBooking
import com.example.hairbysilkeapp.model.BECustomer
import com.example.hairbysilkeapp.model.BETreatment

@Database(entities = [ BETreatment::class, BEBooking::class, BECustomer::class], version = 1)
public abstract class BookingDatabase : RoomDatabase(){
    abstract fun treatmentDAO(): TreatmentDAO
    abstract fun bookingDAO(): BookingDAO
    abstract fun customerDAO(): CustomerDAO
}