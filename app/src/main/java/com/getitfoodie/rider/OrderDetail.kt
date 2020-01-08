package com.getitfoodie.rider

import android.content.Intent
import android.graphics.Color.parseColor
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.getitfoodie.rider.ds.Order
import com.getitfoodie.rider.ds.OrderItemListAdapter
import com.getitfoodie.rider.ds.OrderLiveData
import com.getitfoodie.rider.ds.Singleton
import com.google.android.material.snackbar.Snackbar
import com.squareup.moshi.Moshi
import java.nio.charset.Charset


class OrderDetail : AppCompatActivity() {

    private var passedIndex: Int = -1
    private lateinit var initialStatus: String
    private lateinit var listView: ListView
    private lateinit var toolbar: Toolbar
    private var order: Order? = null
    private val orderJsonAdapter = Moshi.Builder().build().adapter<Order>(Order::class.java)
    private lateinit var buttonCall: Button
    private lateinit var buttonConfirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
        order = OrderLiveData.getInstance(applicationContext).value!!.elementAt(passedIndex)
        initialStatus = order!!.current_status
        toolbar = findViewById(R.id.toolbar_detail)
        toolbar.title = "#${order!!.id}"
        updateToolbarColor()

        findViewById<TextView>(R.id.order_total).text = "${order!!.total}"
        findViewById<TextView>(R.id.order_merchandise).text = order!!.merchandise
        findViewById<TextView>(R.id.order_address).text = order!!.address
        listView = findViewById<ListView>(R.id.listview_order_detail)
        listView.adapter = OrderItemListAdapter(this, order!!.order_items)

        buttonConfirm = findViewById<Button>(R.id.btn_pick)
        buttonCall = findViewById<Button>(R.id.btn_call)
        updateButtons()

        // handle call button click
        buttonCall.setOnClickListener {
            Intent(Intent.ACTION_CALL, Uri.parse("tel:${order?.mobile_number}")).apply {
                startActivity(this)
            }
        }

        buttonConfirm.setOnClickListener {
            if ((it as Button).text == "PICK") {
                order?.current_status = "PICKED"
            } else if (it.text == "DELIVER") {
                order?.current_status = "DELIVERED"
            }
            sync_with_server()
        }
    }

    private fun sync_with_server() {
        val stringRequest = object : StringRequest(
            Request.Method.PUT,
            order!!.url,
            Response.Listener<String> {
                val temp = OrderLiveData.getInstance(applicationContext).value?.toMutableList()
                temp?.removeAt(passedIndex)
                OrderLiveData.getInstance(applicationContext).postValue(temp)
                updateToolbarColor()
                updateButtons()
            },
            Response.ErrorListener {
                val parentLayout = findViewById<View>(android.R.id.content)
                Snackbar.make(
                    parentLayout,
                    "Order Changing Failed. Try again...",
                    Snackbar.LENGTH_SHORT
                ).show()
                order!!.current_status = initialStatus
                updateButtons()
                updateToolbarColor()

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                return HashMap<String, String>().apply {
                    put("Authorization", Singleton.getInstance(applicationContext).accessToken)
                }
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return orderJsonAdapter.toJson(order)
                    .toByteArray(charset = Charset.defaultCharset())
            }
        }
        Singleton.getInstance(applicationContext).addToRequestQueue(stringRequest)
    }

    private fun updateToolbarColor() {
        val color = when (order!!.current_status) {
            // picked is step before delivery or cancel1 or cancel2
            "PICKED" -> "cc7722"
            // successful delivered
            "DELIVERED" -> "#008577"
            // some other misfortune status (impossible to occur, however nothing is for sure my friend)
            "REFUSED", "CANCEL1", "CANCEL2", "REJECTED" -> "#D81B60"
            // else
            else -> "#000000"
        }
        toolbar.setBackgroundColor(parseColor(color))
    }

    private fun updateButtons() {
        val text = when (order?.current_status) {
            "PICKED" -> "DELIVER"
            "ACCEPTED", "CONFIRMED", "READY" -> "PICK"
            else -> null
        }
        if (text != null) {
            buttonConfirm.text = text
        } else {
            buttonCall.visibility = View.GONE
            buttonConfirm.visibility = View.GONE
        }
    }
}

