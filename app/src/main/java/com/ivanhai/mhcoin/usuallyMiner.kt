package com.ivanhai.mhcoin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import kotlinx.coroutines.*
import java.lang.Runnable

class usuallyMiner : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usually_miner)
        var logCorountine = MainScope()
        initPython()

        findViewById<EditText>(R.id.usuallyUsername).setText(Repository.Info.user.username)
        findViewById<Button>(R.id.usuallyGo).setOnClickListener {
            Thread(Runnable {
                val username = findViewById<EditText>(R.id.usuallyUsername).text.toString()
                start(username)
            }).start()
        }
        findViewById<Button>(R.id.updateLogs).setOnClickListener {
            findViewById<RecyclerView>(R.id.usuallyLogs).apply {
                layoutManager = LinearLayoutManager(this@usuallyMiner)
                adapter = LogsAdapter(log().split("\n"))
            }
            findViewById<RecyclerView>(R.id.usuallyLogs).smoothScrollToPosition(log().split("\n").size - 1)
        }
        findViewById<CheckBox>(R.id.autoUpdate).setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                logCorountine.launch {
                    while (true){
                        findViewById<RecyclerView>(R.id.usuallyLogs).apply {
                            layoutManager = LinearLayoutManager(this@usuallyMiner)
                            adapter = LogsAdapter(log().split("\n"))
                        }
                        findViewById<RecyclerView>(R.id.usuallyLogs).smoothScrollToPosition(log().split("\n").size - 1)
                        delay(5000)
                    }
                }
            }
            else{
                logCorountine.cancel()
                logCorountine = MainScope()
            }
        }
    }

    private fun initPython(){
        if(!Python.isStarted()){
            Python.start(AndroidPlatform(this))
        }
    }

    private fun start(username : String): String {
        val python = Python.getInstance()
        val pythonFile = python.getModule("Miner")
        return pythonFile.callAttr("main", username).toString()
    }

    private fun log(): String {
        val python = Python.getInstance()
        val pythonFile = python.getModule("Miner")
        return pythonFile.callAttr("loger").toString()
    }
}