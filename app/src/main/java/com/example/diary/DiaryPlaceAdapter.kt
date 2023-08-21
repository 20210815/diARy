package com.example.diary

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diary.databinding.DiaryDetailPlaceRecyclerviewBinding

class DiaryPlaceAdapter (private val itemList: MutableList<DiaryPlaceModel>) :
    RecyclerView.Adapter<DiaryPlaceAdapter.ViewHolder>() {

    companion object {
        private const val ITEM_TYPE_NORMAL = 0
        private const val ITEM_TYPE_MEMO = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DiaryDetailPlaceRecyclerviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size

    inner class ViewHolder(private val binding: DiaryDetailPlaceRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.diaryCardView.setOnClickListener {


                val item = itemList[adapterPosition] // 클릭된 아이템
                val intent = Intent(itemView.context, AddPlaceInDiaryActivity::class.java)
                Log.d("어댑터", ""+adapterPosition)
                intent.putExtra("itemPosition", adapterPosition) // position 전달
                intent.putExtra("place", item.place)
                intent.putExtra("date", item.placeDate)
                intent.putExtra("timeS", item.placeTimeS)
                intent.putExtra("timeE", item.placeTimeE)
                intent.putExtra("content", item.content)
                intent.putParcelableArrayListExtra("imageUris", item.imageUris)
                //itemView.context.startActivity(intent)

                Log.d("mylog", "여행지 정보 in 지도" + adapterPosition + item.place + item.placeDate
                        + item.placeTimeS +item.placeTimeE)
                AddDiaryActivity.addContentActivityResult.launch(intent)
            }
        }

        fun bind(item: DiaryPlaceModel) {
            // 데이터를 레이아웃의 뷰에 바인딩
            //Log.d(TAG, "Item content: ${item.content}")
            if(item.place != null) {
                binding.diaryDetailPlace.text = item.place
                binding.diaryPlaceDate.text = item.placeDate
                binding.diaryPlaceStartTime.text = item.placeTimeS
                binding.diaryPlaceEndTime.text = item.placeTimeE
            }

            binding.placeContent.text = item.content ?: "클릭하여 여행지별 일기를 기록하세요."

            // 이미지 리사이클러뷰 초기화 및 어댑터 연결
            val imageRecyclerView = binding.recyclerView
            imageRecyclerView.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, true)

            // 이미지 어댑터 초기화 및 연결
            val imageAdapter = MultiImageAdapter(item.imageUris ?: ArrayList(), binding.root.context)
            imageRecyclerView.adapter = imageAdapter
        }
    }

    // 특정 아이템을 맨 마지막으로 이동시키는 함수
    fun moveMemoItemToLast() {
        val memoItem = itemList.find { it.place == "MEMO" }
        memoItem?.let {
            itemList.remove(it)
            itemList.add(it)
            notifyDataSetChanged()
        }
    }

    override fun getItemViewType(position: Int): Int {
        if ("MEMO" == itemList[position].place) {
            return ITEM_TYPE_MEMO
        } else {
            return ITEM_TYPE_NORMAL
        }
    }

}