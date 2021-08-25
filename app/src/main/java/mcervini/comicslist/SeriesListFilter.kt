package mcervini.comicslist

import androidx.recyclerview.widget.SortedList
import java.util.*

class SeriesListFilter(
    private val list: MutableList<Series>,
    private val displayList: SortedList<Series>
) {
    private val activeFilters: MutableSet<(Series) -> Series?> = mutableSetOf()
    private val displayingSeriesMap: MutableMap<UUID, Series> = mutableMapOf()

    private var searchQuery = ""
    private var excludingAvailable: Boolean = false
    private var excludingNotAvailable: Boolean = false
    private var excludingBooked: Boolean = false
    private var filteringByName: Boolean = false

    fun excludeAvailable(exclude: Boolean) {
        if (exclude) {
            activeFilters.add(this::filterAvailable)
        } else {
            activeFilters.remove(this::filterAvailable)
        }
        if (exclude != excludingAvailable) {
            excludingAvailable = exclude
            updateList()
        }
    }

    fun excludeNotAvailable(exclude: Boolean) {
        if (exclude) {
            activeFilters.add(this::filterNotAvailable)
        } else {
            activeFilters.remove(this::filterNotAvailable)
        }
        if (exclude != excludingNotAvailable) {
            excludingNotAvailable = exclude
            updateList()
        }
    }

    fun excludeBooked(exclude: Boolean) {
        if (exclude) {
            activeFilters.add(this::filterBooked)
        } else {
            activeFilters.remove(this::filterBooked)
        }
        if (exclude != excludingBooked) {
            excludingBooked = exclude
            updateList()
        }
    }

    fun filterByName(name: String) {
        activeFilters.add(this::searchByName)
        if (searchQuery != name || !filteringByName) {
            filteringByName = true
            searchQuery = name
            updateList()
        }
    }

    fun clearNameFilter() {
        filteringByName = false
        searchQuery = ""
        activeFilters.remove(this::searchByName)
        updateList()
    }

    private fun updateList() {
        var temp: List<Series> = list
        displayList.clear()

        for (f in activeFilters) {
            temp = temp.mapNotNull(f)
        }

        displayList.apply {
            clear()
            addAll(temp)
            endBatchedUpdates()
        }
    }


    val isFiltering: Boolean
        get() = activeFilters.isNotEmpty()

    fun getSeries(id: UUID): Series {
        return displayingSeriesMap[id]!!
    }

    private fun filterAvailable(s: Series): Series? {
        return excludeByAvailability(s, Availability.AVAILABLE)
    }

    private fun filterNotAvailable(s: Series): Series? {
        return excludeByAvailability(s, Availability.NOT_AVAILABLE)
    }

    private fun filterBooked(s: Series): Series? {
        return excludeByAvailability(s, Availability.BOOKED)
    }

    private fun excludeByAvailability(series: Series, availability: Availability): Series? {
        val comics: List<Comic> =
            series.comics.mapNotNull { it.takeIf { it.availability != availability } }
        if (comics.isNotEmpty()) {
            return Series(
                series.id,
                series.name,
                comics.toMutableList()
            ).also { displayingSeriesMap[it.id] = series }
        }

        if (displayingSeriesMap.keys.contains(series.id)) {

            displayingSeriesMap.remove(series.id)
        }
        return null
    }

    private fun searchByName(s: Series): Series? {
        val seriesNameIncluded = s.name.contains(searchQuery)
        val comics: List<Comic> = s.comics.mapNotNull { c ->
            c.takeIf { c.title.contains(searchQuery) || (c.title.isBlank() && seriesNameIncluded) }
        }
        if (comics.isNotEmpty() || seriesNameIncluded) {
            return Series(s.id, s.name, comics.toMutableList()).also {
                displayingSeriesMap[it.id] = s
            }
        }

        if (displayingSeriesMap.keys.contains(s.id)) {
            displayingSeriesMap.remove(s.id)
        }
        return null
    }
}
