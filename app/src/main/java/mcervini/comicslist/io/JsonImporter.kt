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
        reader.beginArray()
        val series: MutableList<Series> = mutableListOf()
        while (reader.hasNext()) {
            series.add(readSeries(reader))
        }
        reader.endArray()
        reader.close()
        return series
    }


    private fun readSeries(reader: JsonReader): Series {
        var id: UUID? = null
        var name: String? = null
        val comicsData: MutableList<ComicData> = mutableListOf()

        val comics: MutableList<Comic> = mutableListOf()

        reader.beginObject()

        while (reader.hasNext()) {
            when (reader.nextName()) {
                "id" -> id = UUID.fromString(reader.nextString())
                "name" -> name = reader.nextString()
                "comics" -> {
                    reader.beginArray()
                    while (reader.hasNext()) {
                        comicsData.add(readComic(reader))
                    }
                    reader.endArray()
                }
            }
        }
        reader.endObject()

        if (id == null) {
            throw IllegalStateException("no id for series")
        }

        if (name.isNullOrBlank()) {
            throw IllegalStateException("no name for series")
        }

        val series = Series(id, name, comics)
        comics.addAll(comicsData.map { Comic(series, it.number, it.title, it.availability) })
        return series
    }

    private fun readComic(reader: JsonReader): ComicData {

        var number: Int? = null
        var title: String = ""
        var availability: Availability? = null
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "number" -> number = reader.nextInt()
                "title" -> title = reader.nextString()
                "availability" -> availability = Availability.fromValue(reader.nextInt())
            }
        }

        if (number == null) {
            throw IllegalStateException("no number for comic")
        }
        if (availability == null) {
            throw IllegalStateException("no availability for comic")
        }

        reader.endObject()
        return ComicData(number, title, availability)
    }
}