package com.ivanhai.mhcoin

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.google.gson.Gson
import json.transferModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import util.mhApi.rest
import java.io.IOException

class TransfersActivity : AppCompatActivity() {
    val gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfers)

        findViewById<Button>(R.id.send).setOnClickListener{
            val recipient = findViewById<EditText>(R.id.recipient)
            val amount = findViewById<EditText>(R.id.amount)
            if(recipient.text.toString().isEmpty()/* || recipient.text.toString() == "Получатель"*/){ recipient.error = "Неверный получатель" }
            else{
                if(amount.text.toString().isEmpty()/* || amount.text.toString() == "Сумма"*/){ amount.error = "Неверная сумма"}
                else{
                    rest().transfer(Repository.Login.token, recipient.text.toString(), amount.text.toString()).enqueue(object :
                        Callback {
                        override fun onFailure(call: Call, e: IOException) {

                        }

                        override fun onResponse(call: Call, response: Response) {
                            val jsonTransfer = response.body()?.string()
                            val transfer = gson.fromJson(jsonTransfer, transferModel::class.java)
                            val error = findViewById<TextView>(R.id.errorInfo)
                            runOnUiThread {
                                if(transfer.error == "ERROR_TRANSFER"){
                                    findViewById<EditText>(R.id.recipient).error = "Неверный получатель"
                                }
                                else{
                                    if(transfer.error == "INVALID_AMOUNT"){
                                        findViewById<EditText>(R.id.amount).error = "Неверная сумма"
                                    }
                                    else{
                                        if(transfer.error == "YOU_ARE_RECIPIENT"){
                                            findViewById<EditText>(R.id.recipient).error = "Вы получатель"
                                        }
                                        else{
                                            if(transfer.status == "ok"){
                                                error.setTextColor(Color.GREEN)
                                                error.text = "Отправил средства"
                                            }
                                        }}
                                }
                            }
                        }
                    })
                }
            }
        }
    }
}