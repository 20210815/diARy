package com.example.diary

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlanAdapter(private var plans: List<MyPlan>) : RecyclerView.Adapter<PlanAdapter.PlanViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.plan_recyclerview, parent, false)
        return PlanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val plan = plans[position]
        holder.bind(plan)
    }

    override fun getItemCount(): Int {
        return plans.size
    }

    // 데이터 업데이트 메서드 추가
    fun updateData(newPlans: List<MyPlan>) {
        plans = newPlans
        notifyDataSetChanged()
    }

    inner class PlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.plan_title)
        private val periodTextView: TextView = itemView.findViewById(R.id.plan_period)
        private val planLikeView: TextView = itemView.findViewById(R.id.plan_like)
        private val planLikeImeView: TextView = itemView.findViewById(R.id.plan_like_img)

        init {
            itemView.setOnClickListener {
                val clickedPlan = plans[adapterPosition]
                val planId = clickedPlan.planId // 클릭된 플랜의 planId를 가져옴
                val intent = Intent(itemView.context, PlanDetailActivity::class.java)
                intent.putExtra("planId", planId)
                itemView.context.startActivity(intent)
            }
        }

        fun bind(plan: MyPlan) {
            titleTextView.text = plan.travelDest
            periodTextView.text = "${plan.travelStart} ~ ${plan.travelEnd}"

            //일정 비공개라면
            if (plan.public == false) {
                planLikeImeView.visibility = View.GONE
                planLikeView.text = "비공개"
            } else {
                planLikeImeView.visibility = View.VISIBLE
                planLikeView.text = "1" //일정 좋아요 수 (수정 필요)
            }
        }
    }
}