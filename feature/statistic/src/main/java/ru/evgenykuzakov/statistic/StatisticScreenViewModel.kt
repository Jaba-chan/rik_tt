package ru.evgenykuzakov.statistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.evgenykuzakov.common.Resource
import ru.evgenykuzakov.domain.interactor.AgeSexStatisticInteractor
import ru.evgenykuzakov.domain.interactor.DateStatisticsInteractor
import ru.evgenykuzakov.domain.interactor.MostOftenVisitorsInteractor
import ru.evgenykuzakov.domain.interactor.VisitorsByTypeInteractor
import ru.evgenykuzakov.domain.model.ByAgeSexStatisticFilter
import ru.evgenykuzakov.domain.model.ByDateStatisticFilter
import ru.evgenykuzakov.domain.use_case.GetStatisticsUseCase
import ru.evgenykuzakov.domain.use_case.GetUsersUseCase
import java.time.LocalDate

class StatisticScreenViewModel(
    private val dateStatisticsInteractor: DateStatisticsInteractor,
    private val visitorsByTypeInteractor: VisitorsByTypeInteractor,
    private val mostOftenVisitorsInteractor: MostOftenVisitorsInteractor,
    private val ageSexStatisticInteractor: AgeSexStatisticInteractor,
    private val getUsersUseCase: GetUsersUseCase,
    private val getStatisticsUseCase: GetStatisticsUseCase,

    ) : ViewModel() {

    companion object {
        private val sept1_2024: LocalDate = LocalDate.of(2024, 9, 9)

    }
    private val _uiState = MutableStateFlow(StatisticUIState())
    val uiState: StateFlow<StatisticUIState> = _uiState

    private val usersFlow = getUsersUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, Resource.Loading())

    private val statisticsFlow = getStatisticsUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, Resource.Loading())

    init {
        loadVisitorsByDate()
        loadVisitorsByType()
        loadMostOftenVisitors()
        loadSexAgeStatistic()
    }

    private fun loadVisitorsByDate(nowDate: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            dateStatisticsInteractor(sept1_2024, _uiState.value.dateFilter)
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
            ageSexStatisticInteractor(sept1_2024, _uiState.value.ageSexFilter)
                .collect { result ->
                    _uiState.update { state ->
                        state.copy(ageSexStatistic = result)
                    }
                }
        }
    }

    fun onLineChartFilterChanged(newFilter: Int) {
        _uiState.update {
            it.copy(
                dateFilter = ByDateStatisticFilter.entries[newFilter]
            )
        }
        loadVisitorsByDate()
    }

    fun onCircleChartFilterChanged(newFilter: Int) {
        _uiState.update {
            it.copy(
                ageSexFilter = ByAgeSexStatisticFilter.entries[newFilter]
            )
        }
        loadSexAgeStatistic()
    }
}