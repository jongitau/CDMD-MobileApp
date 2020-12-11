package com.cdms.android.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.cdms.android.CDMS


object PreferenceUtils {

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        val settings = PreferenceManager.getDefaultSharedPreferences(CDMS.instance)
        return settings.getBoolean(key, defValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        val editor = PreferenceManager.getDefaultSharedPreferences(CDMS.instance).edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getInt(key: String, defValue: Int): Int {
        val settings = PreferenceManager.getDefaultSharedPreferences(CDMS.instance)
        return settings.getInt(key, defValue)
    }

    fun putInt(key: String, value: Int) {
        val editor = PreferenceManager.getDefaultSharedPreferences(CDMS.instance).edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getLong(key: String, defValue: Long): Long {
        val settings = PreferenceManager.getDefaultSharedPreferences(CDMS.instance)
        return settings.getLong(key, defValue)
    }

    fun putLong(key: String, value: Long) {
        val editor = PreferenceManager.getDefaultSharedPreferences(CDMS.instance).edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getString(key: String, defValue: String?): String? {
        val settings = PreferenceManager.getDefaultSharedPreferences(CDMS.instance)
        return settings.getString(key, defValue)
    }

    fun putString(key: String, value: String) {
        val editor = PreferenceManager.getDefaultSharedPreferences(CDMS.instance).edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun remove(key: String) {
        val editor = PreferenceManager.getDefaultSharedPreferences(CDMS.instance).edit()
        editor.remove(key)
        editor.apply()
    }

}
