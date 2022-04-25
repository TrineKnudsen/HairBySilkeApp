package com.example.hairbysilkeapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class BEBooking (

    @PrimaryKey(autoGenerate = true) var id: Int,
    var datetime: String, var customer: String, var treatment: String,
    var note: String
    ) {

        public override fun toString(): String {
            return "$datetime $customer - $treatment"
        }

}