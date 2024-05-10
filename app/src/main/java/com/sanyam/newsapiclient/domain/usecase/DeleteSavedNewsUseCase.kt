package com.sanyam.newsapiclient.domain.usecase

import com.sanyam.newsapiclient.data.model.Article
import com.sanyam.newsapiclient.domain.repository.NewsRepository

class DeleteSavedNewsUseCase(private val newsRepository: NewsRepository) {
    suspend fun execute(article: Article)=newsRepository.deleteNews(article)
}