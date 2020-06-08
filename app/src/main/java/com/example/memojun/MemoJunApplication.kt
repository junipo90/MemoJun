package com.example.memojun

import android.app.Application
import com.naver.maps.map.NaverMapSdk
import io.realm.Realm

class MemoJunApplication :Application(){

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        NaverMapSdk.getInstance(this).setClient(
            NaverMapSdk.NaverCloudPlatformClient("nhdo5budbn")
        )
    }
}