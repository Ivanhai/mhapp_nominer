package util.mhApi

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request

class socket { // socket api мх коина
    fun regApi(): Call { // регистрирование аккаунта
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://mhcoin.s3.filebase.com/Server.txt")
            .build()
        return client.newCall(request)
    }
}