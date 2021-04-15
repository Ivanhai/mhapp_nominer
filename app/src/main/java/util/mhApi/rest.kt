package util.mhApi

import com.google.gson.Gson
import json.userModel
import okhttp3.*
import java.io.IOException

class rest { // rest api мх коина
    val gson = Gson()
    fun checkLogin(user: String, password : String): Call { // вход в аккаунт, получение токена для дальнейшего взаимодействия с API
        val jsonObject = gson.toJson(userModel(user, password))

        val client = OkHttpClient()

        val json = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(json, jsonObject.toString())
        val request = Request.Builder()
                .url("https://mhc.lapki.dev/api/users/login")
                .post(body)
                .build()
        return client.newCall(request)
    }
    fun checkInfo(token : String): Call { // получение информации о текущем аккаунте
        val client = OkHttpClient()

        val request = Request.Builder()
                .url("https://mhc.lapki.dev/api/users/me")
                .addHeader("Authorization", token)
                .build()
        return client.newCall(request)
    }
    fun checkServer(): Call { // информация о сервере и сети MHCoin
        val client = OkHttpClient()

        val request = Request.Builder()
                .url("https://mhc.lapki.dev/api/info")
                .build()
        return client.newCall(request)
    }
    fun checkRich(): Call { // список 10 самых богатых пользователей
        val client = OkHttpClient()

        val request = Request.Builder()
                .url("https://mhc.lapki.dev/api/rich")
                .get()
                .build()
        return client.newCall(request)
    }
    fun transfer(token: String, recipient : String, amount : String): Call { // перевод денег другому пользователю
        val jsonObject = gson.toJson(json.transfer(recipient, amount))

        val client = OkHttpClient()

        val json = MediaType.parse("application/json")
        val body = RequestBody.create(json, jsonObject.toString())
        val request = Request.Builder()
                .url("https://mhc.lapki.dev/api/users/transfer")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", token)
                .build()
        return client.newCall(request)
    }

    fun changePassword(token : String, oldPassword : String, newPassword : String): Call {
        val client = OkHttpClient()

        val json = MediaType.parse("application/json")
        val body = RequestBody.create(json, "{\"password\":\"$oldPassword\",\"new_password\":\"$newPassword\"}")
        val request = Request.Builder()
            .url("https://mhc.lapki.dev/api/users/me/password")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", token)
            .build()
        return client.newCall(request)
    }
}
