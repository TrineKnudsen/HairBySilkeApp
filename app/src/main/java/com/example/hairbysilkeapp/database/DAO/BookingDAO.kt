package com.example.hairbysilkeapp.database.DAO

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.hairbysilkeapp.model.BEBooking

@Dao
interface BookingDAO {

    @Query("SELECT * from BEBooking order by id")
    fun getBookings(): LiveData<List<BEBooking>>


    @Insert
    fun insert(f: BEBooking)

    @Update
    fun update(b: BEBooking)

    @Query("DELETE FROM BEBooking WHERE id = (:id)")
    fun delete(id: Long)
}