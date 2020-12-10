package com.cdms.android.model

import com.google.gson.annotations.SerializedName

class Bill {
    @SerializedName("PatientID")
    var patientID : Int? = null
    @SerializedName("ID")
    var id: String? = null
    @SerializedName("LastName")
    var lastName: String? = null
    @SerializedName("FirstName")
    var firstName: String? = null
    @SerializedName("DOB")
    var dOB: String? = null
    @SerializedName("BillAmount")
    var billAmount: Double? = null
    @SerializedName("OutStandingAmount")
    var outStandingAmount: Double? = null
    @SerializedName("Discount")
    var discount:Double? = null
    @SerializedName("BillDate")
    var billDate: String? = null

}