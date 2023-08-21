package com.example.diary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class DiaryFragment : Fragment() {
    private lateinit var diaryAdapter: DiaryAdapter
    private lateinit var recyclerView: RecyclerView

    // 여행지 데이터를 저장할 리스트
    private val diaryList = mutableListOf<MyDiary>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_diary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //일기 작성 버튼 클릭 시, bottomSheet 출력
        val addDiaryButton = view.findViewById<ImageView>(R.id.diary_add_btn)
        addDiaryButton.setOnClickListener {
            val bottomSheetFragment = BottomAddDiaryFragment()
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }

        recyclerView = view.findViewById(R.id.diaryRecyclerView) // 리사이클러뷰 초기화

        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        diaryAdapter = DiaryAdapter(emptyList()) // 초기에 빈 목록으로 어댑터 설정
        recyclerView.adapter = diaryAdapter // 리사이클러뷰에 어댑터 설정

        // "MEMO" 항목 추가
        val diary1 = MyDiary(diaryId = 1, travelDest = "제주도",  title = "제주도 여행~!", travelStart = "2023-08-18", travelEnd = "2023-08-20",
        diaryLike = 5, comment = 4, createdAt = "2023-08-18", updatedAt = "2023-08-18", public = true)
        val diary2 = MyDiary(diaryId = 2, travelDest = "부산",  title = "부산 해운대 & 광안리", travelStart = "2023-09-05", travelEnd = "2023-09-08",
            diaryLike = 15, comment = 10, createdAt = "2023-08-18", updatedAt = "2023-08-18", public = true)
        val diary3 = MyDiary(diaryId = 3, travelDest = "강릉",  title = "강릉 바다", travelStart = "2023-05-20", travelEnd = "2023-05-21",
            diaryLike = 2, comment = 0, createdAt = "2023-08-18", updatedAt = "2023-08-18", public = true)
        val diary4 = MyDiary(diaryId = 4, travelDest = "일본",  title = "즐거웠던 일본 여행", travelStart = "2023-09-09", travelEnd = "2023-09-12",
            diaryLike = 25, comment = 5, createdAt = "2023-08-18", updatedAt = "2023-08-18", public = true)
        val diary5 = MyDiary(diaryId = 5, travelDest = "서울",  title = "서울 구경", travelStart = "2023-11-01", travelEnd = "2023-11-03",
            diaryLike = 2, comment = 5, createdAt = "2023-08-18", updatedAt = "2023-08-18", public = true)
        diaryList.add(diary1)
        diaryList.add(diary2)
        diaryList.add(diary3)
        diaryList.add(diary4)
        diaryList.add(diary5)
        diaryAdapter.updateData(diaryList)
    }


}