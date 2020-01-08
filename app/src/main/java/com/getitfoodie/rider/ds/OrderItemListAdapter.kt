package com.getitfoodie.rider.ds


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.getitfoodie.rider.R

class OrderItemListAdapter(val mcontext: Context, val orderItemList: List<OrderItem>) :
    ArrayAdapter<OrderItem>(mcontext, 0, orderItemList) {
    init {

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {


        val listItem = when (convertView == null) {
            true -> LayoutInflater.from(mcontext).inflate(R.layout.order_item_list_item, parent, false)
            false -> convertView
        }

        val currentItem = orderItemList[position]
        listItem.findViewById<TextView>(R.id.text_item_name).text = currentItem.item_name
        listItem.findViewById<TextView>(R.id.text_item_size).text = currentItem.item_size
        listItem.findViewById<TextView>(R.id.text_item_quan).text = currentItem.item_quantity.toString()

        return listItem
    }
}
