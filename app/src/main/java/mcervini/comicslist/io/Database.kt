package mcervini.comicslist.io

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import mcervini.comicslist.BuildConfig

class Database(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object Database {
        private const val DATABASE_NAME = "comics.db"
        private const val DATABASE_VERSION = 2
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            """
            CREATE TABLE series (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL CHECK(name <> '')
            )
        """.trimIndent()
        )

        db?.execSQL(
            """
            CREATE TABLE comic(
                number INTEGER,
                series_id TEXT,
                title TEXT,
                availability INTEGER NOT NULL CHECK(availability >= 0 AND availability <=2),
                PRIMARY KEY(series_id,number),
                FOREIGN KEY(series_id) references series(id) ON DELETE CASCADE ON UPDATE CASCADE
                
            )
        """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE comic")
        db?.execSQL("DROP TABLE series")
        onCreate(db)
    }

    fun query(table: String, columns: Array<String>?, selection: String): Cursor {
        val db: SQLiteDatabase = readableDatabase
        return db.query(table, columns, selection, null, null, null, null)
    }

    fun insert(table: String, values: ContentValues) {
        val db: SQLiteDatabase = writableDatabase
        db.insert(table, null, values)
    }

    fun delete(table: String, where: String?) {
        val db: SQLiteDatabase = writableDatabase
        db.delete(table, where, null)
    }

    fun update(table: String, values: ContentValues, where: String) {
        val db: SQLiteDatabase = writableDatabase
        val affected: Int = db.update(table, values, where, null)
        if (BuildConfig.DEBUG && affected == 0) {
            error("Assertion failed")
        }
    }
}