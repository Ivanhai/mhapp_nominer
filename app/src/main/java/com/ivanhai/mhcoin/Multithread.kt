package com.ivanhai.mhcoin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Multithread : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multithread)
        var logCorountine = MainScope()
        initPython()

        findViewById<EditText>(R.id.minerUser).setText(Repository.Info.user.username)
        findViewById<Button>(R.id.goMiner).setOnClickListener {
            val user = findViewById<EditText>(R.id.minerUser).text.toString()
            val thread = findViewById<EditText>(R.id.minerThreads).text.toString()
            Thread(Runnable {
                start(user, thread.toInt())
            }).start()
            findViewById<Button>(R.id.updateLogs).setOnClickListener {
                findViewById<RecyclerView>(R.id.recyclerLogs).apply {
                    layoutManager = LinearLayoutManager(this@Multithread)
                    adapter = LogsAdapter(log().split("\n"))
                }
                findViewById<RecyclerView>(R.id.recyclerLogs).smoothScrollToPosition(log().split("\n").size - 1)
            }
            findViewById<CheckBox>(R.id.autoUpdate).setOnCheckedChangeListener { _, isChecked ->
                if(isChecked){
                    logCorountine.launch {
                        while (true){
                            findViewById<RecyclerView>(R.id.recyclerLogs).apply {
                                layoutManager = LinearLayoutManager(this@Multithread)
                                adapter = LogsAdapter(log().split("\n"))
                            }
                            findViewById<RecyclerView>(R.id.recyclerLogs).smoothScrollToPosition(log().split("\n").size - 1)
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
    }

    private fun initPython(){
        if(!Python.isStarted()){
            Python.start(AndroidPlatform(this))
        }
    }

    private fun start(username : String, threads : Int): PyObject? {
        val python = Python.getInstance()
        val pythonFile = python.getModule("Multithreaded_PC_Miner")
        return pythonFile.callAttr("main", username, threads)
    }

    private fun log(): String {
        val python = Python.getInstance()
        val pythonFile = python.getModule("Multithreaded_PC_Miner")
        return pythonFile.callAttr("loger").toString()
    }
}