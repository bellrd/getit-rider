package com.getitfoodie.rider

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.getitfoodie.rider.DatePickerFragment
import com.getitfoodie.rider.ds.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.FileNotFoundException
import java.sql.Date
import kotlin.properties.Delegates


private val type = Types.newParameterizedType(MutableList::class.java, Order::class.java)

class HistoryFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var listView: ListView
    private lateinit var swiper: SwipeRefreshLayout
    private var orders = mutableListOf<History>()

    private val orderJsonAdapter = Moshi.Builder().build().adapter(History::class.java)
    private val orderListJsonAdapter = Moshi.Builder().build().adapter<MutableList<History>>(type)

    var date: Date by Delegates.observable(Date(System.currentTimeMillis())) { _, _, newDate ->
        load(newDate)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        listView = view.findViewById<ListView>(R.id.listview_order_history)
        listView.adapter = OrderHistoryListAdapter(context!!, orders)
        listView.setOnItemClickListener { _, _, position, id ->
            val intent = Intent(context, HistoryDetail::class.java).apply {
                putExtra("ORDER", orderJsonAdapter.toJson(orders[position]))
            }
            startActivity(intent)
        }
        swiper = view.findViewById<SwipeRefreshLayout>(R.id.swiper_history)
        swiper.setOnRefreshListener {
            refresh(date)
            swiper.isRefreshing = false
        }

        val fab = view.findViewById<FloatingActionButton>(R.id.pdate)
        fab.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.setTargetFragment(this, 101)
            datePickerFragment.show(activity?.supportFragmentManager, "datePicker")
        }

        date = Date(System.currentTimeMillis())
        return view
    }

    fun load(d: Date) {
        try {
            var orderJson = "[]"
            val file = context?.openFileInput("$date.json")
            file?.bufferedReader()?.use { orderJson = it.readText() }
            val temp = orderListJsonAdapter.fromJson(orderJson)
            if (temp != null) {
                orders.clear()
                orders.addAll(temp)
                (listView.adapter as OrderListAdapter).notifyDataSetChanged()
            }
        } catch (e: FileNotFoundException) {
            refresh(d)
        }
    }

    fun refresh(d: Date) {

        val stringRequest = object : StringRequest(
            Request.Method.GET,
            "$baseUrl/vendor/orders/?date=$d",
            Response.Listener { orderJson ->
                val file = context?.openFileOutput("$d.json", Context.MODE_PRIVATE)
                file?.bufferedWriter().use { it?.write(orderJson) }
                val temp = orderListJsonAdapter.fromJson(orderJson)
                if (temp != null) {
                    orders.clear()
                    orders.addAll(temp)
                    (listView.adapter as OrderListAdapter).notifyDataSetChanged()
                } else {
                    orders.clear()
                    orders.addAll(mutableListOf())
                    (listView.adapter as OrderListAdapter).notifyDataSetChanged()
                }
            },
            Response.ErrorListener {
                if (view != null) {
                    Snackbar.make(view!!, "No internet connection..", Snackbar.LENGTH_SHORT).show()
                }
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return HashMap<String, String>().apply {
                    put("AUTHORIZATION", Singleton.getInstance(context!!).accessToken)
                }
            }
        }
        Singleton.getInstance(context!!).addToRequestQueue(stringRequest)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        date = Date.valueOf("$year-${month + 1}-$dayOfMonth")
    }
}

