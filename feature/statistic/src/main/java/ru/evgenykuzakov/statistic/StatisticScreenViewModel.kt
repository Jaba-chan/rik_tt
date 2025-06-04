package ru.evgenykuzakov.statistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import ru.evgenykuzakov.common.Resource
import ru.evgenykuzakov.domain.interactor.StatisticInteractor
import ru.evgenykuzakov.domain.model.StatisticUIState

class StatisticScreenViewModel(
    private val statisticInteractor: StatisticInteractor
): ViewModel() {
    val uiState: StateFlow<Resource<StatisticUIState>> =
        statisticInteractor()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Resource.Loading()
            )
}