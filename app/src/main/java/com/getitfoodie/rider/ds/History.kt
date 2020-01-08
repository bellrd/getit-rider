package com.getitfoodie.rider.ds

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class History{

    var order_id = 0
    var user_name = ""
    var address_used = ""
    var merchandise = ""
    var date = ""
    var time = ""
    var total = ""
    var payment_method = ""
    var last_status = ""
    var additinal_detail = ""
    var order_items: List<OrderItem> = listOf()

    // TODO implement it isEditable function
    fun isEditable(): Boolean = true

}