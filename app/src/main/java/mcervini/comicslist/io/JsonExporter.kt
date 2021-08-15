package mcervini.comicslist.io

import android.util.JsonWriter
import mcervini.comicslist.Comic
import mcervini.comicslist.Series
import java.io.OutputStream
import java.io.OutputStreamWriter

class JsonExporter(private val stream: OutputStream) {


    fun export(series: MutableList<Series>) {
        val writer: JsonWriter = JsonWriter(OutputStreamWriter(stream))

        writer.beginArray()
        for (s in series) {
            writeSeries(writer, s)
        }
        writer.endArray()
        writer.close()
    }

    private fun writeSeries(writer: JsonWriter, series: Series) {
        writer.beginObject()
        writer.name("id")
        writer.value(series.id.toString())
        writer.name("name")
        writer.value(series.name)
        writer.name("comics")
        writer.beginArray()
        for (c in series.comics) {
            writeComic(writer, c)
        }
        writer.endArray()
        writer.endObject()
    }

    private fun writeComic(writer: JsonWriter, c: Comic) {
        writer.beginObject()
        writer.name("number")
        writer.value(c.number)
        writer.name("title")
        writer.value(c.title)
        writer.name("availability")
        writer.value(c.availability.value)
        writer.endObject()
    }


}