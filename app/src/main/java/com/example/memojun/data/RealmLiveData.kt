package com.example.memojun.data

import androidx.lifecycle.LiveData
import io.realm.RealmChangeListener
import io.realm.RealmObject
import io.realm.RealmResults

class RealmLiveData<T : RealmObject>(private val realmResults: RealmResults<T>) : LiveData<RealmResults<T>>() {

    // 받아온 realmResult 를 value 에 추가 -> Observe 가 동작하기 위해
    init {
        value = realmResults
    }

    // RealmResult 가 갱신 될 때마다 동작할 리스너 -> 갱신되는 값을 value 에 할당
    private val listener =
        RealmChangeListener<RealmResults<T>> { value = it }

    // LiveData 가 활성화 될 때 realmResults 에 리스너를 붙여줌
    override fun onActive() {
        super.onActive()
        realmResults.addChangeListener(listener)
    }

    // LiveData 가 비활성화 될 때 리스너를 제거함
    override fun onInactive() {
        super.onInactive()
        realmResults.removeChangeListener(listener)
    }
}