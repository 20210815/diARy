package com.example.diary

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DiaryAdapter(private var diaries: List<MyDiary>) : RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.diary_recyclerview, parent, false)
        return DiaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val diary = diaries[position]
        holder.bind(diary)
    }

    override fun getItemCount(): Int {
        return diaries.size
    }

    // 데이터 업데이트 메서드 추가
    fun updateData(newDiaries: List<MyDiary>) {
        diaries = newDiaries
        notifyDataSetChanged()
    }

    inner class DiaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.diary_title)
        private val placeTextView: TextView = itemView.findViewById(R.id.diary_place)
        private val periodTextView: TextView = itemView.findViewById(R.id.diary_period)
        private val diaryLikeView: TextView = itemView.findViewById(R.id.diary_like)
        private val commentView: TextView = itemView.findViewById(R.id.diary_comment)

        init {
            itemView.setOnClickListener {
                val clickedDiary = diaries[adapterPosition]
                val diaryId = clickedDiary.diaryId // 클릭된 일기의 diaryId를 가져옴
                val intent = Intent(itemView.context, DiaryDetailActivity::class.java)
                intent.putExtra("diaryId", diaryId)
                itemView.context.startActivity(intent)
            }
        }

        fun bind(diary: MyDiary) {
            titleTextView.text = diary.title
            placeTextView.text = diary.travelDest
            periodTextView.text = "${diary.travelStart} ~ ${diary.travelEnd}"

            //일기 비공개라면
            if (diary.public == false) {
                diaryLikeView.text = "비공개"
            } else {
                diaryLikeView.text = "${diary.diaryLike}" //일기 좋아요 수 (수정 필요)
                commentView.text = "${diary.comment}"
            }
        }
    }
}