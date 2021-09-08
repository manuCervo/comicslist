package mcervini.comicslist.io

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * gives access to the SQlite database by providing methods for running selection,insertion,update and deletion queries
 */
class Database(context: Context) :

    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object Database {
        private const val DATABASE_NAME = "comics.db"
        private const val DATABASE_VERSION = 2
    }


    override fun onCreate(db: SQLiteDatabase?) {
        db?.run {

            execSQL(
                """
                CREATE TABLE series (
                    id TEXT PRIMARY KEY,
                    name TEXT NOT NULL CHECK(name <> '')
                )
                """.trimIndent()
            )

            execSQL(
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
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.run {
            execSQL("DROP TABLE comic")
            execSQL("DROP TABLE series")
            onCreate(this)
        }
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        db?.execSQL("PRAGMA foreign_keys=ON")
    }

    /**
     * runs a selection query and returns a cursor with the result
     * @param table the table to run the query on
     * @param columns the columns to select
     * @param selection the selection clause
     */
    fun select(table: String, columns: Array<String>?, selection: String?): Cursor {
        val db: SQLiteDatabase = readableDatabase
        return db.query(table, columns, selection, null, null, null, null)
    }

    /**
     * inserts into a table
     * @param table the table to insert the values into
     * @param values the values to insert
     */
    fun insert(table: String, values: ContentValues) {
        val db: SQLiteDatabase = writableDatabase
        db.insert(table, null, values)
    }

    /**
     * deletes from a table
     *
     * @param table the table to remove from
     * @param where the conditions for removing a row
     */
    fun delete(table: String, where: String?) {
        val db: SQLiteDatabase = writableDatabase
        db.delete(table, where, null)
    }

    /**
     * updates a table
     * @param table the table to update
     * @param values the values to change
     * @param where the conditions for a row to be updated
     */
    fun update(table: String, values: ContentValues, where: String) {
        val db: SQLiteDatabase = writableDatabase
        db.update(table, values, where, null)

    }
}