package com.getitfoodie.rider


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.getitfoodie.rider.ds.*
import com.google.android.material.snackbar.Snackbar
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.FileNotFoundException


private const val TAG = "Rider:OrderFragment "
private val type = Types.newParameterizedType(MutableList::class.java, Order::class.java)

class OrderFragment : Fragment() {

    private val orderListJsonAdapter = Moshi.Builder().build().adapter<MutableList<Order>>(type)
    private val orderJsonAdapter = Moshi.Builder().build().adapter<Order>(Order::class.java)
    private lateinit var mySwiper: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order, container, false)
        mySwiper = view.findViewById<SwipeRefreshLayout>(R.id.swiper_order)
        mySwiper.setOnRefreshListener {
            refresh()
        }

        val orderListView = view.findViewById<ListView>(R.id.listview_order)
        orderListView.adapter = OrderLiveData.getInstance(context!!.applicationContext).value?.let {
            OrderListAdapter(
                context!!,
                it
            )
        }
        OrderLiveData.getInstance(context!!.applicationContext)
            .observe(this, Observer { _ ->
                mySwiper.isRefreshing = true
                (orderListView.adapter as OrderListAdapter).notifyDataSetChanged()
            })

        orderListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            run {
                Intent(context, OrderDetail::class.java)
                    .apply {

                        putExtra(
                            "ORDER",
                            orderJsonAdapter.toJson(OrderLiveData.getInstance(context!!.applicationContext).value!![position])
                        )
                        putExtra("INDEX", position)
                        startActivity(this)
                    }
            }
        }
        load()
        return view
    }

    /* this function will load order list from order.json file if file not found it will
    call refresh function
     */

    fun load() {
        try {
            val file = context?.openFileInput("order.json")
            val orderJson = file?.bufferedReader()?.use { it.readText() }
            val temp = orderListJsonAdapter.fromJson(orderJson!!)
            OrderLiveData.getInstance(context!!.applicationContext).value = temp

        } catch (e: FileNotFoundException) {
            refresh()
        }
    }

    /* this function will fetch order from network and save the json in file order.json
    and call load function
     */
    fun refresh() {
        mySwiper.isRefreshing = true

        // fetch from network
        val stringRequest = object : StringRequest(
            Request.Method.GET,
            "$baseUrl/rider/orders/",
            Response.Listener<String> { json ->
                val file = context!!.openFileOutput("order.json", Context.MODE_PRIVATE)
                file.bufferedWriter().use {
                    it.write(json)
                }
                load()
                mySwiper.isRefreshing = false
            },
            Response.ErrorListener {
                if (view != null) Snackbar.make(
                    view!!,
                    "No internet connection.",
                    Snackbar.LENGTH_SHORT
                ).show()
                mySwiper.isRefreshing = false
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] =
                    "Token ${Singleton.getInstance(context!!.applicationContext).accessToken}"
                headers["ROLE"] = "RIDER"
                return headers
            }
        }
        Singleton.getInstance(context!!).addToRequestQueue(stringRequest)
    }
}
