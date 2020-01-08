package com.getitfoodie.rider.ds


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.getitfoodie.rider.R

class TransactionListAdapter(val mcontext: Context, val transactionList: List<Transaction>) :
    ArrayAdapter<Transaction>(mcontext, 0, transactionList) {
    init {

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {


        val listItem = when (convertView == null) {
            true -> LayoutInflater.from(mcontext).inflate(R.layout.transaction_list_item, parent, false)
            false -> convertView
        }

        val currentTransaction = transactionList[position]

        val transactionId = listItem.findViewById<TextView>(R.id.transaction_id)
        transactionId.text = "#${currentTransaction.id}"

        val transactionDate = listItem.findViewById<TextView>(R.id.transaction_date)
        transactionDate.text = currentTransaction.on_date

        val transactionAmount = listItem.findViewById<TextView>(R.id.transaction_amount)
        transactionAmount.text = " \u20B9 ${currentTransaction.amount} "

        val transactionType = listItem.findViewById<TextView>(R.id.transaction_type)
        transactionType.text = currentTransaction.type.toUpperCase()

        val transactionBalanceType = listItem.findViewById<TextView>(R.id.transaction_balance_type)
        transactionBalanceType.text = currentTransaction.balance_type

        val transactionDetail = listItem.findViewById<TextView>(R.id.transaction_message)
        transactionDetail.text = currentTransaction.additional_detail

        return listItem
    }
}