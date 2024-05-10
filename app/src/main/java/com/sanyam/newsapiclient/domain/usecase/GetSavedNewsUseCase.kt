package com.sanyam.newsapiclient.domain.usecase

import com.sanyam.newsapiclient.data.model.Article
import com.sanyam.newsapiclient.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow

class GetSavedNewsUseCase(private val newsRepository: NewsRepository) {
    fun execute(): Flow<List<Article>>{
        return newsRepository.getSavedNews()
    }
}