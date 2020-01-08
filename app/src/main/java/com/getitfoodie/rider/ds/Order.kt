package com.getitfoodie.rider.ds

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class OrderItem {

    var item_name = ""
    var item_size  = ""
    var item_quantity = ""
}

@JsonClass(generateAdapter = true)
class Order {

    var id = 0
    var url = ""
    var user = ""
    var address = ""
    var payment_method = ""
    var datetime = ""
    var mobile_number = ""
    var total = ""
    var merchandise = ""
    var current_status = ""
    var additinal_detail = ""
    var order_items: List<OrderItem> = listOf()

    // TODO implement it isEditable function
    fun isEditable(): Boolean = true

}

class OrderLiveData constructor(context: Context) : MutableLiveData<MutableList<Order>>() {

    companion object {
        @Volatile
        private var INSTANCE: OrderLiveData? = null

        fun getInstance(context: Context) = INSTANCE
            ?: synchronized(this) {
                INSTANCE
                    ?: OrderLiveData(context).also { INSTANCE = it }
            }
    }

    override fun onActive() {

    }

    override fun onInactive() {
    }

    override fun setValue(newList: MutableList<Order>?) {
        this.value?.clear()
        this.value?.addAll(newList as MutableIterable<Order>)
    }

    override fun postValue(newList: MutableList<Order>?) {
        this.value?.clear()
        this.value?.addAll(newList as MutableIterable<Order>)
    }
}
