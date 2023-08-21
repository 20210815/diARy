package com.example.diary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diary.databinding.ActivityAddPlanBinding
import com.example.diary.databinding.ActivityPlanDetailBinding
import java.text.SimpleDateFormat
import java.util.*

class PlanDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlanDetailBinding
    private var planId = -1 //현재 플랜ID를 담는 변수
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    // 여행지 데이터를 저장할 리스트
    private val planPlaceList = mutableListOf<PlanDetailModel>()
    private val planDetailAdapter = PlanDetailAdapter(planPlaceList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  //툴바에 뒤로 가기 버튼 추가

        binding.planDetailRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@PlanDetailActivity)
            adapter = planDetailAdapter
        }

        planId = intent.getIntExtra("planId", -1)

        if (planId != -1) {
            // 플랜 아이디를 통해 서버에 데이터 요청
            PlanDetailManager.getPlanDetailData(
                planId,
                onSuccess = { planDetail ->
                    // 플랜 상세 정보를 UI에 적용하는 작업
                    binding.planDetailTitle.text = planDetail.plan.travelDest
                    binding.planDetailSubtitle.text = planDetail.plan.content

                    val tagNames = planDetail.tags.joinToString(" ") { "#${it.name}" }
                    binding.planDetailHash.text = tagNames

                    // 만약 작성한 유저와 현재 유저가 같다면, 수정하기/삭제하기 등등
                    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val formattedStartDate = dateFormatter.format(planDetail.plan.travelStart)
                    val formattedEndDate = dateFormatter.format(planDetail.plan.travelEnd)

                    binding.planDetailMyDate.text = "$formattedStartDate ~ $formattedEndDate"

                    val planDetailModels: List<PlanDetailModel> = planDetail.locations.map { locationDetail ->
                        val formattedStartTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(locationDetail.timeStart)
                        val formattedEndTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(locationDetail.timeEnd)

                        PlanDetailModel(
                            place = locationDetail.name,
                            address = locationDetail.address,
                            placeDate = locationDetail.date,
                            placeStart = formattedStartTime, // timeStart를 원하는 형식으로 변환
                            placeEnd = formattedEndTime    // timeEnd를 원하는 형식으로 변환
                        )
                    }

                    planDetailAdapter.updateData(planDetailModels)

                    //같지 않다면, 수정/삭제 gone, 정보레이아웃 visible 필요

                },
                onError = { throwable ->
                    Log.e("서버 테스트3", "오류: $throwable")
                }
            )
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.detail_admin_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { // 뒤로 가기 버튼 클릭 시
                finish() // 현재 액티비티 종료
                return true
            }
            R.id.mod_menu -> { //수정하기 버튼 클릭 시

                return true
            }
            R.id.remove_menu -> { //삭제하기 버튼 클릭 시
                DeletePlanManager.deleteDataFromServer(planId)
                finish() // 현재 액티비티 종료
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}