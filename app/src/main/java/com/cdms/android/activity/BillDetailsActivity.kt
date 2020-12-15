package com.cdms.android.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cdms.android.Global
import com.cdms.android.R
import com.cdms.android.adapter.MedicineAdapter
import com.cdms.android.model.Medicine
import com.cdms.android.network.RestClient
import com.cdms.android.network.RestInterface
import com.cdms.android.service.WorkService
import com.cdms.android.utils.DataUtils
import com.cdms.android.utils.PreferenceUtils
import com.fevziomurtekin.customprogress.Dialog
import com.fevziomurtekin.customprogress.Type
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_bill_details.*
import java.io.UnsupportedEncodingException
import java.lang.ref.WeakReference
import java.text.DecimalFormat
import java.text.NumberFormat
import androidx.core.view.isVisible

class BillDetailsActivity : AppCompatActivity(){


    private var patientName = ""
    var patientId = ""
    var locationName = ""
    var locationId = ""
    var totalAmount = 0.0
    private val TAG = "BillDetailsActivity"

    private lateinit var progressbar : Dialog

    private var medicine: ArrayList<Medicine>? = null

    private val disposable = CompositeDisposable()
    private val restInterface by lazy {
        RestClient.client.create(RestInterface::class.java)
    }
    private var mHandler: Handler? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_details)

       locationId =  PreferenceUtils.getString("locationId", "").toString()
       patientId =  intent.getIntExtra("patientId", 0).toString()
        locationName =  PreferenceUtils.getString("locationName", "").toString()
        patientName =  intent.getStringExtra("patientName").toString()

        name.text = patientName
        patient_id.text = "Patient ID: $patientId"

        getMedicine()

        print.setOnClickListener {

           printBill()
        }

        mHandler =
            MHandler(this)
        WorkService.addHandler(mHandler)
    }

    private fun printBill(){

        var bill = ""
        medicine?.map {
            val itemName = it.itemName
            val amount = it.amount
            bill += " \n$itemName + \nKES $amount"
        }

        val text: String = ("\n\n\nBILL DETAILS"
                + "\n"
                + "Patient Name: $patientName"
                + "\n"
                + ".........................."
                + "\n"
                + bill
                + "\n"
                + ".........................."
                + "\n"
                + "Total: KES $totalAmount"
                )

        var header: ByteArray? = null
        var strbuf: ByteArray? = null
        header = byteArrayOf(
            0x1b, 0x40, 0x1c, 0x26, 0x1b, 0x39,
            0x01
        )
        try {
            strbuf = text.toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        val buffer: ByteArray = DataUtils.byteArraysToBytes(
            strbuf?.let { it1 ->
                arrayOf<ByteArray>(
                    header, it1
                )
            }
        )
        val data = Bundle()
        data.putByteArray(Global.BYTESPARA1, buffer)
        data.putInt(Global.INTPARA1, 0)
        data.putInt(Global.INTPARA2, buffer.size)
        WorkService.workThread.handleCmd(Global.CMD_POS_WRITE, data)
    }

    @SuppressLint("SetTextI18n")
    private fun getMedicine() {

        progressbar = findViewById(R.id.progress)
        progressbar.settype(Type.SPINNER)
        progressbar.setdurationTime(100)
        progressbar.show()

        disposable.add(
            restInterface.getBillList(
                patientId, locationId
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it ->
                    medicine = it
                    loadViews()
                    progressbar.isVisible = false
                    if( medicine?.size == 0 ){
                        empty_text.visibility = View.VISIBLE
                    }
                    else {
                        empty_text.visibility = View.GONE
                    }
                },
                    { e ->
                        println(e)
                        progressbar.show()
                        Toast.makeText(this, "Failed to the load bill list", Toast.LENGTH_LONG).show()
                    })
        )
    }

    @SuppressLint("SetTextI18n")
    private fun loadViews(){

        val objAdapter = medicine?.let {
            MedicineAdapter(
                it
            )
        }
        rv_medicine.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rv_medicine.adapter = objAdapter

        val formatter: NumberFormat = DecimalFormat("#,###")

        total_amount.text =  "Total: KES " + formatter.format(calculateTotal()).toString()
    }


    private fun calculateTotal() : Double {
            medicine?.map {
                totalAmount += it.amount ?: 0.0
            }

        return  totalAmount
    }

    internal class MHandler(activity: BillDetailsActivity) :
        Handler() {
        private var mActivity: WeakReference<BillDetailsActivity> = WeakReference<BillDetailsActivity>(activity)
        override fun handleMessage(msg: Message) {
            val theActivity: BillDetailsActivity? = mActivity.get()
            when (msg.what) {
                Global.CMD_POS_WRITERESULT -> {
                    val result = msg.arg1
                    Toast.makeText(
                        theActivity,
                        if (result == 1) Global.toast_success else Global.toast_fail,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.v(
                        theActivity?.TAG,
                        "Result: $result"
                    )
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        WorkService.delHandler(mHandler)
        mHandler = null
    }
}