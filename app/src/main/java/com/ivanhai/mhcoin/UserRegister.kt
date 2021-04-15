package com.ivanhai.mhcoin

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import util.mhApi.socket
import java.io.IOException
import java.net.Socket

class UserRegister : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_register)

        findViewById<Button>(R.id.registerButton).setOnClickListener {
            val email = findViewById<EditText>(R.id.registerEmail).text.toString()
            val login = findViewById<EditText>(R.id.Login).text.toString()
            val password = findViewById<EditText>(R.id.Password).text.toString()

            //val message = socket().regApi(listOf(login, password, email))
            socket().regApi().enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }

                override fun onResponse(call: Call, response: Response) {
                    val txt = response.body()?.string()

                    val poolAddress = txt?.lines()?.get(0) as String
                    val poolPort = txt.lines()[1]

                    val s = Socket(poolAddress, poolPort.toInt())

                    val inputStream = s.getInputStream()
                    val outputStream = s.getOutputStream()

                    val data = ByteArray(256)
                    outputStream.write("REGI,${login},${password},${email}".toByteArray())

                    inputStream.read(data)
                    inputStream.read(data)

                    var message = String(data).split(",")
                    if(message.size == 2){
                        message = listOf(message[0], message[1].replace("[^A-Za-zА-Яа-я0-9 ]".toRegex(), ""))
                        if(message.contains("This account already exists")){
                            runOnUiThread{
                                findViewById<EditText>(R.id.Login).error = "Аккаунт уже существует"
                            }
                        }
                    }
                    else{
                        message = listOf(message[0].replace("[^A-Za-zА-Яа-я0-9 ]".toRegex(), ""))
                        if(message[0].contains("OK")){
                            runOnUiThread{
                                findViewById<TextView>(R.id.registerInfo).setTextColor(Color.GREEN)
                                findViewById<TextView>(R.id.registerInfo).text = "Успешно зарегистрировал аккаунт."
                            }
                        }
                    }
                }
            })
        }
    }
}