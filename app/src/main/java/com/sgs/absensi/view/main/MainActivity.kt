package com.sgs.absensi.view.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.sgs.absensi.R
import com.sgs.absensi.utils.PutData
import com.sgs.absensi.utils.SessionLogin
import com.sgs.absensi.utils.SessionLogin.Companion.KEY_ID
import com.sgs.absensi.view.absen.AbsenActivity
import com.sgs.absensi.view.history.HistoryActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    lateinit var strTitle: String
    lateinit var strTitle2: String
    lateinit var strDataUsname: String
    lateinit var count : String
    lateinit var count2 : String
    lateinit var countStr : String
    lateinit var session: SessionLogin
    lateinit var setUserlogin : String
    var KEY_NAMA = "strNama"
    lateinit var sharedPref : SharedPreferences
    lateinit var pref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var namanay :String
    lateinit var idNya : String
    lateinit var titleName : String


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setInitLayout()

        val localDate = LocalDateTime.now()
        val formatDate = DateTimeFormatter.ofPattern("dd MMMM yyyy - HH:mm")
        val setDateNow = localDate.format(formatDate)
        dateNow.text =  setDateNow.toString()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setInitLayout() {
        pref = getSharedPreferences(SessionLogin.PREF_NAME, MODE_PRIVATE)
        namanay = pref.getString(KEY_NAMA,"null").toString()
        idNya   = pref.getString(KEY_ID,"null").toString()
//        logintitleName.text =  "Hallo Kak "+namanay+"-"+idNya

        session = SessionLogin(this)
        session.checkLogin()
        strDataUsname = idNya
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-M-dd")
        val formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatted = current.format(formatter)
        val formatted2 = current.format(formatter2)
//
        val handler = Handler(Looper.getMainLooper())
        handler.post(
            Runnable {
                val field = arrayOfNulls<String>(1)
                    field[0] = "idUser"
                val data = arrayOfNulls<String>(1)
                    data[0] = strDataUsname

                val putData2 = PutData (
//                    "http://192.168.1.6/absensi_apps_web/api_main.php","POST",field,data)
                "http://103.152.119.231/Absensi_apps_web/api_main.php","POST",field,data)

                if(putData2.startPut()){
                    if(putData2.onComplete()){
                        val getPut = putData2.result
                        Log.i("PutData", getPut)
                        titleName = getPut.toString()
//
                        if (titleName != null || !titleName.equals(" ")) {
//                            logintitleName.text = "Hallo Kak "+getPut
                                logintitleName.text =  "Hallo Kak "+titleName
                                            }

                    }
                }

            }
        );


        cvAbsenMasuk.setOnClickListener {
            strTitle = "Absen Masuk"
            val handler = Handler(Looper.getMainLooper())
            handler.post(Runnable {
                //Starting Write and Read data with URL
                //Creating array for parameters
                val field2 = arrayOfNulls<String>(3)
                field2[0] = "nama"
                field2[1] = "tanggal"
                field2[2] = "title"
                //Creating array for data
                val data2 = arrayOfNulls<String>(3)
                data2[0] = strDataUsname
                data2[1] = formatted2
                data2[2] = strTitle
                val putData2 = PutData(
                        "http://103.152.119.231/Absensi_apps_web/proses_check_absen.php",
                    "POST",
                    field2,
                    data2
                )
                if (putData2.startPut()) {
                    if (putData2.onComplete()) {
                        val response_mn_masuk = putData2.result
                        //End ProgressBar (Set visibility to GONE)
                        Log.i("PutData2", response_mn_masuk)
                        count = response_mn_masuk.toString()

//                        Toast.makeText(this@MainActivity, putData2, Toast.LENGTH_SHORT).show()
                        if(response_mn_masuk.equals(" 0")){
                            val intent = Intent(this@MainActivity, AbsenActivity::class.java)
                            intent.putExtra(AbsenActivity.DATA_TITLE, strTitle)
                            intent.putExtra(AbsenActivity.DATA_USNAME,strDataUsname)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this@MainActivity, "Upps, Anda Sudah Absen Masuk Hari ini !", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            });

        }

        cvAbsenKeluar.setOnClickListener {
            strTitle2 = "Absen Keluar"
            val handler = Handler(Looper.getMainLooper())
            handler.post(Runnable {
                //Starting Write and Read data with URL
                //Creating array for parameters
                val field3 = arrayOfNulls<String>(3)
                field3[0] = "nama"
                field3[1] = "tanggal"
                field3[2] = "title"
                //Creating array for data
                val data3 = arrayOfNulls<String>(3)
                data3[0] = strDataUsname
                data3[1] = formatted2
                data3[2] = strTitle2
                val putData3 = PutData(
                    "http://103.152.119.231/Absensi_apps_web/proses_check_absen_out.php",
                    "POST",
                    field3,
                    data3
                )
                if (putData3.startPut()) {
                    if (putData3.onComplete()) {
                        val response_mn_keluar = putData3.result
                        //End ProgressBar (Set visibility to GONE)
                        Log.i("PutData3", response_mn_keluar)
                        count2 = response_mn_keluar.toString()

//                        Toast.makeText(this@MainActivity, count2, Toast.LENGTH_LONG).show()
                        if(response_mn_keluar.equals(" 0")){
//                            System.out.println("Absen Bisa")
                            val intent = Intent(this@MainActivity, AbsenActivity::class.java)
                            intent.putExtra(AbsenActivity.DATA_TITLE, strTitle2)
                            intent.putExtra(AbsenActivity.DATA_USNAME,strDataUsname)
                            startActivity(intent)

                        }else{
//                            System.out.println("Absen Ngga bisa")
                            Toast.makeText(this@MainActivity, "Upps, Anda Sudah Absen Keluar Hari ini !", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            });
        }

        cvPerizinan.setOnClickListener {
//            Toast.makeText(this@MainActivity, "Belum Tersedia ", Toast.LENGTH_SHORT).show()
            strTitle = "Izin"
            val intent = Intent(this@MainActivity, AbsenActivity::class.java)
            intent.putExtra(AbsenActivity.DATA_TITLE, strTitle)
            intent.putExtra(AbsenActivity.DATA_USNAME,strDataUsname)
            startActivity(intent)
        }

        cvHistory.setOnClickListener {
            val intent = Intent(this@MainActivity, HistoryActivity::class.java)
            startActivity(intent)
        }

        imageLogout.setOnClickListener {
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setMessage("Yakin Anda ingin Logout?")
            builder.setCancelable(true)
            builder.setNegativeButton("Batal") { dialog, which -> dialog.cancel() }
            builder.setPositiveButton("Ya") { dialog, which ->
                session.logoutUser()
                finishAffinity()
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }


    }

    companion object {
        const val DATA_USNAME = "USNAME"

    }
}