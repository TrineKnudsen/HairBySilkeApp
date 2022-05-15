package com.example.hairbysilkeapp.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.hairbysilkeapp.model.BEBooking
import com.example.hairbysilkeapp.model.BETreatment
import java.lang.IllegalStateException
import java.util.concurrent.Executors

class BookingRepository private constructor(private val context: Context){

    private val database: BookingDatabase = Room.databaseBuilder(context.applicationContext,
        BookingDatabase::class.java,"booking-database").build()

    private val bookingDAO = database.bookingDAO()
    private val treatmentDAO = database.treatmentDAO()

    fun getBookings(): LiveData<List<BEBooking>> = bookingDAO.getBookings()
    fun getTreatments(): LiveData<List<BETreatment>> = treatmentDAO.getTreatments()

    private val executor = Executors.newSingleThreadExecutor()

    fun insert(f: BEBooking) {
        executor.execute{bookingDAO.insert(f)}
    }

    fun update(b: BEBooking) {
        executor.execute{bookingDAO.update(b)}
    }

    fun insert(f: BETreatment){
        executor.execute{treatmentDAO.insert(f)}
    }

    companion object {
        private var INSTANCE: BookingRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = BookingRepository(context)
                INSTANCE!!.insert(
                    BETreatment(
                    0,
                    "Balayage",
                    1200
                ))
                INSTANCE!!.insert(
                    BETreatment(
                        0,
                        "Dame klip",
                        500
                    ))
                INSTANCE!!.insert(
                    BETreatment(
                        0,
                        "Mand klip",
                        400
                    ))
                INSTANCE!!.insert(
                    BETreatment(
                        0,
                        "Retning af bryn",
                        150
                    ))
            }
        }

        fun get(): BookingRepository {
            if (INSTANCE != null) return INSTANCE!!

            throw IllegalStateException("FriendRepository must be initialized")
        }
    }
}