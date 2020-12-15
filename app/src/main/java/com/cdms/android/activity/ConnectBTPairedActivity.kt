package com.cdms.android.activity

import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.cdms.android.Global
import com.cdms.android.R
import com.cdms.android.service.WorkService
import java.lang.ref.WeakReference
import java.util.*

class ConnectBTPairedActivity : Activity(), OnItemClickListener {
    private var dialog: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_paired_printer)

        boundedPrinters = boundedPrinters
        listView =
            findViewById<View>(R.id.listViewSettingConnect) as ListView
        listView!!.adapter = SimpleAdapter(
            this, boundedPrinters,
            R.layout.item_bt, arrayOf(
                ICON,
                PRINTERNAME,
                PRINTERMAC
            ), intArrayOf(
                R.id.btListItemPrinterIcon, R.id.tvListItemPrinterName,
                R.id.tvListItemPrinterMac
            )
        )
        listView!!.onItemClickListener = this
        mHandler = MHandler(this)
        WorkService.addHandler(mHandler)
    }

    override fun onDestroy() {
        super.onDestroy()
        WorkService.delHandler(mHandler)
        mHandler = null
    }

    override fun onItemClick(
        arg0: AdapterView<*>?, arg1: View, position: Int,
        id: Long
    ) {
        // TODO Auto-generated method stub
        val address =
            boundedPrinters?.get(position)?.get(PRINTERMAC) as String?

        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setTitle("Connecting")
        alertDialog.setMessage(Global.toast_connecting.toString() + address)
        dialog = alertDialog.create()
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.show()

        WorkService.workThread.connectBt(address)
    }// Add the name and address to an array adapter to show in a
    // ListView
    // Toast.makeText(this,
    // ""+device.getBluetoothClass().getMajorDeviceClass(),
    // Toast.LENGTH_LONG).show();
// Loop through paired devices

    // Device does not support Bluetooth
    // If there are paired devices
    private var boundedPrinters: List<Map<String, Any?>>? = null
        get() {
            val list: MutableList<Map<String, Any?>> =
                ArrayList()
            val mBluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter()
                ?: // Device does not support Bluetooth
                return list
            val pairedDevices = mBluetoothAdapter
                .bondedDevices
            // If there are paired devices
            if (pairedDevices.size > 0) {
                // Loop through paired devices
                for (device in pairedDevices) {
                    // Add the name and address to an array adapter to show in a
                    // ListView
                    val map: MutableMap<String, Any?> =
                        HashMap()
                    map[ICON] = R.drawable.ic_baseline_bluetooth_24
                    // Toast.makeText(this,
                    // ""+device.getBluetoothClass().getMajorDeviceClass(),
                    // Toast.LENGTH_LONG).show();
                    map[PRINTERNAME] = device.name
                    map[PRINTERMAC] = device.address
                    list.add(map)
                }
            }
            return list
        }

    internal class MHandler(activity: ConnectBTPairedActivity) : Handler() {
        private var mActivity: WeakReference<ConnectBTPairedActivity> = WeakReference(activity)
        override fun handleMessage(msg: Message) {
            val theActivity = mActivity.get()
            when (msg.what) {
                Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT -> {
                    val result = msg.arg1
                    Toast.makeText(
                        theActivity,
                        if (result == 1) Global.toast_success else Global.toast_fail,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.v(
                        TAG,
                        "Connect Result: $result"
                    )
                    theActivity!!.dialog!!.dismiss()
                    theActivity.finish()
                }
            }
        }
    }

    companion object {
        private var listView: ListView? = null
        const val ICON = "ICON"
        const val PRINTERNAME = "PRINTERNAME"
        const val PRINTERMAC = "PRINTERMAC"
        private var boundedPrinters: List<Map<String, Any?>>? =
            null
        private var mHandler: Handler? = null
        private const val TAG = "ConnectBTMacActivity"
    }
}
