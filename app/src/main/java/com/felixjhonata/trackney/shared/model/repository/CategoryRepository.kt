package com.felixjhonata.trackney.shared.model.repository

import com.felixjhonata.trackney.shared.model.dao.CategoryDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    suspend fun getCategories() = categoryDao.getAll()
}