package com.dicoding.asclepius.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        frameFragment(MainFragment())
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.image_menu -> frameFragment(MainFragment())
                R.id.history_menu -> frameFragment(HistoryFragment())
                else -> {}
            }
            true
        }
    }

    private fun frameFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val frameTransaction = fragmentManager.beginTransaction()
        frameTransaction.replace(R.id.frame_container, fragment)
        frameTransaction.commit()
    }
}