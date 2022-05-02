package com.example.hairbysilkeapp.database.DAO

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.hairbysilkeapp.model.BEBooking

@Dao
interface BookingDAO {

    @Query("SELECT BEBooking.id, customerId, treatmentId, BETreatment.treatmentName, datetime, note FROM BEBooking INNER JOIN BETreatment ON BEBooking.treatmentId = BETreatment.id")
    fun getBookings(): LiveData<List<BEBooking>>


    @Insert
    fun insert(f: BEBooking)
}