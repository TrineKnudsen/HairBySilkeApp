package com.example.hairbysilkeapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.hairbysilkeapp.model.BEBooking

@Dao
interface BookingDAO {

    @Query("SELECT * FROM BEBooking")
    fun getBookings(): LiveData<List<BEBooking>>


    @Insert
    fun insert(f: BEBooking)
}