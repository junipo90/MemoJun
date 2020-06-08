package com.example.memojun.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class MemoData(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),  // 메모의 고유 ID
    var createdAt: Date = Date(),                   // 작성 시간
    var title: String = "",                         // 제목
    var content: String = "",                       // 내용
    var summary: String = "",                       // 내용 요약
    var imageFile:String = "",                      // 첨부 이미지 파일 이름
    var latitude: Double = 0.0,                     // 위도
    var longitude: Double = 0.0,                    // 경도
    var alarmTime: Date = Date(),                   // 알람 시간
    var weather: String = ""                        // 날씨
    ):RealmObject()