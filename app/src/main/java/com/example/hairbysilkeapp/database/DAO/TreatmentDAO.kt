package com.example.hairbysilkeapp.database.DAO

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.hairbysilkeapp.model.BETreatment

@Dao
interface TreatmentDAO {

    @Query("SELECT * FROM BETreatment")
    fun getTreatments(): LiveData<List<BETreatment>>

    @Insert
    fun insert(f: BETreatment)
}