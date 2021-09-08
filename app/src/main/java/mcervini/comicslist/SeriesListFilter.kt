package mcervini.comicslist

import androidx.recyclerview.widget.SortedList
import java.util.*

/**
 * class for filtering a list of series based on their name and on the title and the availability of their comics.
 *
 * @param list the full list containing all of the series
 * @param displayList the list that will contain only the series that match the filters
 */
class SeriesListFilter(
    private val list: MutableList<Series>,
    private val displayList: SortedList<Series>
) {
    private val activeFilters: MutableSet<(Series) -> Series?> = mutableSetOf()

    //since the list of comics in the series can't be modified (because it has to remain the same of what is in the database),
    //a series can't be added directly to the displaying list, but a copy will be created.
    //this map is for getting a series from the corresponding "fake" one in the displaying list
    private val displayingSeriesMap: MutableMap<UUID, Series> = mutableMapOf()

    private var searchQuery = ""
    private var excludingAvailable: Boolean = false
    private var excludingNotAvailable: Boolean = false
    private var excludingBooked: Boolean = false
    private var filteringByName: Boolean = false

    /**
     * gets the corresponding series from the id of one that is currently shown
     */
    fun getSeries(id: UUID): Series {
        return displayingSeriesMap[id]!!
    }

    /**
     * enables or disables the exclusion of available comics
     */
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

    /**
     * enables or disables the exclusion of not available comics
     */
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

    /**
     * enables or disables the exclusion of not booked comics
     */
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

    /**
     * enables the filtering by the search query passed as argument
     * this filter will include in the result all the series that contain the search query or that have at least one comic with the title that includes the search query.
     * if a comic has no title, it will be included if its series's title contains the search query
     */
    fun filterByName(searchQuery: String) {
        activeFilters.add(this::searchByName)
        val search = searchQuery.trim().toLowerCase()
        if (this.searchQuery != search || !filteringByName) {
            filteringByName = true
            this.searchQuery = search
            updateList()
        }
    }

    /**
     * disables the filtering by name
     */
    fun clearNameFilter() {
        filteringByName = false
        searchQuery = ""
        activeFilters.remove(this::searchByName)
        updateList()
    }

    /**
     * applies all the active filters
     */
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

