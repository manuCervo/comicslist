package mcervini.comicslist.io.backup

import android.content.ContentResolver
import android.net.Uri
import android.util.JsonWriter
import mcervini.comicslist.Comic
import mcervini.comicslist.Series
import mcervini.comicslist.io.backup.JsonKeys.Companion.COMIC_AVAILABILITY
import mcervini.comicslist.io.backup.JsonKeys.Companion.COMIC_NUMBER
import mcervini.comicslist.io.backup.JsonKeys.Companion.COMIC_TITLE
import mcervini.comicslist.io.backup.JsonKeys.Companion.SERIES_COMICS
import mcervini.comicslist.io.backup.JsonKeys.Companion.SERIES_ID
import mcervini.comicslist.io.backup.JsonKeys.Companion.SERIES_NAME
import java.io.OutputStreamWriter

/**
 * exports a list of series in a json file
 *
 * @param uri the uri of the destination file
 * @param contentResolver used for opening the file
 */
class JsonExporter(private val uri: Uri, private val contentResolver: ContentResolver) : Exporter {


    override fun export(series: List<Series>) {
        val writer =
            JsonWriter(OutputStreamWriter(contentResolver.openOutputStream(uri)))
        writer.run {
            beginArray()
            for (s in series) {
                writeSeries(this, s)
            }
            endArray()
            close()
        }
    }

    private fun writeSeries(writer: JsonWriter, series: Series) {
        writer.run {
            beginObject()
            name(SERIES_ID)
            value(series.id.toString())
            name(SERIES_NAME)
            value(series.name)
            name(SERIES_COMICS)
            beginArray()
            for (c in series.comics) {
                writeComic(this, c)
            }
            endArray()
            endObject()
        }
    }

    private fun writeComic(writer: JsonWriter, c: Comic) {
        writer.run {
            beginObject()
            name(COMIC_NUMBER)
            value(c.number)
            name(COMIC_TITLE)
            value(c.title)
            name(COMIC_AVAILABILITY)
            value(c.availability.value)
            endObject()
        }
    }
}