package com.felixjhonata.trackney.shared.model.repository

import com.felixjhonata.trackney.shared.model.dao.CategoryDao
import com.felixjhonata.trackney.shared.model.entity.Category
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    suspend fun getCategories() = categoryDao.getAll()

    fun getCategoriesAsFlow(): Flow<List<Category>> = categoryDao.getAllAsFlow()

    suspend fun insertCategory(category: Category) {
        val nextId = (categoryDao.getMaxId() ?: 0) + 1
        categoryDao.insert(category.copy(id = nextId))
    }

    suspend fun updateCategory(category: Category) {
        categoryDao.update(category)
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.delete(category)
    }
}