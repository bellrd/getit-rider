package com.getitfoodie.rider

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.getitfoodie.rider.ds.Singleton
import com.getitfoodie.rider.ds.Transaction
import com.getitfoodie.rider.ds.TransactionListAdapter
import com.getitfoodie.rider.ds.baseUrl
import com.google.android.material.snackbar.Snackbar
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.FileNotFoundException


private const val TAG = "TransactionFragment "
private val type = Types.newParameterizedType(MutableList::class.java, Transaction::class.java)


class TransactionFragment : Fragment() {


    private val transactions = mutableListOf<Transaction>()

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var transactionListView: ListView
    private val transactionListJsonAdapter =
        Moshi.Builder().build().adapter<MutableList<Transaction>>(type)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transaction, container, false)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        transactionListView = view.findViewById(R.id.listview_transactions)
        transactionListView.adapter = TransactionListAdapter(context!!, transactions)
        swipeRefreshLayout.setOnRefreshListener {
            refresh()
            swipeRefreshLayout.isRefreshing = false
        }
        load()
        return view
    }


    private fun load() {
        try {
            var transactionJson: String = "[]"
            context?.openFileInput("transaction.json")?.bufferedReader()
                ?.apply { transactionJson = readText(); close() }
            with(transactionListJsonAdapter.fromJson(transactionJson)) {
                transactions.clear()
                transactions.addAll(this ?: mutableListOf<Transaction>())
                (transactionListView.adapter as? TransactionListAdapter)?.notifyDataSetChanged()

            }
        } catch (e: FileNotFoundException) {
            if (context != null) refresh()
        } catch (e: Exception) {
            Log.e(TAG, "Exception occurs in transaction fragment load()")
            throw e
        }
    }

    fun refresh() {
        val stringRequest = object : StringRequest(
            Request.Method.GET,
            "$baseUrl/transactions",
            Response.Listener { transactionJson ->
                context?.openFileOutput("transaction.json", Context.MODE_PRIVATE)?.bufferedWriter()
                    ?.apply { write(transactionJson); close() }
                with(transactionListJsonAdapter.fromJson(transactionJson)) {
                    transactions.clear()
                    transactions.addAll(this ?: mutableListOf<Transaction>())
                    (transactionListView.adapter as? TransactionListAdapter)?.notifyDataSetChanged()
                }
            },
            Response.ErrorListener {
                println(it.toString())
                val parentLayout = activity?.findViewById<View>(android.R.id.content)
                Snackbar.make(
                    view!!,
                    "Can not connect to the server...",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return HashMap<String, String>().apply {
                    put(
                        "Authorization",
                        Singleton.getInstance(context!!).accessToken
                    )
                    put("ROLE","RIDER")
                }
            }
        }
        Singleton.getInstance(context!!).addToRequestQueue(stringRequest)
    }
}
