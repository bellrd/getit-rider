package com.getitfoodie.rider

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.io.FileNotFoundException

class MainEmptyActivity : AppCompatActivity() {

    var inent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val file = openFileInput("LOGINFILE")
            file.close()
            intent = Intent(this, MainActivity::class.java)

        } catch (e: FileNotFoundException) {
            intent = Intent(this, MainActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}
