package com.example.hairbysilkeapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class BECustomer(

    @PrimaryKey(autoGenerate = true) var customerId: Long,
    var name: String, var phone: String)
{
    override fun toString(): String {
        return "$customerId $name"
    }
}