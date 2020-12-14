package com.cdms.android.adapter;

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cdms.android.activity.BillDetailsActivity
import com.cdms.android.R
import com.cdms.android.model.Bill
import kotlinx.android.synthetic.main.item_bill.view.*
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.collections.ArrayList

class BillAdapter(
    private var dataSource: ArrayList<Bill>

) : RecyclerView.Adapter<BillAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return dataSource.count()

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(dataSource[position])
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_bill, parent, false)
        return ViewHolder(
            v
        )
    }

    //the class is holding the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindItems(bill: Bill) {
            val patientName = bill.firstName + " " + bill.lastName
            itemView.name.text = patientName

            val formatter: NumberFormat = DecimalFormat("#,###")

            itemView.bill_amount.text = "KES " + formatter.format(bill.billAmount).toString()
            itemView.bill_date.text =
                bill.billDate?.count()?.minus(8)?.let { bill.billDate?.substring(0, it) }
            itemView.bill_id.text = bill.id
            itemView.dob.text = bill.dOB
            itemView.outstanding_amount.text = bill.outStandingAmount.toString()
            itemView.discount.text = bill.discount.toString()
            itemView.patient_id.text = bill.patientID.toString()

            itemView.setOnClickListener{
                val intent = Intent(itemView.context, BillDetailsActivity::class.java)
                intent.putExtra("patientId", bill.patientID)
                intent.putExtra("patientName", patientName)

                itemView.context.startActivity(intent)
            }
        }
    }

}
