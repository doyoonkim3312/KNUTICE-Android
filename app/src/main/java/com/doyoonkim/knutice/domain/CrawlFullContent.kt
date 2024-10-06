package com.doyoonkim.knutice.domain

import com.doyoonkim.knutice.viewModel.DetailedContentState
import kotlinx.coroutines.flow.Flow

interface CrawlFullContent {

    fun getFullContentFromSource(title: String, info: String, url: String): Flow<DetailedContentState>

}