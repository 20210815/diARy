package com.example.diary

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.diary.databinding.PlanDetailRecyclerviewBinding

class PlanDetailViewHolder(val binding: PlanDetailRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root)

class PlanDetailAdapter(private val itemList: MutableList<PlanDetailModel>): RecyclerView.Adapter<PlanDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanDetailViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PlanDetailViewHolder(PlanDetailRecyclerviewBinding.inflate(layoutInflater))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: PlanDetailViewHolder, position: Int) {
        val data = itemList.get(position)

        holder.binding.run {
            placeTime.text = "${data.placeStart} ~ ${data.placeEnd}"
            placeName.text = data.place
        }

        //이미지 불러오기 추가 필요

    }

    fun updateData(newItems: List<PlanDetailModel>) {
        itemList.clear()
        itemList.addAll(newItems)
        notifyDataSetChanged()
    }
}