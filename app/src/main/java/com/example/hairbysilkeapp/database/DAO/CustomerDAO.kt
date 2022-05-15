package com.example.hairbysilkeapp.database.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.hairbysilkeapp.model.BECustomer

@Dao
interface CustomerDAO {
    @Insert
    fun insert(c: BECustomer)
}