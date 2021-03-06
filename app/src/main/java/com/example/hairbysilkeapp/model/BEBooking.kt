package com.example.hairbysilkeapp.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File

@Entity
class BEBooking(

    @PrimaryKey(autoGenerate = true) var id: Long,
    var datetime: String,
    @Embedded var customer: BECustomer,
    var treatmentId: Int,
    var treatmentName: String,
    var note: String,
    var pic: String
    ) {

        public override fun toString(): String {
            return "$id $datetime $treatmentName"
        }
}