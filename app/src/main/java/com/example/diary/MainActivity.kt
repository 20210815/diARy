package com.example.diary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils.replace
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.diary.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.internal.NavigationMenuItemView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //toolbar
        setSupportActionBar(binding.toolbar)

        //navigator
        binding.navView.setNavigationItemSelectedListener(this)

        //bottom navigator
        binding.bottomMenu.setOnItemSelectedListener {item ->
            changeFragment(
                when (item.itemId) {
                    R.id.home -> HomeFragment()
                    R.id.map -> MapFragment()
                    R.id.plan -> PlanFragment()
                    R.id.ranking -> RankingFragment()
                    else -> DiaryFragment()
                }
            )
            true
        }

        // 초기화면으로 HomeFragment를 보여줄 수 있도록 설정
        changeFragment(HomeFragment())
    }
    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container,fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)  //menu_actionbar 불러 와서 사용할 것임, 연결 (menu 와)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.toolbar_nav) { // 툴바의 toolbar_nav 버튼 클릭 시
            if (binding.drawer.isDrawerOpen(GravityCompat.END)) {
                binding.drawer.closeDrawer(GravityCompat.END) // 드로어 레이아웃 닫기
            } else {
                binding.drawer.openDrawer(GravityCompat.END) // 드로어 레이아웃 열기
            }
            return true
        }

        if (item.itemId == R.id.toolbar_auth) {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_1 -> {}
            R.id.menu_2 -> {}
            R.id.menu_3 -> {}
        }
        return true
    }
}