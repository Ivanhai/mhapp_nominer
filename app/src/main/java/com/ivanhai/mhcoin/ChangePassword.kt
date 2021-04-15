package com.ivanhai.mhcoin

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.gson.Gson
import com.ivanhai.mhcoin.Repository.Login
import json.transferModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import util.mhApi.rest
import java.io.IOException

class ChangePassword : AppCompatActivity() {
    val gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        findViewById<Button>(R.id.changePass).setOnClickListener {
            val oldPassword = findViewById<EditText>(R.id.oldPassword).text.toString()
            val newPassword = findViewById<EditText>(R.id.newPassword).text.toString()

            rest().changePassword(Login.token, oldPassword, newPassword).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {
                    val messageJson = response.body()?.string()

                    val message = gson.fromJson(messageJson, transferModel::class.java)

                    if(message.error == "OLD_PASSWORD_INVALID"){
                        runOnUiThread {
                            findViewById<EditText>(R.id.oldPassword).error = "Неверный пароль"
                        }
                    }
                    else{
                        if(message.status == "ok"){
                            runOnUiThread {
                                findViewById<TextView>(R.id.passwordInfo).setTextColor(Color.GREEN)
                                findViewById<TextView>(R.id.passwordInfo).text = "Сменил пароль"
                            }
                        }
                    }
                }

            })
        }
    }
}