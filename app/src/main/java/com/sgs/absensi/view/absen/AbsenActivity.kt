package com.sgs.absensi.view.absen


import android.Manifest
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Geocoder
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.location.LocationServices
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.sgs.absensi.BuildConfig
import com.sgs.absensi.R
import com.sgs.absensi.utils.BitmapManager.bitmapToBase64
import com.sgs.absensi.utils.PutData
import com.sgs.absensi.utils.SessionLogin
import com.sgs.absensi.view.main.MainActivity
import com.sgs.absensi.viewmodel.AbsenViewModel
import kotlinx.android.synthetic.main.activity_absen_keluar.btnAbsen
import kotlinx.android.synthetic.main.activity_absen_keluar.imageSelfie
import kotlinx.android.synthetic.main.activity_absen_keluar.inputKeterangan
import kotlinx.android.synthetic.main.activity_absen_keluar.inputLokasi
import kotlinx.android.synthetic.main.activity_absen_keluar.inputNama
import kotlinx.android.synthetic.main.activity_absen_keluar.inputTanggal
import kotlinx.android.synthetic.main.activity_absen_keluar.layoutImage
import kotlinx.android.synthetic.main.activity_absen_keluar.toolbar
import kotlinx.android.synthetic.main.activity_absen_keluar.tvTitle
import kotlinx.android.synthetic.main.activity_absen_masuk.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AbsenActivity : AppCompatActivity() {
    var REQ_CAMERA = 101
    var strCurrentLatitude = 0.0
    var strCurrentLongitude = 0.0
    var strFilePath: String = ""
    var strLatitude = "0"
    var strLongitude = "0"
    lateinit var fileDirectoty: File
    lateinit var imageFilename: File
    lateinit var exifInterface: ExifInterface
    lateinit var strBase64Photo: String
    lateinit var strCurrentLocation: String
    lateinit var strTitle: String
    lateinit var strTitlemenu: String
    lateinit var strTitlemenu2: String
    lateinit var strIIzin: String
    lateinit var tipeAbsen : String
    lateinit var tipeUnit : String
    //    var DATA_TITLE = "STRINGTITLE"
    lateinit var stringNamaKar : String
    lateinit var strTimeStamp: String
    lateinit var strImageName: String
    lateinit var absenViewModel: AbsenViewModel
    lateinit var progressDialog: ProgressDialog
    lateinit var session: SessionLogin


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        strTitlemenu = intent.extras?.getString(DATA_TITLE).toString()
//        Bundle
//        strTitle =
        if(strTitlemenu.equals("Absen Masuk")){
            setContentView(R.layout.activity_absen_masuk)
            setInitLayout()
            setCurrentLocation()
            setUploadData()
        }
        else if (strTitlemenu.equals("Absen Keluar")){
            setContentView(R.layout.activity_absen_keluar)
            setInitLayout()
            setCurrentLocation()
            setUploadData()
        }else if (strTitlemenu.equals("Izin")){
            setContentView(R.layout.activity_izin)
            setInitLayout()
            setCurrentLocation()
            setUploadData()
        }
    }



    private fun setCurrentLocation() {
        progressDialog.show()
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener(this) { location ->
                progressDialog.dismiss()
                if (location != null) {
                    strCurrentLatitude = location.latitude
                    strCurrentLongitude = location.longitude
                    val geocoder = Geocoder(this@AbsenActivity, Locale.getDefault())
                    try {
                        val addressList =
                            geocoder.getFromLocation(strCurrentLatitude, strCurrentLongitude, 1)
                        if (addressList != null && addressList.size > 0) {
                            strCurrentLocation = addressList[0].getAddressLine(0)
                            inputLokasi.setText(strCurrentLocation)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(this@AbsenActivity,
                        "Ups, gagal mendapatkan lokasi. Silahkan periksa GPS atau koneksi internet Anda!",
                        Toast.LENGTH_SHORT).show()
                    strLatitude = "0"
                    strLongitude = "0"
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setInitLayout() {
        progressDialog = ProgressDialog(this)
        strTitle = intent.extras?.getString(DATA_TITLE).toString()
        stringNamaKar = intent.extras?.getString(DATA_USNAME).toString()
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm")
        val formatted = current.format(formatter)

        val handlere = Handler(Looper.getMainLooper())
        handlere.post(
            Runnable {
                val field = arrayOfNulls<String>(1)
                field[0] = "idUser"
                val data = arrayOfNulls<String>(1)
                data[0] = stringNamaKar

                val putData2 = PutData (
                    "http://103.152.119.231/Absensi_apps_web/api_main.php","POST",field,data)
//                    "http://178.1.77.14/absensi_apps_web/api_main.php","POST",field,data)

                if(putData2.startPut()){
                    if(putData2.onComplete()){
                        val getPut = putData2.result
                        Log.i("PutData", getPut)


                        if (strTitle != null) {
                            inputNama.setText(getPut)
                        }

                    }
                }
            }
        );


        if (strTitle != null) {
            tvTitle.text = strTitle
//            inputNama.setText(stringNamaKar)
            inputTanggal.setText(formatted)

        }

        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        absenViewModel = ViewModelProvider(this, (ViewModelProvider.AndroidViewModelFactory
            .getInstance(this.application) as ViewModelProvider.Factory)).get(AbsenViewModel::class.java)

        inputTanggal.setOnClickListener {
            val tanggalAbsen = Calendar.getInstance()
            val date =
                OnDateSetListener { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    tanggalAbsen[Calendar.YEAR] = year
                    tanggalAbsen[Calendar.MONTH] = monthOfYear
                    tanggalAbsen[Calendar.DAY_OF_MONTH] = dayOfMonth
                    val strFormatDefault = "dd MMMM yyyy HH:mm"
                    val simpleDateFormat = SimpleDateFormat(strFormatDefault, Locale.getDefault())
                    inputTanggal.setText(simpleDateFormat.format(tanggalAbsen.time))
                }
            DatePickerDialog(
                this@AbsenActivity, date,
                tanggalAbsen[Calendar.YEAR],
                tanggalAbsen[Calendar.MONTH],
                tanggalAbsen[Calendar.DAY_OF_MONTH]
            ).show()
        }

        layoutImage.setOnClickListener {

            Dexter.withContext(this@AbsenActivity)
                .withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            createImageFile()
                            try {
                                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                cameraIntent.putExtra(
                                    "com.google.assistant.extra.USE_FRONT_CAMERA",
                                    true
                                )
                                cameraIntent.putExtra(
                                    "android.intent.extra.USE_FRONT_CAMERA",
                                    true
                                )
                                cameraIntent.putExtra(
                                    "android.intent.extras.LENS_FACING_FRONT",
                                    1
                                )
                                cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)

                                // Samsung
                                cameraIntent.putExtra("camerafacing", "front")
                                cameraIntent.putExtra("previous_mode", "front")

                                // Huawei
                                cameraIntent.putExtra("default_camera", "1")
                                cameraIntent.putExtra(
                                    "default_mode",
                                    "com.huawei.camera2.mode.photo.PhotoMode"
                                )
                                cameraIntent.putExtra(

                                    MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(
                                        this@AbsenActivity,
                                        BuildConfig.APPLICATION_ID + ".provider",
                                        createImageFile()
                                    )

                                )
                                startActivityForResult(cameraIntent, REQ_CAMERA)
                            } catch (ex: IOException) {
                                Toast.makeText(
                                    this@AbsenActivity,
                                    "Ups, gagal membuka kamera", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                }).check()
        }
    }




    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUploadData() {
        btnAbsen.setOnClickListener {
//            createImageFile()
            strTitlemenu2 = intent.extras?.getString(DATA_TITLE).toString()
            val strNama = inputNama.text.toString()
            val strTanggal = inputTanggal.text.toString()
            val strUnit    = inputUnit.text.toString()
            val setFormatTanggal = DateTimeFormatter.ofPattern("YYYY-mm-dd H:m:s")
            val getTanggal = strTanggal.format(setFormatTanggal)
            val strKeterangan = inputKeterangan.text.toString()

            if (strTitlemenu2.equals("Absen Masuk")) {
                tipeAbsen = "1"
            }else if (strTitlemenu2.equals("Absen Keluar")) {
                tipeAbsen = "2"
            }else
                tipeAbsen = "3"

            if (strUnit.equals("mb") || strUnit.equals("Mb")  || strUnit.equals("MB") || strUnit.equals("martina berto")  || strUnit.equals("Martina Berto") ) {
                tipeUnit = "1"
            }else if (strUnit.equals("sgs") || strUnit.equals("Sgs")  || strUnit.equals("SGS") || strUnit.equals("sinergi global servis")  || strUnit.equals("Sinergi Global Servis") ) {
                tipeUnit = "2"
            }

            if(stringNamaKar == null) {
                Toast.makeText(this@AbsenActivity,
                    "Gagal!!, Data Login Tidak Ditemukan", Toast.LENGTH_SHORT).show()
                session.logoutUser()
                finishAffinity()
            }
            else if (strFilePath.equals(null) || strCurrentLocation.isEmpty()
                || strTanggal.isEmpty() ) {
                Toast.makeText(this@AbsenActivity,
                    "Data tidak boleh ada yang kosong!", Toast.LENGTH_SHORT).show()
            }else if (strUnit.isEmpty()){
                Toast.makeText(this@AbsenActivity,
                    "Data tidak boleh ada yang kosong!", Toast.LENGTH_SHORT).show()
                inputUnit.requestFocus()
            }else if (strKeterangan.isEmpty()){
                Toast.makeText(this@AbsenActivity,
                    "Data tidak boleh ada yang kosong!", Toast.LENGTH_SHORT).show()
                inputKeterangan.requestFocus()
            }else {
                val handler = Handler(Looper.getMainLooper())
                handler.post(
                    Runnable {
                        val field = arrayOfNulls<String>(9)
                        field[0] = "id"
                        field[1] = "uploaded_file"
                        field[2] = "nama"
                        field[3] = "idUnit"
                        field[4] = "tanggal"
                        field[5] = "absenstatus"
                        field[6] = "lokasi"
                        field[7] = "keterangan"
                        field[8] = "created_by"
                        val data = arrayOfNulls<String>(9)
                        data[0] = ""
                        data[1] = imageFilename.toString()
                        data[2] = stringNamaKar
                        data[3] = tipeUnit
                        data[4] = getTanggal
                        data[5] = tipeAbsen
                        data[6] = strCurrentLocation
                        data[7] = strKeterangan
                        data[8] = strNama
                        val putData = PutData(
//                            "http://192.168.1.6/absensi_apps_web/proses_create_absen.php",
                            "http://103.152.119.231/Absensi_apps_web/proses_create_absen.php",
                            "POST",
                            field,
                            data )
                        if (putData.startPut()) {
                            if(putData.onComplete()){
                                val response = putData.result
                                Log.i("PutData",response)

                                    val intent = Intent(applicationContext, MainActivity::class.java)
                                    intent.putExtra(MainActivity.DATA_USNAME, stringNamaKar)
                                    startActivity(intent)
                            }
                        }
                    });
                UploadUtility(this).uploadFile(imageFilename,imageFilename.toString()) // Either Uri, File or String file path
                absenViewModel.addDataAbsen(
                    strBase64Photo,
                    strNama,
                    strTanggal,
                    strCurrentLocation,
                    strKeterangan,
                    strTitlemenu2)
                Toast.makeText(this@AbsenActivity,
                    "Laporan Anda Tersimpan, Silahkan anda cek pada menu History Absen Anda", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        strTimeStamp = SimpleDateFormat("dd MMMM yyyy HH:mm:ss").format(Date())
        strImageName = "IMG_"
        fileDirectoty = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"")
        imageFilename = File.createTempFile(strImageName, ".jpg",fileDirectoty)
        strFilePath = imageFilename.getAbsolutePath()
        return imageFilename
    }

    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        convertImage(strFilePath)
    }

    private fun convertImage(imageFilePath: String?) {
        val imageFile = File(imageFilePath)
        if (imageFile.exists()) {
            val options = BitmapFactory.Options()
            var bitmapImage = BitmapFactory.decodeFile(strFilePath, options)

            try {
                exifInterface = ExifInterface(imageFile.absolutePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90f)
            } else if (orientation == 3) {
                matrix.postRotate(180f)
            } else if (orientation == 8) {
                matrix.postRotate(270f)
            }

            bitmapImage = Bitmap.createBitmap(
                bitmapImage,
                0,
                0,
                bitmapImage.width,
                bitmapImage.height,
                matrix,
                true
            )

            if (bitmapImage == null) {
                Toast.makeText(this@AbsenActivity,
                    "Ups, foto kamu belum ada!", Toast.LENGTH_LONG).show()
            } else {
                val resizeImage = (bitmapImage.height * (512.0 / bitmapImage.width)).toInt()
                val scaledBitmap = Bitmap.createScaledBitmap(bitmapImage, 512, resizeImage, true)

                Glide.with(this)
                    .load(scaledBitmap)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_photo_camera)
                    .into(imageSelfie)
                strBase64Photo = bitmapToBase64(scaledBitmap)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                val intent = intent
                finish()
                startActivity(intent)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val DATA_TITLE = "TITLE"
        const val DATA_USNAME = "USNAME"
    }



}