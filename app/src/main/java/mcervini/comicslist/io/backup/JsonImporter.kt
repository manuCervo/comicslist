package mcervini.comicslist.io.backup

import android.content.ContentResolver
import android.net.Uri
import android.util.JsonReader
import mcervini.comicslist.Availability
import mcervini.comicslist.Comic
import mcervini.comicslist.Series
import mcervini.comicslist.io.backup.JsonKeys.Companion.COMIC_AVAILABILITY
import mcervini.comicslist.io.backup.JsonKeys.Companion.COMIC_NUMBER
import mcervini.comicslist.io.backup.JsonKeys.Companion.COMIC_TITLE
import mcervini.comicslist.io.backup.JsonKeys.Companion.SERIES_COMICS
import mcervini.comicslist.io.backup.JsonKeys.Companion.SERIES_ID
import mcervini.comicslist.io.backup.JsonKeys.Companion.SERIES_NAME
import java.io.InputStreamReader
import java.util.*

class JsonImporter(private val uri: Uri, private val contentResolver: ContentResolver) : Importer {
    private class ComicData(val number: Int, val title: String, val availability: Availability)


    override fun import(): List<Series> {
        val reader: JsonReader = JsonReader(InputStreamReader(contentResolver.openInputStream(uri)))
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
                    SERIES_ID -> id = UUID.fromString(nextString())
                    SERIES_NAME -> name = nextString().takeUnless { it.isNullOrBlank() }
                    SERIES_COMICS -> {
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
                    COMIC_NUMBER -> number = nextInt()
                    COMIC_TITLE -> title = nextString()
                    COMIC_AVAILABILITY -> availability = Availability.fromValue(nextInt())
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