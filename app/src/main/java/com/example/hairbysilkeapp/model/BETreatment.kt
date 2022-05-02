package com.example.hairbysilkeapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class BETreatment (
    @PrimaryKey(autoGenerate = true) var id: Int,
    var treatmentName: String, var price: String
        ) {
    public override fun toString(): String {
        return "$treatmentName"
    }
}