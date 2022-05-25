package com.example.hairbysilkeapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.example.hairbysilkeapp.database.BookingRepository
import com.example.hairbysilkeapp.model.BEBooking
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 1
    val REQUEST_RESULT = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BookingRepository.initialize(this)

        setupDataObserver()
        checkPermissions()
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
        startActivityForResult(nyAftale, REQUEST_RESULT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ChosenBooking.setChosenBooking(null)
    }

    fun onClickBrowser(view: View){
        val url = "http://www.google.dk"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    /**
     * Diverse
     */
    //Checks if the app has the required permissions, and promps the user with the ones missing.
    private fun checkPermissions(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        val permissions = mutableListOf<String>()
        if (!isGranted(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (!isGranted(android.Manifest.permission.CAMERA)) permissions.add(android.Manifest.permission.CAMERA)
        if (permissions.size > 0) ActivityCompat.requestPermissions(this, permissions.toTypedArray(), PERMISSION_REQUEST_CODE)
    }

    private fun isGranted(permission: String): Boolean =
        ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

}
