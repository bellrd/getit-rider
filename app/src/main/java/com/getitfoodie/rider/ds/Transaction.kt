package com.getitfoodie.rider.ds

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class Transaction {

    var id = 0
    var type = ""                   // CREDIT, DEBIT, UPDATE
    var additional_detail = ""
    var balance_type = ""           // MAIN_BALANCE, INCENTIVE_BALANCE, TIP_BALANCE
    var amount = 0
    var on_date = ""

}