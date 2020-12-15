package com.cdms.android.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cdms.android.Global
import com.cdms.android.R
import com.cdms.android.adapter.BillAdapter
import com.cdms.android.model.Bill
import com.cdms.android.network.RestClient
import com.cdms.android.network.RestInterface
import com.cdms.android.service.WorkService
import com.cdms.android.utils.FileUtils
import com.cdms.android.utils.PreferenceUtils
import com.fevziomurtekin.customprogress.Dialog
import com.fevziomurtekin.customprogress.Type
import com.lvrenyang.io.IOCallBack
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity() {

    var currentLocationId = ""
    var currentLocationName = ""

    var locationIds: Array<Int> = arrayOf(755, 762, 763)
    private var locationNames: Array<String> = arrayOf("HURUMA", "RIRUTA", "KAREN")

    private lateinit var progressbar : Dialog

    private var openBills: ArrayList<Bill>? = null

    private var mHandler: Handler? = null

    private val disposable = CompositeDisposable()
    private val restInterface by lazy {
        RestClient.client.create(RestInterface::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        location.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, locationNames)

        currentLocationId = locationIds[location.selectedItemPosition].toString()

        location.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                currentLocationId = locationIds[position].toString()
                currentLocationName = locationNames[position]
                PreferenceUtils.putString("locationId", currentLocationId)
                PreferenceUtils.putString("locationName", currentLocationName)
                getOpenBills()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        }

        getOpenBills()

        WorkService.cb = object : IOCallBack {
            // WorkThread线程回调
            override fun OnOpen() {
                // TODO Auto-generated method stub
                if (null != mHandler) {
                    val msg: Message =
                        mHandler?.obtainMessage(Global.MSG_IO_ONOPEN)!!
                    mHandler?.sendMessage(msg)!!
                }
            }

            override fun OnClose() {
                // TODO Auto-generated method stub
                if (null != mHandler) {
                    val msg: Message =
                        mHandler?.obtainMessage(Global.MSG_IO_ONCLOSE)!!
                        mHandler?.sendMessage(msg)!!
                }
            }
        }
        mHandler = MHandler(this)
        WorkService.addHandler(mHandler)

        if (null == WorkService.workThread) {

            val intent = Intent(this, WorkService::class.java)
            startService(intent)
        }

        handleIntent(intent)

    }

    private fun handleIntent(intent: Intent) {
        val action = intent.action
        val type = intent.type
        if (Intent.ACTION_SEND == action && type != null) {
            if ("text/plain" == type) {
                handleSendText(intent) // Handle text being sent
            }
        }
    }

    private fun handleSendText(intent: Intent) {
        val textUri =
            intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri?
        if (textUri != null) {
            // Update UI to reflect text being shared
            if (WorkService.workThread.isConnected) {
                val buffer =
                    byteArrayOf(0x1b, 0x40, 0x1c, 0x26, 0x1b, 0x39, 0x01) // 设置中文，切换双字节编码。
                val data = Bundle()
                data.putByteArray(Global.BYTESPARA1, buffer)
                data.putInt(Global.INTPARA1, 0)
                data.putInt(Global.INTPARA2, buffer.size)
                WorkService.workThread.handleCmd(Global.CMD_POS_WRITE, data)
            }
            if (WorkService.workThread.isConnected) {
                val path = textUri.path
                val strText: String = FileUtils.ReadToString(path)
                val buffer = strText.toByteArray()
                val data = Bundle()
                data.putByteArray(Global.BYTESPARA1, buffer)
                data.putInt(Global.INTPARA1, 0)
                data.putInt(Global.INTPARA2, buffer.size)
                data.putInt(Global.INTPARA3, 128)
                WorkService.workThread.handleCmd(
                    Global.CMD_POS_WRITE_BT_FLOWCONTROL, data
                )
            } else {
                Toast.makeText(
                    this, Global.toast_notconnect,
                    Toast.LENGTH_SHORT
                ).show()
            }
            finish()
        }
    }


    internal class MHandler(activity: MainActivity) : Handler() {
        var mActivity: WeakReference<MainActivity>
        override fun handleMessage(msg: Message) {
//            val theActivity = mActivity.get()
//            when (msg.what) {
//                Global.MSG_IO_ONOPEN -> theActivity.mStatusBar.setProgress(100)
//                Global.MSG_IO_ONCLOSE -> theActivity.mStatusBar.setProgress(0)
//            }
        }

        init {
            mActivity = WeakReference(activity)
        }
    }


    private fun getOpenBills() {

        progressbar = findViewById(R.id.progress)
        progressbar.settype(Type.SPINNER)
        progressbar.setdurationTime(100)
        progressbar.show()

        disposable.add(
            restInterface.getOpenBills(
                currentLocationId
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it ->
                    openBills = it
                    loadViews()
                    progressbar.isVisible = false
                    if( openBills?.size == 0 ){
                        empty_text.visibility = View.VISIBLE
                    }
                    else {
                        empty_text.visibility = View.GONE
                    }
                },
                    { e ->
                        println(e)
                        progressbar.show()
                        Toast.makeText(this, "Failed to load bills", Toast.LENGTH_LONG).show()
                    })
        )
    }

    private fun loadViews(){

        val objAdapter = openBills?.let {
            BillAdapter(
                it
            )
        }
        rv_bills.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rv_bills.adapter = objAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                val intent = Intent(this, ConnectBTPairedActivity::class.java)
                this.startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}