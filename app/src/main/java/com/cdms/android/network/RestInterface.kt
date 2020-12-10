package com.cdms.android.network

import com.cdms.android.model.Bill
import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface RestInterface {

    @GET("PharOpenBills/{id}")
    open fun getOpenBills(@Path("id") id: String?): Observable<ArrayList<Bill>?>

}