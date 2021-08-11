package mcervini.comicslist

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import mcervini.comicslist.io.JsonExporter
import mcervini.comicslist.io.JsonImporter
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*

@RunWith(AndroidJUnit4::class)
class JsonTest {
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun testWrite() {
        val file: File = File(context.getExternalFilesDir(null), "testWrite.json")
        val series1 = Series(UUID.fromString("9d03c7cd-fc09-485d-8c7d-8531cc9b7e89"), "series1")
        for (i in 1..3) {
            series1.comics.add(Comic(series1, i, "", Availability.fromValue(i - 1)))
        }

        val series2 = Series(UUID.fromString("ed7ff5a8-8dab-44c7-9cab-9d080c0a75f4"), "series2")
        for (i in 1..3) {
            series2.comics.add(Comic(series1, i, "comic$i", Availability.fromValue(i - 1)))
        }

        val series3 = Series(UUID.fromString("1f931bbc-3b6b-480b-bfa9-6894120e9744"), "series3")

        val series: MutableList<Series> = mutableListOf(series1, series2, series3)

        val jsonExporter = JsonExporter(file)

        jsonExporter.export(series)

        val fileReader = FileReader(file)
        val content: String = fileReader.readText()
        fileReader.close()
        val expected: String = "[{\"id\":\"9d03c7cd-fc09-485d-8c7d-8531cc9b7e89\",\"name\":\"series1\",\"comics\":[{\"number\":1,\"title\":\"\",\"availability\":0},{\"number\":2,\"title\":\"\",\"availability\":1},{\"number\":3,\"title\":\"\",\"availability\":2}]},{\"id\":\"ed7ff5a8-8dab-44c7-9cab-9d080c0a75f4\",\"name\":\"series2\",\"comics\":[{\"number\":1,\"title\":\"comic1\",\"availability\":0},{\"number\":2,\"title\":\"comic2\",\"availability\":1},{\"number\":3,\"title\":\"comic3\",\"availability\":2}]},{\"id\":\"1f931bbc-3b6b-480b-bfa9-6894120e9744\",\"name\":\"series3\",\"comics\":[]}]"
        assert(content.equals(expected))
    }

    @Test
    fun testRead() {
        val json: String = "[{\"id\":\"9d03c7cd-fc09-485d-8c7d-8531cc9b7e89\",\"name\":\"series1\",\"comics\":[{\"number\":1,\"title\":\"\",\"availability\":0},{\"number\":2,\"title\":\"\",\"availability\":1},{\"number\":3,\"title\":\"\",\"availability\":2}]},{\"id\":\"ed7ff5a8-8dab-44c7-9cab-9d080c0a75f4\",\"name\":\"series2\",\"comics\":[{\"number\":1,\"title\":\"comic1\",\"availability\":0},{\"number\":2,\"title\":\"comic2\",\"availability\":1},{\"number\":3,\"title\":\"comic3\",\"availability\":2}]},{\"id\":\"1f931bbc-3b6b-480b-bfa9-6894120e9744\",\"name\":\"series3\",\"comics\":[]}]"
        val file: File = File(context.getExternalFilesDir(null), "testRead.json")
        val writer: FileWriter = FileWriter(file)
        writer.write(json)
        writer.close()
        val jsonImporter: JsonImporter = JsonImporter(file)

        val series: MutableList<Series> = jsonImporter.import()

        val series1 = Series(UUID.fromString("9d03c7cd-fc09-485d-8c7d-8531cc9b7e89"), "series1")
        for (i in 1..3) {
            series1.comics.add(Comic(series1, i, "", Availability.fromValue(i - 1)))
        }

        val series2 = Series(UUID.fromString("ed7ff5a8-8dab-44c7-9cab-9d080c0a75f4"), "series2")
        for (i in 1..3) {
            series2.comics.add(Comic(series1, i, "comic$i", Availability.fromValue(i - 1)))
        }

        val series3 = Series(UUID.fromString("1f931bbc-3b6b-480b-bfa9-6894120e9744"), "series3")

        val expected: MutableList<Series> = mutableListOf(series1, series2, series3)

        assert(series.size == expected.size)

        for (i in series.indices) {
            val s = series[i]
            val e = expected[i]

            assert(s.id.equals(e.id))
            assert(s.name.equals(e.name))
            assert(s.comics.size == e.comics.size)
            for (j in s.comics.indices) {
                val cs = s.comics[j]
                val ce = e.comics[j]

                assert(cs.number == ce.number)
                assert(cs.title.equals(ce.title))
                assert(cs.availability.value == ce.availability.value)
            }
        }
    }
}