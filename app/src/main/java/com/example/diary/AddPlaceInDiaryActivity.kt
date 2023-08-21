package com.example.diary

import android.app.Activity
import android.content.ClipData
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diary.databinding.ActivityAddPlaceInDiaryBinding

class AddPlaceInDiaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPlaceInDiaryBinding
    private var uriList = ArrayList<Uri>()
    private lateinit var adapter: MultiImageAdapter

    // 여러 이미지 선택을 위한 ActivityResultLauncher
    private val multipleImagePicker = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri>? ->
        if (uris != null) {
            val totalSelectedImages = uriList.size + uris.size
            if (totalSelectedImages > 10) {
                Toast.makeText(applicationContext, "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG).show()
            } else {
                uriList.addAll(0, uris)
                adapter = MultiImageAdapter(uriList, applicationContext)
                binding.recyclerView.adapter = adapter
            }
        } else {
            Toast.makeText(applicationContext, "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlaceInDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  //툴바에 뒤로 가기 버튼 추가

        val itemPosition = intent.getIntExtra("itemPosition", -1)
        val place = intent.getStringExtra("place")
        val placeDate = intent.getStringExtra("date")
        val placeTimeS = intent.getStringExtra("timeStart")
        val placeTimeE = intent.getStringExtra("timeEnd")
        var content = intent.getStringExtra("content")

        Log.d("여행지추가", ""+itemPosition+place+content)

        uriList = intent.getParcelableArrayListExtra<Uri>("imageUris")?: ArrayList()

        if (place == "MEMO") {
            binding.placeImgAddBtn.visibility = View.GONE
            binding.placeInDiaryTime.visibility = View.GONE
        }

        if (content == "클릭하여 여행지별 일기를 기록하세요." || content == "클릭하여 메모를 작성하세요.") {
            content = null
        }

        val placeTime = "$placeDate $placeTimeS ~ $placeTimeE"

        // 여행지 정보를 텍스트뷰에 표시
        binding.placeInDiaryTitle.setText(place)
        binding.placeInDiaryTime.setText(placeTime)
        binding.placeInDiaryContent.setText(content)

        // 이미지 리사이클러뷰 초기화 및 어댑터 연결
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val imageAdapter = MultiImageAdapter(uriList ?: ArrayList(), this)
        binding.recyclerView.adapter = imageAdapter

        binding.placeImgAddBtn.setOnClickListener {
            // 이미지 선택을 위해 ActivityResultLauncher 실행
            val remainingImages = 10 - uriList.size
            if (remainingImages > 0) {
                // 최대 10장 이하의 이미지만 선택 가능하도록 합니다.
                multipleImagePicker.launch("image/*")
            } else {
                Toast.makeText(applicationContext, "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG).show()
            }
        }

        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true)

        // 완료 버튼 클릭 시
        binding.placeInDiarySaveBtn.setOnClickListener {
            val enteredText = binding.placeInDiaryContent.text.toString() //일기 내용 저장

            // 데이터를 이전 활동으로 전달하기 위한 인텐트 생성
            val intent = Intent()
            //intent.putExtra("position", position)  // 수정 중인 아이템의 위치 정보 전달
            intent.putExtra("itemPosition", itemPosition) // position 전달
            intent.putExtra("enteredText", enteredText)
            intent.putExtra("place", place)
            intent.putExtra("date", placeDate)
            intent.putExtra("timeStart", placeTimeS)
            intent.putExtra("timeEnd", placeTimeE)
            intent.putParcelableArrayListExtra("imageUris", uriList)

            Log.d("mylog", "AddPlaceInDiary에서 완료 클릭" + itemPosition + title + placeDate + placeTimeS +placeTimeE)

            // 결과를 설정하고 현재 활동 종료
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

}