package mcervini.comicslist.io.backup

import mcervini.comicslist.Series

/**
 * interface for importing a list of series
 */
interface Importer {
    /**
     * imports a list of series
     */
    fun import(): List<Series>

    class MissingDataException : Exception()
}