package com.cdms.android

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cdms.android.adapter.MedicineAdapter
import com.cdms.android.model.Medicine
import com.cdms.android.network.RestClient
import com.cdms.android.network.RestInterface
import com.cdms.android.utils.PreferenceUtils
import com.fevziomurtekin.customprogress.Dialog
import com.fevziomurtekin.customprogress.Type
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_bill_details.*
import kotlinx.android.synthetic.main.activity_main.empty_text

class BillDetails : AppCompatActivity(){


    private var patientName = ""
    var patientId = ""
    var locationName = ""
    var locationId = ""
    var totalAmount = 0.0

    private lateinit var progressbar : Dialog

    private var medicine: ArrayList<Medicine>? = null

    private val disposable = CompositeDisposable()
    private val restInterface by lazy {
        RestClient.client.create(RestInterface::class.java)
    }

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

        }
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
                    total_amount.text = "TOTAL: KES " + calculateTotal().toString()
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

    private fun loadViews(){

        val objAdapter = medicine?.let {
            MedicineAdapter(
                it
            )
        }
        rv_medicine.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rv_medicine.adapter = objAdapter
    }

    private fun calculateTotal() : Double {
            medicine?.map {
                totalAmount += it.amount ?: 0.0
            }

        return  totalAmount
    }
}