package com.example.hairbysilkeapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginLeft
import androidx.lifecycle.Observer
import com.example.hairbysilkeapp.R.color.teal_200
import com.example.hairbysilkeapp.database.BookingRepository
import com.example.hairbysilkeapp.model.BEBooking
import com.example.hairbysilkeapp.model.BECustomer
import com.example.hairbysilkeapp.model.BETreatment
import kotlinx.android.synthetic.main.ny_booking.*
import java.util.jar.Manifest
import kotlin.math.absoluteValue

class NyBookingActivity : AppCompatActivity() {
    private var RESULT_CODE = 0
    lateinit var spinner: Spinner
    val PERMISSION_REQUEST_CODE = 1

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

    fun onClickCancel(view: View){
        val mRep = BookingRepository.get()
        var id = ChosenBooking.getChosenBooking()?.id
        mRep.cancelBooking(id)
        onClickMessage()

    }

    fun onClickMessage() {
        var alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("SMS")
        alertDialogBuilder
            .setMessage("Skal SMS om aflysning sendes automatisk?")
            .setCancelable(true)
            .setPositiveButton("Ja") { _, _ -> sendSMSDirect() }
            .setNegativeButton("Nej - gå til SMS app") {_,_ -> startSMSActivity()}
        var alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun sendSMSDirect(){
        Toast.makeText(this, "En sms om aflysning bliver sendt", Toast.LENGTH_LONG)
            .show()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.SEND_SMS) ==
                    PackageManager.PERMISSION_DENIED) {
                Log.d(TAG, "permission denied to SEND_SMS - requesting it")
                val permissions = arrayOf(android.Manifest.permission.SEND_SMS)
                requestPermissions(permissions, PERMISSION_REQUEST_CODE)
                return
            } else Log.d(TAG, "permission to SEND_SMS granted")
        } else Log.d(TAG, "Runtime permission not needed")
    }

    private fun startSMSActivity(){
        val sendIntent = Intent(Intent.ACTION_VIEW)
        sendIntent.data = Uri.parse("sms:${ChosenBooking.getChosenBooking()?.customer?.phone }")
        startActivity(sendIntent)
    }

    override fun onBackPressed() {
        setResult(RESULT_CODE, intent)
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "Permission: " + permissions[0] + " - grantResult: " + grantResults[0])
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sendMessage()
        }
    }

    private fun sendMessage() {
        val m = SmsManager.getDefault()
        val text = "Hej ${ChosenBooking.getChosenBooking()!!.customer.name}." +
                "Din frisør tid ${ChosenBooking.getChosenBooking()!!.datetime} er desværre blevet aflyst." +
                "Ring til os hvis du har spørgsmål, eller for at aftale en ny tid." +
                "Venlig hilsen Silke"
        m.sendTextMessage(ChosenBooking.getChosenBooking()!!.customer.phone,null, text, null, null)
    }
}