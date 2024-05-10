package com.sanyam.newsapiclient.domain.usecase

import com.sanyam.newsapiclient.data.model.APIResponse
import com.sanyam.newsapiclient.data.util.Resource
import com.sanyam.newsapiclient.domain.repository.NewsRepository

class GetSearchedNewsUseCase(private val newsRepository: NewsRepository) {
     suspend fun execute(country:String,searchQuery:String,page:Int): Resource<APIResponse>{
         return newsRepository.getSearchedNews(country,searchQuery,page)
     }
}