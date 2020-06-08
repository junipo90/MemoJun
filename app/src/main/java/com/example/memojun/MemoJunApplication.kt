package com.example.memojun

import android.app.Application
import io.realm.Realm

class MemoJunApplication :Application(){

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}