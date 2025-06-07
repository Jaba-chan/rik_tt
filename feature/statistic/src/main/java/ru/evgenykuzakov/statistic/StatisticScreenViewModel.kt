package ru.evgenykuzakov.statistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.evgenykuzakov.domain.interactor.AgeSexStatisticInteractor
import ru.evgenykuzakov.domain.interactor.DateStatisticsInteractor
import ru.evgenykuzakov.domain.interactor.MostOftenVisitorsInteractor
import ru.evgenykuzakov.domain.interactor.VisitorsByTypeInteractor
import ru.evgenykuzakov.domain.model.ByAgeSexStatisticFilter
import ru.evgenykuzakov.domain.model.ByDateStatisticFilter
import ru.evgenykuzakov.domain.model.StatisticUIState
import java.time.LocalDate

class StatisticScreenViewModel(
    private val dateStatisticsInteractor: DateStatisticsInteractor,
    private val visitorsByTypeInteractor: VisitorsByTypeInteractor,
    private val mostOftenVisitorsInteractor: MostOftenVisitorsInteractor,
    private val ageSexStatisticInteractor: AgeSexStatisticInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticUIState())
    val uiState: StateFlow<StatisticUIState> = _uiState

    init {
        loadVisitorsByDate()
        loadVisitorsByType()
        loadMostOftenVisitors()
        loadSexAgeStatistic()
    }

    private fun loadVisitorsByDate(nowDate: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            dateStatisticsInteractor(nowDate, _uiState.value.dateFilter)
                .collect { result ->
                    _uiState.update { state ->
                        state.copy(dateStatistic = result)
                    }
                }
        }
    }

    private fun loadVisitorsByType() {
        viewModelScope.launch {
            visitorsByTypeInteractor()
                .collect { result ->
                    _uiState.update { state ->
                        state.copy(visitorsByType = result)
                    }
                }
        }
    }

    private fun loadMostOftenVisitors() {
        viewModelScope.launch {
            mostOftenVisitorsInteractor()
                .collect { result ->
                    _uiState.update { state ->
                        state.copy(mostOftenVisitors = result)
                    }
                }
        }
    }

    private fun loadSexAgeStatistic(nowDate: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            ageSexStatisticInteractor(nowDate, _uiState.value.ageSexFilter)
                .collect { result ->
                    _uiState.update { state ->
                        state.copy(ageSexStatistic = result)
                    }
                }
        }
    }

    fun onDateFilterChanged(newFilter: ByDateStatisticFilter) {
        _uiState.update { it.copy(dateFilter = newFilter) }
        loadVisitorsByDate()
    }

    fun onAgeSexFilterChanged(newFilter: ByAgeSexStatisticFilter) {
        _uiState.update { it.copy(ageSexFilter = newFilter) }
        loadSexAgeStatistic()
    }
}