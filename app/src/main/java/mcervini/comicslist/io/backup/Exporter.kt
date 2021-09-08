package mcervini.comicslist.io.backup

import mcervini.comicslist.Series

/**
 * interface for exporting a list of series
 */
interface Exporter {
    /**
     * exports the a list of series
     */
    fun export(series: List<Series>)
}