package com.cdms.android.app
import androidx.multidex.MultiDexApplication


class CDMS : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        instance = this

    }

    companion object {
        lateinit var instance: CDMS
    }
}