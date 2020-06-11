package com.example.memojun.data

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memojun.AlarmTool
import io.realm.Realm
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.*

class DetailViewModel : ViewModel() {

    var memoData = MemoData()
    val memoLiveData: MutableLiveData<MemoData> by lazy {
        MutableLiveData<MemoData>().apply { value = memoData }
    }

    // Realm 인스턴스와 memoDao 를 초기화하고 realm 을 닫아줌
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

    // 메모를 수정할 때 메모의 id 를 받아와 memoData 를 로드하는 함수
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

    fun deleteLocation(){
        memoData.latitude = 0.0
        memoData.longitude = 0.0
        memoLiveData.value = memoData
    }

    fun setLocation(latitude:Double, longtitude:Double){
        memoData.latitude = latitude
        memoData.longitude = longtitude
        memoLiveData.value = memoData
    }

    fun deleteWeather(){
        memoData.weather = ""
        memoLiveData.value = memoData
    }

    // 받아온 좌표 정보를 WeatherData에 넘겨 날씨 정보를 가져와 memoData에 저장
    fun setWeather(latitude: Double, longtitude: Double){
        // viewModelScope -> ViewModel이 소멸할 때에 맞춰 코루틴을 정지시켜줌
        viewModelScope.launch {
            memoData.weather = WeatherData.getCurrentWeather(latitude, longtitude)
            memoLiveData.value = memoData
        }
    }

    // 이미지를 받아 설정하는 함수
    fun setImageFile(context: Context,bitmap: Bitmap){
        // 이미지를 저장할 파일 경로를 생성 (내부 저장소 앱 영역 image 폴더에 저장
        val imageFile = File(
            // context의 getDir함수로 앱데이터 폴더 내의 image라는 하위 폴더 지정
            context.getDir("image", Context.MODE_PRIVATE),
            memoData.id + ".jpg") // 파일명은 메모id.jpg로 지정

        // 기존에 파일이 있다면 삭제
        if (imageFile.exists()) imageFile.delete()

        try {
            // imageFile 객체에 지정된 경로로 새 파일을 생성함
            imageFile.createNewFile()

            // FileOutputStream 으로 패러미터로 받은 이미지 데이터를 JPEG으로 압축하여 저장하고 stream 객체를 닫아줌
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.close()

            // 저장이 끝나면 저장한 이미지 이름을 memoData에 갱신함
            memoData.imageFile = memoData.id + ".jpg"
            memoLiveData.value = memoData
        }
        catch (e: Exception) {
            println(e)
        }
    }

    // 메모 추가나 수정시 사용하기 위해 MemoDao 와 기능 연결
    fun addOrUpdateMemo(context: Context) {
        memoDao.addOrUpdateMemo(memoData)

        // 메모와 연결된 기존 알람을 삭제하고 새 알람시간이 현재시간 이후라면 새로 등록해줌
        AlarmTool.deleteAlarm(context, memoData.id)
        if (memoData.alarmTime.after(Date())) {
            AlarmTool.addAlarm(context, memoData.id, memoData.alarmTime)
        }
    }
}