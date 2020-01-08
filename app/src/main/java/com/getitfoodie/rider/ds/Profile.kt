package com.getitfoodie.rider.ds

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class Profile {

    var id = 0
    var user = ""                    //mobile number
    var full_name = ""
    var is_present = false
    var balance = 0                 // main balance
    var total_incentive = 0
    var total_tip = 0
    var url = ""
    var rider_photo : String? = ""

}