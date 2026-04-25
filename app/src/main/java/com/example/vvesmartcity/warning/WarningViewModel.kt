package com.example.vvesmartcity.warning

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class WarningState(
    val warnings: List<WarningRecord> = emptyList(),
    val searchQuery: String = ""
)

class WarningViewModel : ViewModel() {
    private val _state = MutableStateFlow(WarningState(
        warnings = WarningDataSource.getAllWarnings()
    ))
    val state: StateFlow<WarningState> = _state.asStateFlow()

    val currentWarnings: List<WarningRecord>
        get() = WarningDataSource.getCurrentWarnings()

    fun searchWarnings(query: String) {
        _state.update { it.copy(
            warnings = WarningDataSource.searchWarnings(query),
            searchQuery = query
        ) }
    }

    fun addWarning(warning: WarningRecord) {
        WarningDataSource.addWarning(warning)
        refreshWarnings()
    }

    fun updateWarning(warning: WarningRecord) {
        WarningDataSource.updateWarning(warning)
        refreshWarnings()
    }

    fun deleteWarning(id: String) {
        WarningDataSource.deleteWarning(id)
        refreshWarnings()
    }

    fun markAsHandled(id: String) {
        WarningDataSource.markAsHandled(id)
        refreshWarnings()
    }

    private fun refreshWarnings() {
        _state.update { it.copy(
            warnings = WarningDataSource.searchWarnings(_state.value.searchQuery)
        ) }
    }
}
