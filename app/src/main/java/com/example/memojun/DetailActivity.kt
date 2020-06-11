package com.example.memojun

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.memojun.data.DetailViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.takisoft.datetimepicker.DatePickerDialog
import com.takisoft.datetimepicker.TimePickerDialog
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*
import java.io.File
import java.sql.Time
import java.util.*

class DetailActivity : AppCompatActivity() {

    // DetailViewModel 을 위한 변수
    private var viewModel: DetailViewModel? = null
    // 날짜와 시간을 다이얼로그에서 설정 중인 값을 임시로 저장하는 변수
    private var dialogCalender = Calendar.getInstance()

    // 기기내의 저장된 이미지는 intent를 이용해 가져올 수 있는데,
    // intent로 activity 결과를 요청할때 사용되는 요청코 값을 추가함
    private val REQUEST_IMAGE = 1000

    // 날짜 다이얼로그를 여는 함수
    private fun openDateDialog() {
        val datePickerDialog = DatePickerDialog(this)
        datePickerDialog.setOnDateSetListener { view, year, month, dayOfMonth ->
            // 다이얼로그에서 받은 값은 임시 변수에 저장
            dialogCalender.set(year, month, dayOfMonth)
            openTimeDialog()
        }
        datePickerDialog.show()
    }

    // 시간 다이얼로그를 여는 함수
    private fun openTimeDialog() {
        val timePickerDialog = TimePickerDialog(
            this,
            // 사용자가 입력한 시간을 임시 캘린더 변수에 저장하고 캘린더 변수의 time을 viewModel에 새 알람 값으로 설정
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                dialogCalender.set(Calendar.HOUR_OF_DAY, hourOfDay)
                dialogCalender.set(Calendar.MINUTE, minute)

                viewModel?.setAlarm(dialogCalender.time)
            },
            0, 0, false
        )
        timePickerDialog.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        // 기기내에서 이미지파일을 읽어올수 있는 ACTION_GET_CONTENT 사용하여 해당 기능이 있는 activity 호출
        fab.setOnClickListener { view ->
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            // 실행시 요청코드를 넣어 결과를 받아옴
            startActivityForResult(intent, REQUEST_IMAGE)
        }

        // ViewModel 생성
        viewModel = application!!.let {
            ViewModelProvider(viewModelStore, ViewModelProvider.AndroidViewModelFactory(it))
                .get(DetailViewModel::class.java)
        }
        // ViewModel 에 observe 를 걸어 화면 갱신
        viewModel!!.memoLiveData.observe(this, Observer {
            supportActionBar?.title = it.title
            contentEdit.setText(it.content)
            alarmInfoView.setAlarmDate(it.alarmTime)
            locationInfoView.setLocation(it.latitude, it.longitude)
            weatherInfoView.setWeather(it.weather)

            // 이미지 파일 경로를 Uri로 바꿔 bgImage에 설정
            val imageFile = File(
                getDir("image", Context.MODE_PRIVATE),
                it.id + ".jpg")

            bgImage.setImageURI(imageFile.toUri())
        })

        // ListActivity 에서 아이템을 선택 했을 때 보내주는 메모 id로 데이터를 로드
        val memoId = intent.getStringExtra("MEMO_ID")
        // 새로 작성할 때는 메모 id가 없어 루틴 작동 안함
        if (memoId != null) {
            viewModel!!.loadMemo(memoId)
        }

        // 툴바를 누르면 제목 작성
        toolbar_layout.setOnClickListener {
            // LayoutInflater 로 레이아웃 xml 을 view 로 변환
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_title, null)
            // view 에 포함된 titleEdit 을 변수에 담음
            val titleEdit = view.findViewById<EditText>(R.id.titleEdit)

            // 다이얼로그 창 제작
            AlertDialog.Builder(this)
                .setTitle("제목을 입력하세요")
                .setView(view)  // 다이얼로그 내용이 되는 view 설정
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                    // 제목이 변경될 때 viewModel의 memoData도 함께 갱신 됨
                    supportActionBar?.title = titleEdit.text.toString()
                    viewModel!!.memoData.title = titleEdit.text.toString()
                }).show()
        }

        // 내용이 변경될 때마다 Listener 내에서 viewModel의 memoData의 내용도 함께 변경
        contentEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel!!.memoData.content = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        locationInfoView.setOnClickListener {
            val latitude = viewModel!!.memoData.latitude
            val longitude = viewModel!!.memoData.longitude

            if (!(latitude == 0.0 && longitude == 0.0)) {
                // 네이버 지도 sdk에서 제공하는 MapView객체 생성 (지도를 출력하는 view)
                val mapView = MapView(this)
                mapView.getMapAsync {
                    // 메모의 위치 정보를 NaverMap의 CameraUpdate 객체에 넣어줌
                    val latitude = viewModel!!.memoData.latitude
                    val longitude = viewModel!!.memoData.longitude
                    val cameraUpdate = CameraUpdate.scrollTo(LatLng(latitude, longitude))
                    it.moveCamera(cameraUpdate) // CameraUpdate객체를 NaverMap에 설정
                }
                // 모든 설정이 끝난 mapView는 AlertDialog에 설정하여 표시
                AlertDialog.Builder(this)
                    .setView(mapView)
                    .show()
            }

        }
    }

    // 뒤로가기를 눌렀을 때 동작하는 함수
    override fun onBackPressed() {
        super.onBackPressed()

        // viewModel 의 addOrUpdateMemo 함수 호출해 메모 DB 갱신
        viewModel?.addOrUpdateMemo(this)
    }

    // activity 에서 사용할 메뉴를 설정하는 함수를 override
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // menu_detail.xml 을 menuInflater를 통해 activity의 메뉴로 사용
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true // 메뉴를 사용한다는 의미로 true 반환
    }

    // 메뉴 아이템이 선택 되었을때 실행되는 함수
    // Intro Activity 에서 이미 체크한 위치 권한 정보를 다시 체크 하지 않기 위한 annotaion
    @SuppressLint("MissingPermission")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_share -> {
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, supportActionBar?.title)
                intent.putExtra(Intent.EXTRA_TEXT, contentEdit.text.toString())

                startActivity(intent)
            }
            R.id.menu_alarm -> {
                if (viewModel?.memoData?.alarmTime!!.after(Date())) {
                    AlertDialog.Builder(this)
                        .setTitle("안내")
                        .setMessage("기존에 알람이 설정되어 있습니다 삭제 또는 재설정할 수 있습니다")
                        .setPositiveButton("재설정", DialogInterface.OnClickListener { dialog, which ->
                            openDateDialog()
                        })
                        .setNegativeButton("삭제", DialogInterface.OnClickListener { dialog, which ->
                            viewModel?.deleteAlarm()
                        })
                        .show()
                } else {
                    openDateDialog()
                }
            }

            R.id.menu_location -> {
                AlertDialog.Builder(this)
                    .setTitle("안내")
                    .setMessage("현재 위치를 메모에 저장하거나 삭제할 수 있습니다")
                    .setPositiveButton("위치지정", DialogInterface.OnClickListener { dialog, which ->
                        // locationManager를 가져와 위치기능이 켜져있는지 확인 (GPS, 네트워크)
                        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        val isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        val isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                        // 위치 기능이 둘다 꺼진경우 Snackbar 를 띄워 시스템의 위치 옵션화면을 안내
                        if (!isGPSEnable && !isNetworkEnable) {
                            Snackbar.make(
                                toolbar_layout,
                                "폰의 위치기능을 켜야 기능을 사용할 수 있습니다",
                                Snackbar.LENGTH_LONG
                            )
                                .setAction("설정", View.OnClickListener {
                                    val getToSettins = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                    startActivity(getToSettins)
                                }).show()
                        } else {
                            // 위치 기능이 켜져있으면 정확도, 배터리 소모량을 설정
                            val criteria = Criteria()
                            criteria.accuracy = Criteria.ACCURACY_MEDIUM
                            criteria.powerRequirement = Criteria.POWER_MEDIUM

                            // locationManager에 해당 설정 값을 넘겨 위치 정보를 1회 받아온다
                            // 위치값은 LocationListener의 object를 구현하여 넘겨줘야 함
                            locationManager.requestSingleUpdate(criteria, object : LocationListener {
                                // 위치값을 viewModel에 넘겨줌
                                override fun onLocationChanged(location: Location?) {
                                    location?.run {
                                        viewModel!!.setLocation(latitude, longitude)
                                    }
                                }

                                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

                                override fun onProviderEnabled(provider: String?) {}

                                override fun onProviderDisabled(provider: String?) {}
                            }, null)
                        }
                    }).setNegativeButton("삭제", DialogInterface.OnClickListener { dialog, which ->
                        viewModel!!.deleteLocation()
                    })
                    .show()
            }
            R.id.weatherInfoView -> {
                AlertDialog.Builder(this)
                    .setTitle("안내")
                    .setMessage("현재 날씨 정보를 메모에 저장하거나 삭제할 수 있습니다")
                    .setPositiveButton("날씨 가져오기", DialogInterface.OnClickListener { dialog, which ->
                        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        val isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        val isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                        if (!isGPSEnable && !isNetworkEnable) {
                            Snackbar.make(
                                toolbar_layout,
                                "폰의 위치기능을 켜야 기능을 사용할 수 있습니다",
                                Snackbar.LENGTH_LONG
                            )
                                .setAction("설정", View.OnClickListener {
                                    val getToSettins = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                    startActivity(getToSettins)
                                }).show()
                        } else {
                            val criteria = Criteria()
                            criteria.accuracy = Criteria.ACCURACY_MEDIUM
                            criteria.powerRequirement = Criteria.POWER_MEDIUM

                            locationManager.requestSingleUpdate(criteria, object : LocationListener {
                                override fun onLocationChanged(location: Location?) {
                                    location?.run {
                                        // 위치기능이 켜져있으면 받아온 위치를 setWeather함수에 전달
                                        viewModel!!.setWeather(latitude, longitude)
                                    }
                                }

                                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                                }

                                override fun onProviderEnabled(provider: String?) {
                                }

                                override fun onProviderDisabled(provider: String?) {
                                }
                            }, null)
                        }
                    }).setNegativeButton("삭제", DialogInterface.OnClickListener { dialog, which ->
                        viewModel!!.deleteWeather()
                    })
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Activity(intent를 통해)의 실행결과를 받는 함수
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 요청코드와 결과값이 ok인지 확인
        if(requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            try {
                // 결과값으로 들어온 데이터를 비트맵으로 변환함
                val inputStream = data?.data?.let { contentResolver.openInputStream(it) }
                inputStream?.let {
                    val image = BitmapFactory.decodeStream(it)

                    // bgImage를 초기화하고 새 이미지를 viewModel에 설정
                    bgImage.setImageURI(null)
                    image?.let { viewModel?.setImageFile(this, it) }

                    it.close() // 모두 끝났으면 inputstream 을 닫아줌
                }
            }
            catch (e: Exception) {
                println(e)
            }
        }
    }
}
