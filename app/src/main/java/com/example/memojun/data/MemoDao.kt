package com.example.memojun.data

import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.util.*

// 생성자에서 Realm 인스턴스를 받음
class MemoDao(private val realm: Realm) {
    // 생성시간 역순으로 정렬해서 받아옴
    fun getAllMemo(): RealmResults<MemoData> {
        return realm.where(MemoData::class.java)
            .sort("createdAt", Sort.DESCENDING)
            .findAll()
    }
    // 지정된 id의 메모를 가져옴
    fun selectMemo(id:String):MemoData{
        return realm.where(MemoData::class.java)
            .equalTo("id", id)
            .findFirst() as MemoData
    }
    // 메모 생성, 업데이트
    fun addOrUpdateMemo(memoData: MemoData){
        // DB 를 업데이트 하는 쿼리는 반드시 executeTransaction 함수로 감싸야 함 -> 쿼리가 끝날 때까지 DB를 안전하게 사용가능
        realm.executeTransaction{
            memoData.createdAt = Date()

            if(memoData.content.length > 100){
                memoData.summary = memoData.content.substring(0..100)
            }else{
                memoData.summary = memoData.content
            }

            // Managed 상태가 아니면 copyToRealm() 함수로 DB에 추가
            if(!memoData.isManaged){
                it.copyToRealm(memoData)
            }

        }
    }

    // 전체 MemoData 중 alarmTime 이 현재 시간보다 큰 데이터만 가져오는 함수
    fun getActiveAlarms():RealmResults<MemoData>{
        return realm.where(MemoData::class.java)
            .greaterThan("alarmTime", Date())
            .findAll()
    }
}