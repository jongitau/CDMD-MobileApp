package com.cdms.android.activity

import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import com.cdms.android.Global
import com.cdms.android.R
import com.cdms.android.service.WorkService
import com.cdms.android.utils.DataUtils
import java.lang.ref.WeakReference


class SearchBTActivity : Activity(), View.OnClickListener {
    private var linearLayoutDevices: LinearLayout? = null
    private var progressBarSearchStatus: ProgressBar? = null
    private var dialog: ProgressDialog? = null
    private var broadcastReceiver: BroadcastReceiver? = null
    private var intentFilter: IntentFilter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_bt)
        findViewById<View>(R.id.buttonSearch).setOnClickListener(this)
        progressBarSearchStatus =
            findViewById<View>(R.id.progressBarSearchStatus) as ProgressBar
        linearLayoutDevices =
            findViewById<View>(R.id.linearlayoutdevices) as LinearLayout
        dialog = ProgressDialog(this)
        initBroadcast()


        mHandler = MHandler(this)
        WorkService.addHandler(mHandler)
    }

    override fun onDestroy() {
        super.onDestroy()
        uninitBroadcast()
    }

    override fun onClick(arg0: View) {
        // TODO Auto-generated method stub
        when (arg0.id) {
            R.id.buttonSearch -> {
                val adapter = BluetoothAdapter.getDefaultAdapter()
                if (adapter == null) {
                    finish()
                    return
                }

                if (!adapter.isEnabled) {
                    if (adapter.enable()) {
                        while (!adapter.isEnabled) {
                            Log.v(
                                TAG,
                                "Enable BluetoothAdapter"
                            )
                        }
                    } else {
                        finish()
                    }
                }

                if (null != WorkService.workThread) {
                    WorkService.workThread.disconnectBt()
                    Thread.sleep(1_0)
                }


                adapter.cancelDiscovery()
                linearLayoutDevices!!.removeAllViews()

                Thread.sleep(1_0)
                adapter.startDiscovery()

            }
        }
    }

    private fun initBroadcast() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(
                context: Context,
                intent: Intent
            ) {
                // TODO Auto-generated method stub
                val action = intent.action
                val device = intent
                    .getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (BluetoothDevice.ACTION_FOUND == action) {
                    if (device == null) return
                    val address = device.address
                    var name = device.name
                    if (name == null) name = "BT" else if (name == address) name = "BT"
                    val button = Button(context)
                    button.text = "$name: $address"
                    button.gravity = (Gravity.CENTER_VERTICAL
                            or Gravity.LEFT)
                    button.setOnClickListener { // TODO Auto-generated method stub
                        WorkService.workThread.disconnectBt()
                        // 只有没有连接且没有在用，这个才能改变状态
                        dialog!!.setMessage(
                            Global.toast_connecting.toString() + " "
                                    + address
                        )
                        dialog!!.isIndeterminate = true
                        dialog!!.setCancelable(false)
                        dialog!!.show()
                        WorkService.workThread.connectBt(address)
                    }
                    button.background.alpha = 100
                    linearLayoutDevices!!.addView(button)
                } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED
                    == action
                ) {
                    progressBarSearchStatus!!.isIndeterminate = true
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    == action
                ) {
                    progressBarSearchStatus!!.isIndeterminate = false
                }
            }
        }
        intentFilter = IntentFilter()
        intentFilter!!.addAction(BluetoothDevice.ACTION_FOUND)
        intentFilter!!.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        intentFilter!!.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun uninitBroadcast() {
        if (broadcastReceiver != null) unregisterReceiver(broadcastReceiver)
    }

    internal class MHandler(activity: SearchBTActivity) : Handler() {
        private var mActivity: WeakReference<SearchBTActivity> = WeakReference(activity)
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
                    Log.v(TAG, "Connect Result: $result")
                    theActivity!!.dialog!!.cancel()
                    if (1 == result) {
                        printTest()
                    }
                }
            }
        }

        fun printTest() {
            val str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ\n0123456789\n"
            val tmp1 = byteArrayOf(
                0x1b, 0x40, 0xB2.toByte(), 0xE2.toByte(), 0xCA.toByte(),
                0xD4.toByte(), 0xD2.toByte(), 0xB3.toByte(), 0x0A
            )
            val tmp2 = byteArrayOf(0x1b, 0x21, 0x01)
            val tmp3 = byteArrayOf(0x0A, 0x0A, 0x0A, 0x0A)
            val buf: ByteArray = DataUtils.byteArraysToBytes(
                arrayOf(
                    tmp1,
                    str.toByteArray(), tmp2, str.toByteArray(), tmp3
                )
            )
            if (WorkService.workThread.isConnected) {
                val data = Bundle()
                data.putByteArray(Global.BYTESPARA1, buf)
                data.putInt(Global.INTPARA1, 0)
                data.putInt(Global.INTPARA2, buf.size)
                WorkService.workThread.handleCmd(Global.CMD_WRITE, data)
            } else {
                Toast.makeText(
                    mActivity.get(), Global.toast_notconnect,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        private var mHandler: Handler? = null
        private const val TAG = "SearchBTActivity"
    }
}