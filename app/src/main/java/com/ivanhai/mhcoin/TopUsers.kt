package com.ivanhai.mhcoin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import json.rich
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import util.mhApi.rest
import java.io.IOException

class TopUsers : AppCompatActivity() {
    val gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_users)

        rest().checkRich().enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                val messageJson = response.body()?.string()
                val message = gson.fromJson(messageJson, rich::class.java)

                runOnUiThread {
                    findViewById<RecyclerView>(R.id.recyclerView).apply {
                        layoutManager = LinearLayoutManager(this@TopUsers)
                        adapter = UsersAdapter(message.users)
                    }
                }
            }

        })
    }
}