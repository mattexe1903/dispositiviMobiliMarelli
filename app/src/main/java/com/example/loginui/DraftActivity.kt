package com.example.loginui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.loginui.databinding.DraftActivityBinding

class DraftActivity : AppCompatActivity() {
    private lateinit var binding: DraftActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DraftActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}