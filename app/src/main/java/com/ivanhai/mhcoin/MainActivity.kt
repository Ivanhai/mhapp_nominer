package com.ivanhai.mhcoin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.ivanhai.mhcoin.Repository.Info
import com.ivanhai.mhcoin.Repository.Login
import json.info
import json.login
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import util.mhApi.rest
import java.io.IOException

class MainActivity : AppCompatActivity() {
    val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mSettings = getSharedPreferences("login", MODE_PRIVATE)
        if(mSettings.contains("username") && mSettings.contains("password")){
            findViewById<EditText>(R.id.Login).setText(mSettings.getString("username", ""))
            findViewById<EditText>(R.id.Password).setText(mSettings.getString("password", ""))
        }

        

        findViewById<TextView>(R.id.register).setOnClickListener {
            val intent = Intent(
                this@MainActivity,
                UserRegister::class.java
            )
            startActivity(intent)
        }

        findViewById<Button>(R.id.registerButton).setOnClickListener {
            val user = findViewById<EditText>(R.id.Login).text.toString()
            val password = findViewById<EditText>(R.id.Password).text.toString()

            rest().checkLogin(user, password).enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {
                    val jsonLogin = response.body()?.string()
                    Login = gson.fromJson(jsonLogin, login::class.java)
                    if(Login.status == "ok"){
                        runOnUiThread {
                            if(findViewById<CheckBox>(R.id.rememberMe).isChecked){
                                getSharedPreferences("login", MODE_PRIVATE)
                                    .edit()
                                    .putString("username", user)
                                    .putString("password", password)
                                    .apply()
                            }
                        }
                            rest().checkInfo(Login.token).enqueue(object : Callback {
                                override fun onFailure(call: Call, e: IOException) {

                                }

                                @SuppressLint("SetTextI18n")
                                override fun onResponse(call: Call, response: Response) {
                                    val jsonInfo = response.body()?.string()
                                    Info = gson.fromJson(jsonInfo, info::class.java)
                                    if(Info.status == "ok"){
                                        val intent = Intent(
                                            this@MainActivity,
                                            InfoActivity::class.java
                                        )
                                        startActivity(intent)
                                        }
                                    }
                                })
                    }
                    else{
                        if(Login.error == "NO_SUCH_USER"){
                            runOnUiThread {
                                findViewById<EditText>(R.id.Login).error = "Неверный ник"
                            }
                        }
                        else{
                            if(Login.error == "INVALID_PASSWORD"){
                                runOnUiThread {
                                    findViewById<EditText>(R.id.Password).error = "Неверный пароль"
                                }
                            }
                        }
                    }
                }
            })
        }
    }
}
