package com.example.hairbysilkeapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.hairbysilkeapp.model.BEBooking

@Database(entities = [BEBooking::class], version = 1)
abstract class BookingDatabase : RoomDatabase(){
    abstract fun bookingDAO(): BookingDAO
}