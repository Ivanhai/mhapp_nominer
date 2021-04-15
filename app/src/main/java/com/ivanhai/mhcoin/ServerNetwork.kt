package com.ivanhai.mhcoin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.gson.Gson
import json.mhServer
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import util.mhApi.rest
import java.io.IOException

class ServerNetwork : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_network)

        val gson = Gson()
        rest().checkServer().enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                val serverJson = response.body()?.string()
                val server = gson.fromJson(serverJson, mhServer::class.java)

                runOnUiThread {
                    findViewById<TextView>(R.id.blocks).text = "Количество блоков:\n${server.network.blocks}"
                    findViewById<TextView>(R.id.difficulty).text = "Сложность:\n${server.network.difficulty}"
                    findViewById<TextView>(R.id.total_emission).text = "Всего монет:\n${server.network.total_emission}"
                    findViewById<TextView>(R.id.users).text = "Всего\nпользователей:\n${server.network.users}"

                    findViewById<TextView>(R.id.cpu).text = "Использование CPU:\n${server.server.cpu}%"
                    findViewById<TextView>(R.id.ram).text = "Использование RAM:\n${server.server.ram}%"
                    findViewById<TextView>(R.id.version).text = "Версия сервера:\n${server.server.version}"
                }
            }
        })
        findViewById<Button>(R.id.updateServerInfo).setOnClickListener {
            rest().checkServer().enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    TODO("Not yet implemented")
                }

                override fun onResponse(call: Call, response: Response) {
                    val serverJson = response.body()?.string()
                    val server = gson.fromJson(serverJson, mhServer::class.java)

                    runOnUiThread {
                        findViewById<TextView>(R.id.blocks).text = "Количество блоков:\n${server.network.blocks}"
                        findViewById<TextView>(R.id.difficulty).text = "Сложность:\n${server.network.difficulty}"
                        findViewById<TextView>(R.id.total_emission).text = "Всего монет:\n${server.network.total_emission}"
                        findViewById<TextView>(R.id.users).text = "Всего\nпользователей:\n${server.network.users}"

                        findViewById<TextView>(R.id.cpu).text = "Использование CPU:\n${server.server.cpu}%"
                        findViewById<TextView>(R.id.ram).text = "Использование RAM:\n${server.server.ram}%"
                        findViewById<TextView>(R.id.version).text = "Версия сервера:\n${server.server.version}"
                    }
                }
            })
        }
    }
}