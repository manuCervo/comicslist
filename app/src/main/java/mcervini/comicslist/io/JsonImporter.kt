package mcervini.comicslist.io

import android.util.JsonReader
import mcervini.comicslist.Availability
import mcervini.comicslist.Comic
import mcervini.comicslist.Series
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

class JsonImporter(private val stream: InputStream) {
    private class ComicData(val number: Int, val title: String, val availability: Availability)

    fun import(): MutableList<Series> {
        val reader: JsonReader = JsonReader(InputStreamReader(stream))
        val series: MutableList<Series> = mutableListOf()

        return reader.run {
            beginArray()
            while (hasNext()) {
                series.add(readSeries(this))
            }
            endArray()
            close()
            series
        }
    }


    private fun readSeries(reader: JsonReader): Series {
        var id: UUID? = null
        var name: String? = null
        val comicsData: MutableList<ComicData> = mutableListOf()

        reader.run {
            beginObject()
            while (hasNext()) {
                when (nextName()) {
                    "id" -> id = UUID.fromString(nextString())
                    "name" -> name = nextString().takeUnless { it.isNullOrBlank() }
                    "comics" -> {
                        beginArray()
                        while (hasNext()) {
                            comicsData.add(readComic(this))
                        }
                        endArray()
                    }
                }
            }
            endObject()
        }

        return Series(
            id ?: throw IllegalStateException("no id for series"),
            name ?: throw IllegalStateException("no name for series")
        ).apply {
            comics.addAll(comicsData.map { Comic(this, it.number, it.title, it.availability) })
        }
    }

    private fun readComic(reader: JsonReader): ComicData {

        var number: Int? = null
        var title: String = ""
        var availability: Availability? = null
        return reader.run {

            beginObject()
            while (hasNext()) {
                when (nextName()) {
                    "number" -> number = nextInt()
                    "title" -> title = nextString()
                    "availability" -> availability = Availability.fromValue(nextInt())
                }
            }

            endObject()
            ComicData(
                number ?: throw IllegalStateException("no number for comic"),
                title,
                availability ?: throw IllegalStateException("no availability for comic")
            )
        }
    }
}