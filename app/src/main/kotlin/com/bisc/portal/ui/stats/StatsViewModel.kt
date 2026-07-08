package com.bisc.portal.ui.stats

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bisc.portal.data.repository.PortalRepository
import com.bisc.portal.ui.home.HomePrefKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class TileStatEntry(
    val tileId: Long,
    val label: String,
    val url: String,
    val count: Int,
    val lastClicked: Long
)

data class PortalStats(
    val totalClicks: Int,
    val tiles: List<TileStatEntry>,
    val hourlyGlobal: IntArray
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repo: PortalRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    val statsEnabled = dataStore.data.map { it[HomePrefKeys.STATS_ENABLED] ?: false }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val stats = repo.allClicks
        .map { clicks ->
            if (clicks.isEmpty()) return@map PortalStats(0, emptyList(), IntArray(24))
            val hourly = IntArray(24)
            val cal = Calendar.getInstance()
            clicks.forEach { click ->
                cal.timeInMillis = click.timestamp
                hourly[cal.get(Calendar.HOUR_OF_DAY)]++
            }
            val tiles = clicks
                .groupBy { it.tileId }
                .map { (tileId, tileClicks) ->
                    val latest = tileClicks.maxBy { it.timestamp }
                    TileStatEntry(
                        tileId = tileId,
                        label = latest.label.ifBlank { latest.url },
                        url = latest.url,
                        count = tileClicks.size,
                        lastClicked = latest.timestamp
                    )
                }
                .sortedByDescending { it.count }
            PortalStats(totalClicks = clicks.size, tiles = tiles, hourlyGlobal = hourly)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, PortalStats(0, emptyList(), IntArray(24)))

    fun setStatsEnabled(on: Boolean) = viewModelScope.launch {
        dataStore.edit { it[HomePrefKeys.STATS_ENABLED] = on }
    }

    fun clearStats() = viewModelScope.launch { repo.clearAllClicks() }
}
