package com.sgs.absensi.utils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.sgs.absensi.utils.SessionLogin;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.sgs.absensi.R;
import com.sgs.absensi.view.main.MainActivity;

import org.jetbrains.annotations.*;


public class ProcessLogin extends AppCompatActivity {

    TextInputEditText textInputEditTextUsername,textInputEditTextPassword;
    Button buttonLogin;
    TextView TextViewLogin;
    ProgressBar progressBar;

//    @Override
    protected void Oncreate(Bundle SavedIntanceState){
        super.onCreate(SavedIntanceState);
        setContentView(R.layout.activity_login);

        textInputEditTextUsername = findViewById(R.id.inputNama);
        textInputEditTextPassword = findViewById(R.id.inputPassword);
        buttonLogin = findViewById(R.id.btnLogin);
        TextViewLogin = findViewById(R.id.tvTitle);
        progressBar = findViewById(R.id.imageProfile);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username,password;
                username = String.valueOf(textInputEditTextUsername.getText());
                password = String.valueOf(textInputEditTextPassword.getText());

                if(!username.equals("") && !password.equals("")){
                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    String[] field      = new String[2];
//                                             field[0]   = "username"; // nama lengkap
                                             field[0]   = "user"; // username
                                             field[1]   = "password"; // password
//                                             field[3]   = "description"; // nama lengkap juga
                                    String[] data       = new String[2];
                                    data[0]             = username;
                                    data[1]             = password;

                                    PutData putData = new PutData("http://178.1.77.14/absensi_apps_web/proses_login.php", "POST", field, data);
                                    if(putData.startPut()){
                                        if(putData.onComplete()){
                                            progressBar.setVisibility(View.GONE);
                                            String result = putData.getResult();
                                            if(result.equals("Login Berhasil")){
                                                Toast.makeText(ProcessLogin.this,result, Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
//                                                session.createLoginSession(username);
//                                                finish();
                                            }else{
                                                Toast.makeText(ProcessLogin.this, result, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(ProcessLogin.this, "All Files Required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
