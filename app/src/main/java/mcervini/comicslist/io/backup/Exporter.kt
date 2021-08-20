package mcervini.comicslist.io.backup

import mcervini.comicslist.Series

interface Exporter {
    fun export(series: List<Series>)
}