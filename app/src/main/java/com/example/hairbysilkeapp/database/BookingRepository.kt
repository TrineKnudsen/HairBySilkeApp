package com.example.hairbysilkeapp.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.hairbysilkeapp.model.BEBooking
import java.lang.IllegalStateException
import java.util.concurrent.Executors

class BookingRepository private constructor(private val context: Context){

    private val database: BookingDatabase = Room.databaseBuilder(context.applicationContext,
        BookingDatabase::class.java,"booking-database").build()

    private val bookingDAO = database.bookingDAO()

    fun getBookings(): LiveData<List<BEBooking>> = bookingDAO.getBookings()

    private val executor = Executors.newSingleThreadExecutor()

    fun insert(f: BEBooking) {
        executor.execute{bookingDAO.insert(f)}
    }

    companion object {
        private var INSTANCE: BookingRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = BookingRepository(context)
            }
        }

        fun get(): BookingRepository {
            if (INSTANCE != null) return INSTANCE!!

            throw IllegalStateException("FriendRepository must be initialized")
        }
    }

}