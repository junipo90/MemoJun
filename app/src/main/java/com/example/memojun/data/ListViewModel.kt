package com.example.memojun.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm

class ListViewModel : ViewModel() {

    // Realm 인스턴스를 생성하여 사용하는 변수
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    // Realm 인스턴스를 넣어 MemoDao 를 생성하여 사용하는 변수
    private val memoData: MemoDao by lazy {
        MemoDao(realm)
    }

    // MemoDao 에서 모든 메로를 가져와서 RealmLiveData 로 변환하여 사용하는 변수
    val memoLiveData:RealmLiveData<MemoData> by lazy{
        RealmLiveData<MemoData> (memoData.getAllMemo())
    }

    // LiveVeiwModel 을 더이상 사용하지 않을 때 Realm 인스턴스를 닫아줌
    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}