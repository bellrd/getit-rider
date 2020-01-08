package com.getitfoodie.rider.ds


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.getitfoodie.rider.R

class OrderListAdapter(val mcontext: Context, val orderList: List<Order>) :
    ArrayAdapter<Order>(mcontext, 0, orderList) {
    init {

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val listItem = when (convertView == null) {
            true -> LayoutInflater.from(mcontext).inflate(R.layout.order_list_item, parent, false)
            false -> convertView
        }

        val currentOrder: Order = orderList[position]
        val orderId: TextView? = listItem.findViewById(R.id.order_id)
        orderId?.text = "#${currentOrder.id}"

        val orderName: TextView? = listItem.findViewById(R.id.order_name)

        val orderTotal: TextView? = listItem.findViewById(R.id.order_total)
        orderTotal?.text = "\u20B9 ${currentOrder.total}"

        val orderMerchandise: TextView? = listItem.findViewById(R.id.order_merchandise)
        orderMerchandise?.text = currentOrder.merchandise

        val orderStatus: TextView? = listItem.findViewById(R.id.order_status)
        orderStatus?.text = currentOrder.current_status
        val tempColor = when (currentOrder.current_status) {
            "PLACED" -> "#214d39"
            "PICKED" -> "#c87403"
            "DELIVERED" -> "#000000"
            "CANCEL1" -> "#ba2121"
            "CANCEL2" -> "#ba2121"
            else -> "#000000"
        }
        orderStatus?.setTextColor(Color.parseColor(tempColor))

        val orderAddress: TextView? = listItem.findViewById(R.id.order_address)
        orderAddress?.text = currentOrder.address

        return listItem
    }
}