package com.felixjhonata.trackney.shared.model.repository

import com.felixjhonata.trackney.shared.model.TransactionType
import com.felixjhonata.trackney.shared.model.dao.CategoryDao
import com.felixjhonata.trackney.shared.model.entity.Category
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    fun getCategories() = categoryDao.getAll()
    suspend fun getAllCategoriesList() = categoryDao.getAllList()
    suspend fun existsByName(name: String, type: TransactionType) = categoryDao.existsByName(name, type)
    suspend fun insertCategory(category: Category) = categoryDao.insertCategory(category)
    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)
    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)
}