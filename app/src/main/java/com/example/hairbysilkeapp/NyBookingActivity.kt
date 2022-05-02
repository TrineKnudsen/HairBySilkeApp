package com.example.hairbysilkeapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        etBehandling.setText(b?.treatmentName)
        etTidspunkt.setText(b?.datetime)
        etNote.setText(b?.note)
    }

    override fun onBackPressed() {
        setResult(RESULT_CODE, intent)
        finish()
    }
}