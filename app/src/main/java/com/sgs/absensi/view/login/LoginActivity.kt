package com.sgs.absensi.view.login

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.sgs.absensi.R
import com.sgs.absensi.utils.PutData
import com.sgs.absensi.utils.SessionLogin
import com.sgs.absensi.view.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    lateinit var session: SessionLogin
    lateinit var strNama: String
    lateinit var strPassword: String
    lateinit var setStrID: String
    lateinit var strID : String
    lateinit var nama: String
    var loading: ProgressDialog? = null
    var REQ_PERMISSION = 101
    var builder: AlertDialog.Builder? = null
    var mContext: Context? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setPermission()
        setInitLayout()
    }

    private fun setPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQ_PERMISSION
            )
        }
    }



    private fun setInitLayout() {
        session = SessionLogin(applicationContext)

        if (session.isLoggedIn()) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }

        btnLogin.setOnClickListener {
            strNama = loginName.text.toString()
            strPassword = inputPassword.text.toString()

            if (strNama.isEmpty()) {
                Toast.makeText(
                    this@LoginActivity, "Username harus diisi!",
                    Toast.LENGTH_SHORT
                ).show()
                loginName.requestFocus()
            } else if (strPassword.isEmpty()) {
                Toast.makeText(
                    this@LoginActivity, "Password harus diisi!",
                    Toast.LENGTH_SHORT
                ).show()
                inputPassword.requestFocus()
            } else {
                //Start ProgressBar first (Set visibility VISIBLE)
                val handler = Handler(Looper.getMainLooper())
                handler.post(Runnable {
                    //Starting Write and Read data with URL
                    //Creating array for parameters
                    val field = arrayOfNulls<String>(2)
                    field[0] = "strNama"
                    field[1] = "strPassword"
                    //Creating array for data
                    val data = arrayOfNulls<String>(2)
                    data[0] = strNama
                    data[1] = strPassword
                    val putData = PutData(
                        "http://103.152.119.231/Absensi_apps_web/proses_login.php",
//                        "http://178.1.77.14/absensi_apps_web/proses_login.php",
//                            "http://192.168.1.6/absensi_apps_web/proses_login.php",
                        "POST",
                        field,
                        data
                    )
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            val response2 = putData.result
                            Log.i("PutData", response2)
                            strID = response2.toString()

//                            log.i("dhdhd")

                            if (strID == null || strID.isEmpty() || strID.equals(" ")) {
//                                Toast.makeText(this@LoginActivity, strID, Toast.LENGTH_SHORT).show()
                                Toast.makeText(this@LoginActivity, "Gagal Masuk", Toast.LENGTH_SHORT).show()
                            }else if (strID.equals("java.net.SocketException: Connection reset")) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Gagal Terhubung Ke Server !!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }else{
//                                Toast.makeText(this@LoginActivity, strID, Toast.LENGTH_SHORT).show()
                                Toast.makeText(this@LoginActivity, "Berhasil Masuk", Toast.LENGTH_SHORT).show()
                                val intent = Intent(applicationContext, MainActivity::class.java);
                                intent.putExtra(MainActivity.DATA_USNAME,response2 )
                                startActivity(intent)
                                session.createLoginSession(strNama,strID);
                            }

                        }
                    }
                });

//


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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }



}

private fun Nothing?.execute(s: String, strNama: String, strPassword: String){
}

