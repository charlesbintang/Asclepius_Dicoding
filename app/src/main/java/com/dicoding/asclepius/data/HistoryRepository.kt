package com.dicoding.asclepius.data

import android.app.Application
import androidx.lifecycle.LiveData
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.data.local.room.HistoryDao
import com.dicoding.asclepius.data.local.room.HistoryDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HistoryRepository(application: Application) {
    private val mHistoryDao: HistoryDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = HistoryDatabase.getDatabase(application)
        mHistoryDao = db.historyDao()
    }

    fun getAllHistory(): LiveData<List<HistoryEntity>> = mHistoryDao.getAllHistory()

    fun insert(historyEntity: HistoryEntity) {
        executorService.execute{ mHistoryDao.insert(historyEntity) }
    }
}