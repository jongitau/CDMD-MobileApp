package com.cdms.android.adapter;

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cdms.android.BillDetails
import com.cdms.android.R
import com.cdms.android.model.Bill
import kotlinx.android.synthetic.main.item_bill.view.*

class BillAdapter(
    private var dataSource: ArrayList<Bill>

) : RecyclerView.Adapter<BillAdapter.ViewHolder>() {

    private lateinit var filteredDataSource: ArrayList<Bill>

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
            itemView.name.text = bill.firstName + " " + bill.lastName
            itemView.bill_amount.text = bill.billAmount.toString()
            itemView.bill_date.text = bill.billDate
            itemView.bill_id.text = bill.id
            itemView.dob.text = bill.dOB
            itemView.outstanding_amount.text = bill.outStandingAmount.toString()
            itemView.discount.text = bill.discount.toString()
            itemView.patient_id.text = bill.patientID.toString()

            itemView.setOnClickListener{
                val intent = Intent(itemView.context, BillDetails::class.java)
                intent.putExtra("id", bill.id)
                itemView.context.startActivity(intent)
            }
        }
    }

}
