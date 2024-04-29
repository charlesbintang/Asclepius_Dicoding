package com.dicoding.asclepius.view

import android.app.Application
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.HistoryRepository
import com.dicoding.asclepius.data.local.entity.HistoryEntity

class ResultViewModel(application: Application) : ViewModel() {
    private val mHistoryRepository: HistoryRepository = HistoryRepository(application)

    fun insert(historyEntity: HistoryEntity) {
        mHistoryRepository.insert(historyEntity)
    }
}