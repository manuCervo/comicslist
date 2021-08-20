package mcervini.comicslist.io

import android.util.JsonWriter
import mcervini.comicslist.Comic
import mcervini.comicslist.Series
import java.io.OutputStream
import java.io.OutputStreamWriter

class JsonExporter(private val stream: OutputStream) {


    fun export(series: MutableList<Series>) {
        val writer: JsonWriter = JsonWriter(OutputStreamWriter(stream))
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
            name("id")
            value(series.id.toString())
            name("name")
            value(series.name)
            name("comics")
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
            name("number")
            value(c.number)
            name("title")
            value(c.title)
            name("availability")
            value(c.availability.value)
            endObject()
        }
    }


}