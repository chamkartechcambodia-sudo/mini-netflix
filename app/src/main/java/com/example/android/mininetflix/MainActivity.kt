package com.example.android.mininetflix

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android.mininetflix.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // MainActivity only hosts the navigation graph. Each screen is a Fragment.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
