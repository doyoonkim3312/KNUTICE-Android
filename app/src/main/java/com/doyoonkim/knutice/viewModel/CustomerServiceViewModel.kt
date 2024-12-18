package com.doyoonkim.knutice.viewModel

import androidx.lifecycle.ViewModel
import com.doyoonkim.knutice.model.CustomerServiceReportState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CustomerServiceViewModel @Inject constructor(

) : ViewModel() {

    private var _uiState = MutableStateFlow(CustomerServiceReportState())
    val uiState = _uiState.asStateFlow()

    fun updateUserReportContent(content: String) {
        if (!_uiState.value.reachedMaCharacters) {
            _uiState.update {
                it.copy(
                    userReport = content,
                    reachedMaCharacters = content.length >= 500
                )
            }
        }
    }

}