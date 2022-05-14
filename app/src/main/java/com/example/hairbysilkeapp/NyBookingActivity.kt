package com.example.hairbysilkeapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.hairbysilkeapp.database.BookingRepository
import com.example.hairbysilkeapp.model.BEBooking
import kotlinx.android.synthetic.main.ny_booking.*

class NyBookingActivity : AppCompatActivity() {
    private var RESULT_CODE = 0

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ny_booking)

        setBookingOnPage()
    }

    private fun setBookingOnPage() {
        var b = ChosenBooking.getChosenBooking()
        if (b==null) tvHeader.text == "Opret ny booking"
        tvHeader.text = b?.datetime
        etTreatment.setText(b?.treatmentName)
        etTime.setText(b?.datetime)
        etNote.setText(b?.note)
    }

    fun createBooking(view: View){
        RESULT_CODE = 1
        val mRep = BookingRepository.get()
        mRep.insert(BEBooking(
            treatmentName = etTreatment.text.toString(),
            id = 0,
            treatmentId = 0,
            datetime = etTime.text.toString(),
            note = etNote.text.toString(),
            customerId = 0
        ))
    }

    fun updateBooking(view: View){
        RESULT_CODE = 1
        var id = ChosenBooking.getChosenBooking()?.id
        var treatment = etTreatment.text.toString()
        var time = etTime.text.toString()
        var note = etNote.text.toString()
        val mRep = BookingRepository.get()
        mRep.update(BEBooking(id, time, 0, 0, treatment, note))
    }

    override fun onBackPressed() {
        setResult(RESULT_CODE, intent)
        finish()
    }
}