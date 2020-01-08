package com.getitfoodie.rider

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.getitfoodie.rider.ds.Profile
import com.getitfoodie.rider.ds.Singleton
import com.getitfoodie.rider.ds.baseUrl
import com.google.android.material.snackbar.Snackbar
import com.squareup.moshi.Moshi
import java.io.FileNotFoundException

private const val TAG = "ProfileFrag: "

class ProfileFragment : Fragment() {

    private lateinit var fragmentView: View
    private val profileJsonAdapter = Moshi.Builder().build().adapter<Profile>(Profile::class.java)
    private var profile = Profile()


    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentView = inflater.inflate(R.layout.fragment_profile, container, false)
        val switch: Switch = fragmentView.findViewById(R.id.profile_present)
        switch.isChecked = profile.is_present

        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            profile.is_present = isChecked
            val stringRequest = object : StringRequest(
                Request.Method.PUT,
                profile.url,
                Response.Listener<String> {
                    val file = context?.openFileOutput("profile.json", Context.MODE_PRIVATE)
                    file?.bufferedWriter()?.apply { write(it); close() }
                },
                Response.ErrorListener { }

            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    return HashMap<String, String>().apply {
                        put(
                            "Authorization",
                            "Token ${Singleton.getInstance(context!!).accessToken}"
                        )
                        put(
                            "ROLE",
                            "RIDER"
                        )
                    }
                }

                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getBody(): ByteArray {
                    return profileJsonAdapter.toJson(profile).toByteArray()
                }
            }

            Singleton.getInstance(context!!).addToRequestQueue(stringRequest)
        }


        val button: Button = fragmentView.findViewById(R.id.profile_logout)

        button.setOnClickListener {
            context?.deleteFile("LOGINFILE")
            context?.deleteFile("profile.json")
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        load()
        return view
    }

    private fun updateFragmentView() {
        fragmentView.findViewById<TextView>(R.id.profile_id).text = "#${profile.id}"
        fragmentView.findViewById<TextView>(R.id.profile_name).text = profile.full_name
        fragmentView.findViewById<TextView>(R.id.profile_mobile).text = profile.user
        fragmentView.findViewById<TextView>(R.id.profile_main).text = "\u20B9 ${profile.balance}"
        fragmentView.findViewById<TextView>(R.id.profile_incentives).text =
            "\u20B9 ${profile.total_incentive}"
        fragmentView.findViewById<TextView>(R.id.profile_tip).text = "\u20B9 ${profile.total_tip}"
        //val imageView = view.findViewById<ImageView>(R.id.profile_image)
        //Picasso.get().load(profile.rider_photo).into(imageView)
    }

    fun load() {
        try {
            val file = context?.openFileInput("profile.json")
            val profileJson = file?.bufferedReader().use { it?.readText() }
            val temp = profileJsonAdapter.fromJson(profileJson!!)
            profile = if (temp != null) temp else throw KotlinNullPointerException()
            updateFragmentView()

        } catch (e: FileNotFoundException) {
            Log.d(TAG, "Profile.load(): loading failed calling Refresh()")
            refresh()
        }
    }

    private fun refresh() {

        val stringRequest = object : StringRequest(
            Request.Method.GET,
            "$baseUrl/rider/profile",
            Response.Listener { profileJson ->
                context?.openFileOutput("profile.json", Context.MODE_PRIVATE).use {
                    it?.bufferedWriter()?.write(profileJson)
                }
                load()
            },
            Response.ErrorListener {
                Snackbar.make(fragmentView, "No internet connection.", Snackbar.LENGTH_SHORT).show()
                Log.e(TAG, "profile.refresh() Error in network.")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return HashMap<String, String>().apply {
                    put(
                        "Authorization",
                        Singleton.getInstance(context!!.applicationContext).accessToken
                    )
                    put(
                        "ROLE",
                        "RIDER"
                    )
                }
            }
        }
        Singleton.getInstance(context!!).addToRequestQueue(stringRequest)
    }
}
