package com.cdms.android.adapter;

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cdms.android.R
import com.cdms.android.model.Medicine
import kotlinx.android.synthetic.main.item_medicine.view.*


class MedicineAdapter(
    private var dataSource: ArrayList<Medicine>

) : RecyclerView.Adapter<MedicineAdapter.ViewHolder>() {

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
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_medicine, parent, false)
        return ViewHolder(
            v
        )
    }

    //the class is holding the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindItems(medicine: Medicine) {
            itemView.name.text = medicine.itemName
            itemView.total.text = medicine.amount.toString()
            itemView.quantity.text = medicine.quantity.toString()
            itemView.bill_date.text = medicine.billItemDate.toString()
        }
    }

}
