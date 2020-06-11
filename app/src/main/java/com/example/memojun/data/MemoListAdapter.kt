package com.example.memojun.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.memojun.R
import kotlinx.android.synthetic.main.item_memo.view.*
import java.text.SimpleDateFormat

                    // 생성자에서 MemoData 의 MutableList를 받음
class MemoListAdapter(private val list: MutableList<MemoData>) : RecyclerView.Adapter<ItemViewHolder>() {

    // Data 객체를 사람이 볼 수 있는 문자열로 만들기 위한 객체
    private val dateFormat = SimpleDateFormat("MM/dd HH:mm")
    lateinit var itemClickListener: (itemId: String) -> Unit

    // item_memo 를 불러 ViewHolder 를 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_memo, parent, false)
        view.setOnClickListener {
            itemClickListener?.run {
                val memoId = it.tag as String
                this(memoId)
            }
        }
        return ItemViewHolder(view)
    }

    // list 에 담긴 MemoData 의 개수 반환
    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        // 제목이 있는 경우 titleView 를 화면에 표시, 없는 경우 titleView 영역까지 숨김
        // VISIBLE = View 를 화면에 표시
        // INVISIBLE = VIew 의 내용만 감추고 영역은 유지
        // GONE = View 의 내용 및 영역까지 숨김
        if (list[position].title.isNotEmpty()) {
            holder.containerView.titleView.visibility = View.VISIBLE
            holder.containerView.titleView.text = list[position].title
        } else {
            holder.containerView.titleView.visibility = View.GONE
        }
        // 요약 내용은 그래도 출력
        holder.containerView.summaryView.text = list[position].summary
        // 작성 시간은 dataFormat 으로 변환하여 dataView 에 표시
        holder.containerView.dataView.text = dateFormat.format(list[position].createdAt)
        holder.containerView.tag = list[position].id
    }
}