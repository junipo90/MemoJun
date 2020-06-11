package com.example.memojun

import android.app.Application
import com.naver.maps.map.NaverMapSdk
import io.realm.Realm

class MemoJunApplication() :Application(){    // 앱을 제어하는 객체 Application 을 상속

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)    // Realm 데이터베이스를 초기화
        // 네이버 클라우드 플랫폼 정보
        NaverMapSdk.getInstance(this).setClient(
            NaverMapSdk.NaverCloudPlatformClient("nhdo5budbn")
        )
    }
}