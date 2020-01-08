package com.getitfoodie.rider

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.getitfoodie.rider.ds.Singleton
import com.getitfoodie.rider.ds.baseUrl
import com.getitfoodie.rider.ds.syncFcmTokenWithServer
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val button = findViewById<Button>(R.id.button_login)
        button.setOnClickListener {
            val mobile = findViewById<EditText>(R.id.edit_mobile).text.toString()
            val password = findViewById<EditText>(R.id.edit_password).text.toString()
            Snackbar.make(
                findViewById<CoordinatorLayout>(R.id.cl_snackbar),
                "Logging in ",
                Snackbar.LENGTH_INDEFINITE
            ).show()
            login(mobile, password)
        }
    }

    fun login(mobile: String, password: String) {
        val stringRequest = object : StringRequest(
            Request.Method.POST,
            "$baseUrl/get-access-token",
            Response.Listener { token ->
                openFileOutput("LOGINFILE", Context.MODE_PRIVATE).use {
                    it.bufferedWriter().write(token)
                }
                syncFcmTokenWithServer(
                    applicationContext
                )
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            },
            Response.ErrorListener {
                Snackbar.make(
                    findViewById(R.id.cl_snackbar)
                    , "Some error occurred",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val data = HashMap<String, String>()
                data.put("username", mobile)
                data.put("password", password)
                return data
            }

        }

        Singleton.getInstance(applicationContext).addToRequestQueue(stringRequest)
    }
}


