package mcervini.comicslist.io.backup

import mcervini.comicslist.Series

interface Importer {
    fun import(): List<Series>

    class MissingDataException : Exception()
}