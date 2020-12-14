package com.cdms.android.activity

import android.content.Intent
import android.os.Bundle
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
import com.cdms.android.R
import com.cdms.android.adapter.BillAdapter
import com.cdms.android.model.Bill
import com.cdms.android.network.RestClient
import com.cdms.android.network.RestInterface
import com.cdms.android.utils.PreferenceUtils
import com.fevziomurtekin.customprogress.Dialog
import com.fevziomurtekin.customprogress.Type
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var currentLocationId = ""
    var currentLocationName = ""

    var locationIds: Array<Int> = arrayOf(755, 762, 763)
    private var locationNames: Array<String> = arrayOf("HURUMA", "RIRUTA", "KAREN")

    private lateinit var progressbar : Dialog

    private var openBills: ArrayList<Bill>? = null

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