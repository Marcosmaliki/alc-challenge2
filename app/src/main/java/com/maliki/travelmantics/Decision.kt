package com.maliki.travelmantics

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import kotlinx.android.synthetic.main.activity_decision.*

class Decision : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decision)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        admin.setOnClickListener {
            startActivity(Intent(this, Admin::class.java))
            Animatoo.animateSlideLeft(this)
        }
        user.setOnClickListener {
            startActivity(Intent(this, User::class.java))
            Animatoo.animateSlideLeft(this)
        }
    }
}
