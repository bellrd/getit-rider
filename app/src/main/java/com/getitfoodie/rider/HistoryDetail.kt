package com.getitfoodie.rider

import android.graphics.Color
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.getitfoodie.rider.ds.History
import com.getitfoodie.rider.ds.OrderItemListAdapter
import com.squareup.moshi.Moshi

class HistoryDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_detail)

        val orderJsonAdapter = Moshi.Builder().build().adapter<History>(History::class.java)
        val order: History? = orderJsonAdapter.fromJson(intent.getStringExtra("ORDER"))

        val toolbar: Toolbar = findViewById(R.id.toolbar_history_detail)
        toolbar.title = "#${order!!.order_id}"
        val color = when (order.last_status) {

            "PICKED" -> "cc7722"
            // successful delivered
            "DELIVERED" -> "#008577"
            // some other misfortune status (impossible to occur, however nothing is for sure my friend)
            "REFUSED", "CANCEL1", "CANCEL2", "REJECTED" -> "#D81B60"
            // else
            else -> "#000000"
        }
        toolbar.setBackgroundColor(Color.parseColor(color))

        findViewById<TextView>(R.id.order_history_total).text = "${order.total}"
        findViewById<TextView>(R.id.order_history_merchandise).text = order.merchandise
        findViewById<TextView>(R.id.order_history_address).text = order.address_used
        val listView = findViewById<ListView>(R.id.listview_order_history_detail)
        listView.adapter = OrderItemListAdapter(this, order.order_items)

    }
}
