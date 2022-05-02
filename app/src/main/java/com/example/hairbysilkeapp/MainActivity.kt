package com.example.hairbysilkeapp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.SimpleAdapter
import androidx.core.view.get
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.hairbysilkeapp.database.BookingRepository
import com.example.hairbysilkeapp.model.BEBooking
import com.example.hairbysilkeapp.model.BETreatment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val REQUEST_RESULT = 1


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BookingRepository.initialize(this)
        insertTestData()

        setupDataObserver()
    }

    private fun insertTestData(){
        val mRep = BookingRepository.get()
        mRep.insert(BETreatment(0, "Balayage", "123"))
        mRep.insert(BEBooking(0, "120895", 1, 1, "","Har mørkt hår"))
        mRep.insert(BEBooking(0, "120895", 2, 1, "","Har lyst hår"))
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

        lvBookings.onItemClickListener = AdapterView.OnItemClickListener { parent, view, pos, id -> onClickBooking(parent,pos)}
    }

    private fun onClickBooking(listview: AdapterView<*>,pos: Int) {
        val booking = listview.getItemAtPosition(pos) as BEBooking
        ChosenBooking.setChosenBooking(booking)
        val intent = Intent(this, NyBookingActivity::class.java)

        startActivityForResult(intent, REQUEST_RESULT)
    }

    fun onClickNyAftale(view: View) {
        val nyAftale = Intent(this, NyBookingActivity::class.java)
        startActivity(nyAftale)
    }
}
