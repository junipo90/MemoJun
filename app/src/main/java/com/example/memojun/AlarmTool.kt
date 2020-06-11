package com.example.memojun

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.memojun.data.MemoDao
import io.realm.Realm
import java.util.*

// 알람을 삭제 추가, BroadcastReceiver 기능을 겸함
class AlarmTool : BroadcastReceiver() {
    companion object {
        // 알람 Intent를 분류하기 위한 action 값을 상수로 선언해 둠
        private const val ACTION_RUN_ALARM = "RUN_ALARM"

        // 알람으로 보낼 Intent를 만드는 함수
        private fun createAlarmIntent(context: Context, id: String): PendingIntent {
            // AlarmTool클래스를 목적지로 하는 Intent 생성 (이 클래스가 Receiver 역할도 하기 때문)
            val intent = Intent(context, AlarmTool::class.java)
            // data에 메모 id를 받아 Uri로 추가함 (시스템에서 intent를 구별하는 기준이 됨)
            intent.data = Uri.parse("id:" + id)
            // intent의 extra로 메모 id를 넣어주고 action 유형을 지정함 (receiver코드에서 받아서 사용하는 목적)
            intent.putExtra("MEMO_ID", id)
            intent.action = ACTION_RUN_ALARM

            // intent를 PendingIntent의 broadcast 형태롤 반환
            return PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        // creatAlarmIntent 함수로 알람 Intent를 생성하여 AlarmManager에 알람을 설정하는 함수
        fun addAlarm(context: Context, id: String, alarmTime: Date) {
            val alarmIntent = createAlarmIntent(context, id)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime.time, alarmIntent)
        }

        // 알람 삭제 함수
        fun deleteAlarm(context: Context, id: String) {
            val alarmIntent = createAlarmIntent(context, id)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(alarmIntent)
        }
    }

    // BroadcastReceiver가 broadcast를 받았을 때 동작하는 함수
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            AlarmTool.ACTION_RUN_ALARM -> {
                // intent에 넣었던 메모 id를 받아 DB에서 MemoData를 로드함
                val memoId = intent.getStringExtra("MEMO_ID")
                val realm = Realm.getDefaultInstance()
                val memoData = MemoDao(realm).selectMemo(memoId)

                // Notification에 연결할 intent를 생성
                // Notification을 누르면 해당 메모의 상세화면으로 이동하도록 만듬
                val notificationIntent = Intent(context, DetailActivity::class.java)
                notificationIntent.putExtra("MEMO_ID", memoId)

                // intent를 pendingIntent의 activity 형태로 만들어 반환
                val pendingIntent = PendingIntent.getActivity(
                    context, 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
                )

                // NotificationCompat.Builder 로 notification 생성
                val builder = NotificationCompat.Builder(context, "alarm")
                    .setContentTitle(memoData.title)
                    .setContentText(memoData.content)
                    .setContentIntent(pendingIntent) // 눌렀을때 상세화면으로 이동하도록 pendingIntent를 연결
                    .setAutoCancel(true)             // 누르면 스스로 사라지도록

                // 시스템의 notificationManager를 받아옴
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                // 안드로이드 버전 별로 분기하는 코드
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    builder.setSmallIcon(R.drawable.ic_launcher_foreground)
                    val channel = NotificationChannel(
                        "alarm", "알람 메시지",
                        NotificationManager.IMPORTANCE_HIGH
                    )

                    notificationManager.createNotificationChannel(channel)
                } else {
                    builder.setSmallIcon(R.mipmap.ic_launcher)
                }

                // notificationManager로 알림을 띄움
                notificationManager.notify(1, builder.build())
            }
            // 기기 재부팅시 받는 action
            Intent.ACTION_BOOT_COMPLETED ->
            {
                // 활성화된 알람을 찾아
                val realm = Realm.getDefaultInstance()
                val activeAlarms = MemoDao(realm).getActiveAlarms()

                // 알람을 재등록 해줌
                for(memoData in activeAlarms) {
                    addAlarm(context, memoData.id, memoData.alarmTime)
                }
            }
        }
    }
}