package com.example.hairbysilkeapp

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginLeft
import androidx.lifecycle.Observer
import com.example.hairbysilkeapp.R.color.teal_200
import com.example.hairbysilkeapp.database.BookingRepository
import com.example.hairbysilkeapp.model.BEBooking
import com.example.hairbysilkeapp.model.BECustomer
import com.example.hairbysilkeapp.model.BETreatment
import kotlinx.android.synthetic.main.ny_booking.*
import kotlin.math.absoluteValue

class NyBookingActivity : AppCompatActivity() {
    private var RESULT_CODE = 0
    lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ny_booking)

        spinner = findViewById(R.id.spTreatment)
        setupSpinnerObserver()
        setBookingOnPage()
    }

    private fun setupSpinnerObserver() {
        val mRep = BookingRepository.get()
        val getTreatObserver = Observer<List<BETreatment>>{ treatments ->
            val adapter: SpinnerAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                treatments)
            spinner.adapter = adapter
            Log.d(ContentValues.TAG, "getAll observer notified")
        }
        mRep.getTreatments().observe(this, getTreatObserver)

        //lvBookings.onItemClickListener = AdapterView.OnItemClickListener { parent, view, pos, id -> onClickTreatment(parent,pos)}
    }

    private fun setBookingOnPage() {
        var b = ChosenBooking.getChosenBooking()
        val dynamicBtn = Button(this)
        dynamicBtn.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )
        dynamicBtn.setBackgroundResource(R.drawable.button)
        if (b==null) {
            tvHeader.text == "Opret ny booking"
            dynamicBtn.text = "Tilføj ny booking"
            buttonli.addView(dynamicBtn)
            dynamicBtn.setOnClickListener{createBooking()}

        } else if (b!=null){
            dynamicBtn.text = "Opdater booking"
            buttonli.addView(dynamicBtn)
            tvHeader.text = b?.datetime
            ChosenBooking.getChosenBooking()?.treatmentId?.let { spinner.setSelection(it) }
            etTime.setText(b?.datetime)
            etName.setText(b?.customer.name)
            etPhone.setText(b?.customer.phone)
            etNote.setText(b?.note)
            dynamicBtn.setOnClickListener{updateBooking()}
        }
    }

    fun createBooking(){
        RESULT_CODE = 1
        val mRep = BookingRepository.get()

        var customer = BECustomer(
            0,
            name = etName.text.toString(),
            phone = etPhone.text.toString()
        )
        mRep.insert(customer)

        mRep.insert(BEBooking(
            treatmentName = spinner.selectedItem.toString(),
            id = 0,
            treatmentId = spinner.selectedItemId.toInt(),
            datetime = etTime.text.toString(),
            note = etNote.text.toString(),
            customer = customer
        ))
    }

    fun updateBooking(){
        RESULT_CODE = 1
        var id = ChosenBooking.getChosenBooking()?.id
        var cust = ChosenBooking.getChosenBooking()?.customer
        var treatment = spinner.selectedItem.toString()
        var time = etTime.text.toString()
        var note = etNote.text.toString()
        val mRep = BookingRepository.get()
        mRep.update(BEBooking(id!!, time, cust!!, 0, treatment, note))
    }

    fun onClickCall(view: View){
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${ChosenBooking.getChosenBooking()?.customer?.phone}")
        startActivity(intent)
    }

    override fun onBackPressed() {
        setResult(RESULT_CODE, intent)
        finish()
    }
}