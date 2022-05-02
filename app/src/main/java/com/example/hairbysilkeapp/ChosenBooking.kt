package com.example.hairbysilkeapp

import com.example.hairbysilkeapp.model.BEBooking

object ChosenBooking {
    private var chosenContact: BEBooking? = null

    fun setChosenBooking(booking: BEBooking?) {
        this.chosenContact = booking
    }
    fun getChosenBooking(): BEBooking? {
        return chosenContact
    }
}