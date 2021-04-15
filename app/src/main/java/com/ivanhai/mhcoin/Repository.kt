package com.ivanhai.mhcoin

import json.User
import json.info
import json.login

object Repository {
    lateinit var Login : login
    var Info : info = info("ok", User("Баланс", "Почта", "Ник"))
}