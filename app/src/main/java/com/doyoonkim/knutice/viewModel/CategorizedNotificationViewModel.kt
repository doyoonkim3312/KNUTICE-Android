package com.doyoonkim.knutice.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.doyoonkim.knutice.domain.CrawlFullContentImpl
import com.doyoonkim.knutice.domain.FetchTopThreeNoticeByCategory
import com.doyoonkim.knutice.model.Notice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategorizedNotificationViewModel @Inject constructor(
    private val fetchTopThreeNoticeUseCase: FetchTopThreeNoticeByCategory,
    private val crawlFullContentUseCase: CrawlFullContentImpl
) : ViewModel() {
    init {
        CoroutineScope(Dispatchers.IO).launch {
            fetchTopThreeGeneralNotice()
            fetchTopThreeAcademicNotice()
            fetchTopThreeScholarshipNotice()
            fetchTopThreeEventNotice()
        }
    }

    private val fileName = "CategorizedNotificationViewModel"
    private val _uiState = MutableStateFlow(CategorizedNotificationState())
    var uiState: StateFlow<CategorizedNotificationState> = _uiState.asStateFlow()

    fun updateState (
        updatedNotificationGeneral: List<Notice> = _uiState.value.notificationGeneral,
        updatedNotificationAcademic: List<Notice> = _uiState.value.notificationAcademic,
        updatedNotificationScholarship: List<Notice> = _uiState.value.notificationScholarship,
        updatedNotificationEvent: List<Notice> = _uiState.value.notificationEvent
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            _uiState.update {
                it.copy(
                    notificationGeneral = updatedNotificationGeneral,
                    notificationAcademic = updatedNotificationAcademic,
                    notificationScholarship = updatedNotificationScholarship,
                    notificationEvent = updatedNotificationEvent
                )
            }
        }
    }

    private suspend fun fetchTopThreeGeneralNotice() {
        fetchTopThreeNoticeUseCase.fetchTopThreeGeneralNotice()
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
            .collectLatest { result ->
                result.fold(
                    onSuccess = {
                        updateState(
                            updatedNotificationGeneral = listOf(
                                it.notice1!!, it.notice2!!, it.notice3!!
                            )
                        )
                    },
                    onFailure = {
                        Log.d(fileName, "Retrofit2: Failure: ${it.toString()}")
                    }
                )
            }
    }

    private suspend fun fetchTopThreeAcademicNotice() {
        fetchTopThreeNoticeUseCase.fetchTopThreeAcademicNotice()
            .map{ Result.success(it) }
            .catch { emit(Result.failure(it)) }
            .collectLatest { result ->
                result.fold(
                    onSuccess = {
                        updateState(
                            updatedNotificationAcademic = listOf(
                                it.notice1!!, it.notice2!!, it.notice3!!
                            )
                        )
                    },
                    onFailure = {
                        Log.d(fileName, "Retrofit2: Failure: ${it.toString()}")
                    }
                )
            }
    }

    private suspend fun fetchTopThreeScholarshipNotice() {
        fetchTopThreeNoticeUseCase.fetchTopThreeScholarshipNotice()
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
            .collectLatest { result ->
                result.fold(
                    onSuccess = {
                        updateState(
                            updatedNotificationScholarship = listOf(
                                it.notice1!!, it.notice2!!, it.notice3!!
                            )
                        )
                    },
                    onFailure = {
                        Log.d(fileName, "Retrofit2: Failure: ${it.toString()}")
                    }
                )
            }
    }

    private suspend fun fetchTopThreeEventNotice() {
        fetchTopThreeNoticeUseCase.fetchTopThreeEventNotice()
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
            .collectLatest { result ->
                result.fold(
                    onSuccess = {
                        updateState(
                            updatedNotificationEvent = listOf(
                                it.notice1!!, it.notice2!!, it.notice3!!
                            )
                        )
                    },
                    onFailure = {
                        Log.d(fileName, "Retrofit2: Failure: ${it.toString()}")
                    }
                )
            }
    }
}

data class CategorizedNotificationState(
    val notificationGeneral: List<Notice> = listOf(Notice(), Notice(), Notice()),
    val notificationAcademic: List<Notice> = listOf(Notice(), Notice(), Notice()),
    val notificationScholarship: List<Notice> = listOf(Notice(), Notice(), Notice()),
    val notificationEvent: List<Notice> = listOf(Notice(), Notice(), Notice())
)