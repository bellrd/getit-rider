package com.getitfoodie.rider

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        //toolbar.title = "Today's Order"
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectListener)
        bottomNavigationView.selectedItemId = R.id.nav_order
    }

    private val mOnNavigationItemSelectListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_order -> {
                    toolbar.title = "Today's Order"
                    toolbar.subtitle = ""
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragment_container, OrderFragment())
                    transaction.setCustomAnimations(
                        android.R.animator.fade_in,
                        android.R.animator.fade_out
                    )
                    transaction.commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_history -> {
                    toolbar.title = "Order History"
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.setCustomAnimations(
                        android.R.animator.fade_in,
                        android.R.animator.fade_out
                    )
                    transaction.replace(R.id.fragment_container, HistoryFragment())
                    transaction.commit()
                    return@OnNavigationItemSelectedListener true
                }

                R.id.nav_profile -> {
                    toolbar.title = "Profile"
                    toolbar.subtitle = ""
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.setCustomAnimations(
                        android.R.animator.fade_in,
                        android.R.animator.fade_out
                    )
                    transaction.replace(R.id.fragment_container, ProfileFragment())
                    transaction.commit()
                    return@OnNavigationItemSelectedListener true
                }

                R.id.nav_transaction -> {
                    toolbar.title = "Transaction"
                    toolbar.subtitle = ""
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.setCustomAnimations(
                        android.R.animator.fade_in,
                        android.R.animator.fade_out
                    )
                    transaction.replace(R.id.fragment_container, TransactionFragment())
                    transaction.commit()
                    return@OnNavigationItemSelectedListener true
                }

            }
            false
        }
}
