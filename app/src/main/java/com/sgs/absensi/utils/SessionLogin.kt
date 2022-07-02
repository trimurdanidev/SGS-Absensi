package com.sgs.absensi.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.sgs.absensi.view.login.LoginActivity


class SessionLogin(var context: Context) {
    var pref: SharedPreferences
    var editor: SharedPreferences.Editor

    fun createLoginSession(nama: String, id: String) {
        editor.putBoolean(IS_LOGIN, true)
        editor.putString(KEY_NAMA, nama)
        editor.putString(KEY_ID,id)
        editor.apply()
    }



    fun checkLogin() {
        if (!isLoggedIn()) {
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    fun logoutUser() {
        editor.clear()
        editor.commit()
        val intent = Intent(context, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun isLoggedIn(): Boolean = pref.getBoolean(IS_LOGIN, false)

    companion object {
        public const val PREF_NAME = "AbsensiPref"
        public const val IS_LOGIN = "IsLoggedIn"
        const val KEY_NAMA = "strNama"
        const val KEY_ID = "intId"
    }

    init {
        pref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE)
        editor = pref.edit()
    }
}