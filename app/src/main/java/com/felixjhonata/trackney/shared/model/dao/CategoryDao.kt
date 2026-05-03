package com.felixjhonata.trackney.shared.model.dao

import androidx.room.Dao
import androidx.room.Query
import com.felixjhonata.trackney.shared.model.entity.Category

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    suspend fun getAll(): List<Category>
}