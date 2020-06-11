package com.example.memojun.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

// RealmObject 는 open 이어야 함
open class MemoData(
    @PrimaryKey     // 테이블에서 각 레코드를 구분할 수 있는 고유값 (업데이트나 삭제의 기준값)
    var id: String = UUID.randomUUID().toString(),  // 메모의 고유 ID, UUID는 랜덤한 고유값을 자동으로 생성
    var createdAt: Date = Date(),                   // 작성 시간
    var title: String = "",                         // 제목
    var content: String = "",                       // 내용
    var summary: String = "",                       // 내용 요약
    var imageFile:String = "",                      // 첨부 이미지 파일 이름
    var latitude: Double = 0.0,                     // 위도
    var longitude: Double = 0.0,                    // 경도
    var alarmTime: Date = Date(),                   // 알람 시간
    var weather: String = ""                        // 날씨
    ):RealmObject()     // RealmObject 를 상속 받음