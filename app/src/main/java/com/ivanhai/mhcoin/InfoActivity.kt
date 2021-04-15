package com.ivanhai.mhcoin

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.google.gson.Gson
import com.ivanhai.mhcoin.Repository.Info
import com.ivanhai.mhcoin.Repository.Login
import json.info
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import util.mhApi.rest
import java.io.IOException

class InfoActivity : AppCompatActivity() {
    val gson = Gson()
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        findViewById<TextView>(R.id.login).text = Info.user.username

        val formattedBalance = "%.8f Պ".format(Info.user.balance.toDouble())

        findViewById<TextView>(R.id.balance).text = "Баланс: $formattedBalance"
        findViewById<TextView>(R.id.registerEmail).text = "Почта: ${Info.user.email}"

        findViewById<ImageButton>(R.id.refreshInfo).setOnClickListener {
            rest().checkInfo(Login.token).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }

                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call, response: Response) {
                    val jsonInfo = response.body()?.string()
                    Info = gson.fromJson(jsonInfo, info::class.java)
                    if(Info.status == "ok"){
                        runOnUiThread {
                            findViewById<TextView>(R.id.login).text = Info.user.username
                            findViewById<TextView>(R.id.balance).text = "Баланс: ${Info.user.balance}"
                            findViewById<TextView>(R.id.registerEmail).text = "Почта: ${Info.user.email}"
                        }
                    }
                }
            })
        }
        findViewById<ImageButton>(R.id.transfer).setOnClickListener {
            val intent = Intent(
                this@InfoActivity,
                TransfersActivity::class.java
            )
            startActivity(intent)
        }
        findViewById<Button>(R.id.changePassword).setOnClickListener {
            val intent = Intent(
                this@InfoActivity,
                ChangePassword::class.java
            )
            startActivity(intent)
        }
        findViewById<Button>(R.id.topUsers).setOnClickListener {
            val intent = Intent(
                this@InfoActivity,
                TopUsers::class.java
            )
            startActivity(intent)
        }
        findViewById<Button>(R.id.serverNetwork).setOnClickListener {
            val intent = Intent(
                this@InfoActivity,
                ServerNetwork::class.java
            )
            startActivity(intent)
        }
    }
}
