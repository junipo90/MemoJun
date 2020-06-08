package com.example.memojun.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memojun.AlarmTool
import io.realm.Realm
import java.util.*

class DetailViewModel : ViewModel() {
//    val title: MutableLiveData<String> = MutableLiveData<String>().apply { value = "" }
//    val content: MutableLiveData<String> = MutableLiveData<String>().apply { value = "" }
//    val alarmTime: MutableLiveData<Date> = MutableLiveData<Date>().apply { value = Date(0) }
//
//    private var memoData = MemoData()

    var memoData = MemoData()
    val memoLiveData: MutableLiveData<MemoData> by lazy {
        MutableLiveData<MemoData>().apply { value = memoData }
    }

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    private val memoDao: MemoDao by lazy {
        MemoDao(realm)
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

    fun loadMemo(id: String) {
        memoData = realm.copyFromRealm(memoDao.selectMemo(id))
        memoLiveData.value = memoData
    }

    fun deleteAlarm() {
        memoData.alarmTime = Date(0)
        memoLiveData.value = memoData
    }

    fun setAlarm(time: Date) {
        memoData.alarmTime = time
        memoLiveData.value = memoData
    }

    fun deleteLacation(){
        memoData.latitude = 0.0
        memoData.longitude = 0.0
        memoLiveData.value = memoData
    }

    fun setLocation(latitude:Double, longtitude:Double){
        memoData.latitude = latitude
        memoData.longitude = longtitude
        memoLiveData.value = memoData

    }

    fun addOrUpdateMemo(context: Context) {
        memoDao.addOrUpdateMemo(memoData)

        AlarmTool.deleteAlarm(context, memoData.id)
        if (memoData.alarmTime.after(Date())) {
            AlarmTool.addAlarm(context, memoData.id, memoData.alarmTime)
        }
    }
}