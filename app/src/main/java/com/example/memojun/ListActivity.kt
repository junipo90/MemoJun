package com.example.memojun

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.memojun.data.ListViewModel
import com.example.memojun.data.MemoData

import kotlinx.android.synthetic.main.activity_list.*
import java.util.*

class ListActivity : AppCompatActivity() {

    // viewModel 을 담을 변수
    private var viewModel: ListViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setSupportActionBar(toolbar)

        // fab 버튼을 클릭하면 DetailActivity 로 이동
        fab.setOnClickListener {
            val intent = Intent(applicationContext, DetailActivity::class.java)
            startActivity(intent)
        }

        // MemoListFragment 를 화면에 표시
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.contentLayout, MemoListFragment())
        fragmentTransaction.commit()

        // ViewModel 을 가져옴
        // ViewModel 은 앱을 기준으로 생성됨 -> application 이 null 이 아닌지 체크하고,
        viewModel = application!!.let {
            // ViewModel 을 가져오도록 ViewModelProvider 객체를 생성
            // viewModelStore 는 ViewModel 의 생성과 소멸 기준
            // ViewModelFactory 는 ViewModel 을 실제로 생성하는 객체
            ViewModelProvider(viewModelStore, ViewModelProvider.AndroidViewModelFactory(it))
                .get(ListViewModel::class.java)
                // ViewModelProvider 의 get 함수를 이용해 ListViewModel 을 얻을 수 있음
        }
    }

}
