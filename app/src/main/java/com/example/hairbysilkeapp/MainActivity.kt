package com.example.hairbysilkeapp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import androidx.lifecycle.Observer
import com.example.hairbysilkeapp.database.BookingRepository
import com.example.hairbysilkeapp.model.BEBooking
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BookingRepository.initialize(this)
        insertTestData()

        setupDataObserver()
    }

    private fun insertTestData(){
        val mRep = BookingRepository.get()
        mRep.insert(BEBooking(0, "1208", "Tine", "Balayage", "ole@mail.com"))
        mRep.insert(BEBooking(0, "Katrine", "12344678", ":(", "Katrine@mail.com"))
        }

    private fun setupDataObserver() {
        val mRep = BookingRepository.get()
        val getAllObserver = Observer<List<BEBooking>>{ bookings ->
            val adapter: ListAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                bookings)
            lvBookings.adapter = adapter
            Log.d(TAG, "getAll observer notified")
        }
        mRep.getBookings().observe(this, getAllObserver)

        //lvNames.onItemClickListener = AdapterView.OnItemClickListener { parent, view, pos, id -> onClickPerson(parent, pos)}
    }

    fun onClickNyAftale(view: View) {
        val nyAftale = Intent(this, NyBookingActivity::class.java)
        startActivity(nyAftale)

    }
}