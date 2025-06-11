package u.ficappx.components.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import u.ficappx.api.classes.Fanfic

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION ) {
    companion object{
        private const val DATABASE_NAME = "fanfic_saved_db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_FANFICS = "fanfics"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_AUTHOR = "author"
        private const val COLUMN_TAGS = "tags"
        private const val COLUMN_BADGES = "badges"
        private const val COLUMN_URL = "url"
        private const val COLUMN_SHORT_DESCRIPTION = "short_description"
        private const val COLUMN_FANDOMS = "fandoms"
        private const val COLUMN_PARTS_COUNT = "parts_count"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableSql = """
            CREATE TABLE $TABLE_FANFICS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_AUTHOR TEXT NOT NULL,
                $COLUMN_TAGS TEXT NOT NULL,
                $COLUMN_BADGES TEXT NOT NULL,
                $COLUMN_URL TEXT NOT NULL,
                $COLUMN_SHORT_DESCRIPTION TEXT NOT NULL,
                $COLUMN_FANDOMS TEXT NOT NULL,
                $COLUMN_PARTS_COUNT INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableSql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FANFICS")
        onCreate(db)
    }

    fun insert(fanfic: Fanfic): Long{
        val db = writableDatabase
        val appendValues = ContentValues().apply {
            put(COLUMN_NAME, fanfic.name)
            put(COLUMN_AUTHOR, Converters.fromAuthor(fanfic.author))
            put(COLUMN_TAGS, Converters.fromTagList(fanfic.tags))
            put(COLUMN_BADGES, Converters.fromBadgeList(fanfic.badges))
            put(COLUMN_URL, fanfic.url)
            put(COLUMN_SHORT_DESCRIPTION, fanfic.shortDescription)
            put(COLUMN_FANDOMS, Converters.fromFandomList(fanfic.fandoms))
            put(COLUMN_PARTS_COUNT, fanfic.partsCount)
        }
        val id = db.insert(TABLE_FANFICS, null, appendValues)
        db.close()
        return id
    }

    fun getFanficById(id: Long): Fanfic? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_FANFICS,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_AUTHOR, COLUMN_TAGS, COLUMN_BADGES, COLUMN_URL, COLUMN_SHORT_DESCRIPTION, COLUMN_FANDOMS),
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        var fanfic: Fanfic? = null
        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val authorJson = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR))
            val tagsJson = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAGS))
            val badgesJson = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BADGES))
            val url = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL))
            val shortDescription = cursor.getString(cursor.getColumnIndexOrThrow(
                COLUMN_SHORT_DESCRIPTION
            ))
            val fandomsJson = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FANDOMS))
            val partsCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PARTS_COUNT))
            fanfic = Fanfic(
                name = name,
                author = Converters.toAuthor(authorJson),
                tags = Converters.toTagList(tagsJson),
                badges = Converters.toBadgeList(badgesJson),
                url = url,
                shortDescription = shortDescription,
                fandoms = Converters.toFandomList(fandomsJson),
                partsCount = partsCount
            )
        }
        cursor.close()
        db.close()
        return fanfic
    }

    fun getAllFanfics(): List<Fanfic> {
        val fanfics = mutableListOf<Fanfic>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_FANFICS", null)

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val authorJson = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR))
                val tagsJson = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAGS))
                val badgesJson = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BADGES))
                val url = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL))
                val shortDescription = cursor.getString(cursor.getColumnIndexOrThrow(
                    COLUMN_SHORT_DESCRIPTION
                ))
                val fandomsJson = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FANDOMS))
                val partsCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PARTS_COUNT))
                val fanfic = Fanfic(
                    name = name,
                    author = Converters.toAuthor(authorJson),
                    tags = Converters.toTagList(tagsJson),
                    badges = Converters.toBadgeList(badgesJson),
                    url = url,
                    shortDescription = shortDescription,
                    fandoms = Converters.toFandomList(fandomsJson),
                    partsCount = partsCount
                )
                fanfics.add(fanfic)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return fanfics
    }

    fun updateFanfic(id: Long, fanfic: Fanfic): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, fanfic.name)
            put(COLUMN_AUTHOR, Converters.fromAuthor(fanfic.author))
            put(COLUMN_TAGS, Converters.fromTagList(fanfic.tags))
            put(COLUMN_BADGES, Converters.fromBadgeList(fanfic.badges))
            put(COLUMN_URL, fanfic.url)
            put(COLUMN_SHORT_DESCRIPTION, fanfic.shortDescription)
            put(COLUMN_FANDOMS, Converters.fromFandomList(fanfic.fandoms))
            put(COLUMN_PARTS_COUNT, fanfic.partsCount)
        }
        val rowsAffected = db.update(TABLE_FANFICS, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsAffected
    }

    fun deleteFanfic(id: Long): Int {
        val db = writableDatabase
        val rowsAffected = db.delete(TABLE_FANFICS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsAffected
    }

    fun deleteFanfic(fanfic: Fanfic): Int {
        val db = writableDatabase
        val rowsAffected = db.delete(
            TABLE_FANFICS, "$COLUMN_NAME = ? AND $COLUMN_URL = ?", (arrayOf(
            fanfic.name, fanfic.url)))
        db.close()
        return rowsAffected
    }


    fun exists(fanfic: Fanfic) : Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_FANFICS WHERE $COLUMN_NAME = ? AND $COLUMN_URL = ?"
        val cursor = db.rawQuery(query, arrayOf(fanfic.name, fanfic.url))
        val exists = cursor.moveToFirst()
        cursor.close()
        db.close()
        return exists

    }

    fun putOrDelete(fanfic: Fanfic): Boolean{
        val exists = exists(fanfic)
        if(exists) {
            deleteFanfic(fanfic)
            return false
        }
        else{
            insert(fanfic)
            return true
        }
    }
}