package com.example.memojun

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memojun.data.ListViewModel
import com.example.memojun.data.MemoListAdapter
import kotlinx.android.synthetic.main.fragment_memo_list.*

/**
 * A simple [Fragment] subclass.
 */
// 실제로 RecyclerView 가 표시 되는 곳
class MemoListFragment : Fragment() {

    // MemoListAdapter, ListViewModel 을 담을 변수 선언
    private lateinit var listAdapter: MemoListAdapter
    private var viewModel: ListViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_memo_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = activity!!.application!!.let {
            ViewModelProvider(
                // activity 의 viewModelStore 를 쓰는 이유는 activity 와 viewModel 을 공유 할 수 있음
                // 여기서 Fragment 의 viewModel 을 사용 하면 데이터가 공유 되지 않음
                activity!!.viewModelStore,
                ViewModelProvider.AndroidViewModelFactory(it))
                .get(ListViewModel::class.java)
        }

        // 생성 된 viewModel 에서 memoLiveData 를 가져와
        // Adapter 에 담아서 RecyclerView 에 출력
        viewModel!!.let {
            it.memoLiveData.value?.let {
                listAdapter = MemoListAdapter(it)
                memoListView.layoutManager =
                    LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                memoListView.adapter = listAdapter
                listAdapter.itemClickListener = {
                    val intent = Intent(activity, DetailActivity::class.java)
                    intent.putExtra("MEMO_ID", it)
                    startActivity(intent)
                }
            }
            // MemoLiveData 의 observe 함수를 통해 값이 변할 때 동작 할 Observe 를 붙여 줌
            it.memoLiveData.observe(this,
                Observer {
                    // Observe 내에서는 adapter 의 갱신 코드를 호출
                    listAdapter.notifyDataSetChanged()
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        listAdapter.notifyDataSetChanged()
    }
}
