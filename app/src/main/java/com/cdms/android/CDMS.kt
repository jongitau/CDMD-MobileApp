package com.cdms.android
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