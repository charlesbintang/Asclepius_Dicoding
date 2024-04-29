package com.dicoding.asclepius.view

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.DateHelper

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    private var historyEntity: HistoryEntity? = null
    private lateinit var resultViewModel: ResultViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        val dataResult = intent.getStringExtra(EXTRA_DISPLAY_TEXT)
        imageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.resultImage.setImageURI(it)
        }
        binding.resultText.text = dataResult

        resultViewModel = obtainViewModel(this@ResultActivity)
        historyEntity = HistoryEntity()
        if (imageUri != null && dataResult != null) {
            historyEntity.let { historyEntity ->
                historyEntity?.title = dataResult
                historyEntity?.image = imageUri.toString()
                historyEntity?.date = DateHelper.getCurrentDate()
            }
            resultViewModel.insert(historyEntity as HistoryEntity)
            Toast.makeText(
                this@ResultActivity,
                "Pengecekan telah selesai, data disimpan.",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun obtainViewModel(activity: AppCompatActivity): ResultViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[ResultViewModel::class.java]
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_DISPLAY_TEXT = "extra_display_text"
    }
}