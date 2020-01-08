package com.getitfoodie.rider.ds

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import org.json.JSONObject

/*
this function will send fcm token to server must be called after login function
 */
fun syncFcmTokenWithServer(applicationContext: Context) {
    FirebaseApp.initializeApp(applicationContext)
    FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(
        OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result!!.token
            val stringRequest = object : StringRequest(
                Request.Method.POST,
                "$baseUrl/customer/fcmTokens/",
                Response.Listener { println(it) },
                Response.ErrorListener { println(token) }
            ) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getBody(): ByteArray {
                    val temp = HashMap<String, String>()
                    temp["source"] = "RIDER"
                    temp["token"] = token
                    return JSONObject(temp as Map<*, *>).toString().toByteArray()
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] =
                        "Token ${Singleton.getInstance(
                            applicationContext
                        ).accessToken}"
                    return headers
                }
            }

            Singleton.getInstance(
                applicationContext
            ).addToRequestQueue(stringRequest)
        }
    )
}

