import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

const val DATABASE_NAME = "Notes.db"
const val TABLE_NAME_NOTES = "notes"
const val COLUMN_TITLE = "title"
const val COLUMN_TEXT = "text"
const val DATABASE_VERSION = 1

class MyDatabaseHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Create the users table
        val createTableUsers = """
        CREATE TABLE $TABLE_NAME_USERS (
            $COLUMN_EMAIL TEXT PRIMARY KEY, 
            $COLUMN_NAME TEXT, 
            $COLUMN_PASSWORD TEXT
        )
        """.trimIndent()

        // Create the notes table with a foreign key reference to users
        val createTableNotes = """
        CREATE TABLE $TABLE_NAME_NOTES(
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_TITLE TEXT, 
            $COLUMN_TEXT TEXT,
            $COLUMN_EMAIL TEXT,
            $COLUMN_COLOR TEXT,
            $TIME_STAMP TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY ($COLUMN_EMAIL) REFERENCES $TABLE_NAME_USERS($COLUMN_EMAIL)
                ON DELETE CASCADE
                ON UPDATE CASCADE
        )
        """.trimIndent()

        // Execute SQL statements to create tables
        db.execSQL(createTableUsers)
        db.execSQL(createTableNotes)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_NOTES")
        onCreate(db)
    }

    fun insertUser(name: String, email: String, password: String): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
        }
        val result = db.insert(TABLE_NAME_USERS, null, cv)
        db.close() // Close the database connection
        return result != -1L
    }

    fun readAllUsers(): Cursor? {
        val query = "SELECT * FROM $TABLE_NAME_USERS"
        val db = this.readableDatabase
        return db.rawQuery(query, null)
    }

    fun readAllNotes(email: String?): Cursor? {
        if (email == null) {
            throw IllegalArgumentException("Email cannot be null")
        }
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME_NOTES WHERE $COLUMN_EMAIL = ?", arrayOf(email))
    }



    fun addNote(title: String?, text: String?, email: String?, color: String, context: Context) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_TEXT, text)
            put(COLUMN_EMAIL, email)
            put(COLUMN_COLOR, color)
        }
        val result = db.insert(TABLE_NAME_NOTES, null, cv)
        if (result == -1L) {
            Toast.makeText(context, "Failed to insert note", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Note added successfully", Toast.LENGTH_SHORT).show()
        }
        db.close() // Close the database connection
    }


    fun checkUsername(email: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME_USERS WHERE $COLUMN_EMAIL = ?", arrayOf(email))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun checkUsernamePassword(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME_USERS WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?", arrayOf(email, password))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun updateData(id: String, title: String, text: String, color: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("title", title)
            put("text", text)
            put("color", color)
        }
        val result = db.update("my_notes", contentValues, "_id = ?", arrayOf(id))
        db.close()
        return result > 0
    }


    fun getNameByEmail(email: String): String? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME_USERS,
            arrayOf(COLUMN_NAME),
            "$COLUMN_EMAIL = ?",
            arrayOf(email),
            null,
            null,
            null
        )
        return if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            cursor.close()
            name
        } else {
            cursor.close()
            null
        }
    }

    fun deleteNote(title: String, email: String) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME_NOTES, "$COLUMN_TITLE = ? AND $COLUMN_EMAIL = ?", arrayOf(title, email))
        db.close()
    }
        fun searchNotes(email: String, query: String): Cursor? {
            val db = this.readableDatabase
            val searchQuery = """
        SELECT * FROM $TABLE_NAME_NOTES
        WHERE $COLUMN_EMAIL = ? AND (
            $COLUMN_TITLE LIKE ? OR $COLUMN_TEXT LIKE ?
        )
    """.trimIndent()
            val cursor = db.rawQuery(
                searchQuery,
                arrayOf(email, "%$query%", "%$query%")
            )
            return cursor
        }

        companion object {
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME_USERS = "my_creds"
        private const val TABLE_NAME_NOTES = "my_notes"
        private const val COLUMN_ID = "_id"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_TEXT = "text"
        private const val COLUMN_COLOR = "color"
        private const val TIME_STAMP = "time"
    }
}
