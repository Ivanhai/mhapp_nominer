package com.ivanhai.mhcoin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MinerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_miner)

        findViewById<Button>(R.id.usuallyMiner).setOnClickListener {
            val intent = Intent(
                this@MinerActivity,
                usuallyMiner::class.java
            )
            startActivity(intent)
        }

        findViewById<Button>(R.id.multithreadMiner).setOnClickListener {
            val intent = Intent(
                this@MinerActivity,
                Multithread::class.java
            )
            startActivity(intent)
        }
    }
}