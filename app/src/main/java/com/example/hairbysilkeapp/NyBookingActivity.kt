package com.example.hairbysilkeapp

import android.content.ContentValues
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
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import com.example.hairbysilkeapp.database.BookingRepository
import com.example.hairbysilkeapp.model.BEBooking
import com.example.hairbysilkeapp.model.BECustomer
import com.example.hairbysilkeapp.model.BETreatment
import kotlinx.android.synthetic.main.ny_booking.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class NyBookingActivity : AppCompatActivity() {
    private var RESULT_CODE = 1
    lateinit var spinner: Spinner
    val PERMISSION_REQUEST_CODE = 1
    var mFile: File? = null

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
            dynamicBtn.text = "Tilf??j ny booking"
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
            dynamicBtn.setOnClickListener{updateBooking()
            imgView.setImageURI(b?.pic?.toUri())}
        }
    }

    fun createBooking(){
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
            customer = customer,
            pic = mFile?.absolutePath.toString()
        ))
    }

    fun updateBooking(){
        var id = ChosenBooking.getChosenBooking()?.id
        var cust = ChosenBooking.getChosenBooking()?.customer
        var treatment = spinner.selectedItem.toString()
        var time = etTime.text.toString()
        var note = etNote.text.toString()
        val mRep = BookingRepository.get()
        mRep.update(BEBooking(id!!, time, cust!!, 0, treatment, note,mFile?.toUri().toString()))
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
            .setNegativeButton("Nej - g?? til SMS app") {_,_ -> startSMSActivity()}
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

    /**
     *  File
     */
    fun onTakeByFile(view: View) {
        mFile = getOutputMediaFile("Camera01") // create a file to save the image

        if (mFile == null) {
            Toast.makeText(this, "Could not create file...", Toast.LENGTH_LONG).show()
            return
        }

        // create Intent to take a picture
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Add extra to inform the app where to put the image.
        val applicationId = "com.example.hairbysilkeapp"
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(
            this,
            "${applicationId}.provider",
            mFile!!))

        fileCallback.launch(intent)
    }

    val fileCallback = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ activityResult ->
        val mImage = findViewById<ImageView>(R.id.imgView)
        if (activityResult.resultCode == RESULT_OK)
            showImageFromFile(mImage, mFile!!)
        else handleOther(activityResult.resultCode)
    }

    private fun showImageFromFile(img: ImageView, f: File) {
        img.setImageURI(Uri.fromFile(f))
        img.setBackgroundColor(Color.RED)
    }

    // return a new file with a timestamp name in a folder named [folder] in
    // the external directory for pictures.
    // Return null if the file cannot be created
    private fun getOutputMediaFile(folder: String): File? {
        // in an emulated device you can see the external files in /sdcard/Android/data/<your app>.
        val mediaStorageDir = File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), folder)
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory")
                return null
            }
        }

        // Create a media file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val postfix = "jpg"
        val prefix = "IMG"
        return File(mediaStorageDir.path +
                File.separator + prefix +
                "_" + timeStamp + "." + postfix)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "Permission: " + permissions[0] + " - grantResult: " + grantResults[0])
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sendMessage()
        }

        var cameraGranted = true
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (i in 0..permissions.size -1) {
                if (permissions[i] == android.Manifest.permission.CAMERA && grantResults[i] == PackageManager.PERMISSION_DENIED)
                    cameraGranted = false
            }
        }
        if (!cameraGranted) {
            val btnByFile = findViewById<Button>(R.id.btnPic)
            btnByFile.isEnabled = false
        }
    }

    private fun sendMessage() {
        val m = SmsManager.getDefault()
        val text = "Hej ${ChosenBooking.getChosenBooking()!!.customer.name}." +
                "Din fris??r tid ${ChosenBooking.getChosenBooking()!!.datetime} er desv??rre blevet aflyst." +
                "Ring til os hvis du har sp??rgsm??l, eller for at aftale en ny tid." +
                "Venlig hilsen Silke"
        m.sendTextMessage(ChosenBooking.getChosenBooking()!!.customer.phone,null, text, null, null)
    }

    private fun handleOther(resultCode: Int){
        if (resultCode == RESULT_CANCELED)
            Toast.makeText(this, "Annulleret...", Toast.LENGTH_LONG).show()
    }
}