package com.sanyam.newsapiclient.domain.usecase

import com.sanyam.newsapiclient.data.model.Article
import com.sanyam.newsapiclient.domain.repository.NewsRepository

class SaveNewsUseCase(private val newsRepository: NewsRepository) {
  suspend fun execute(article: Article)=newsRepository.saveNews(article)
}