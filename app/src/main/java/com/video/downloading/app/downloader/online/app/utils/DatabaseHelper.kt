package com.video.downloading.app.downloader.online.app.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.video.downloading.app.downloader.online.app.models.Bookmarks

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    companion object {
        private const val DATABASE_NAME = "videoDownloaderDb"
        private const val TABLE_NAME4 = "bookmarktbl"
        private const val KEY_ID = "id"
        const val COLUMN_title = "title"
        const val COLUMN_address = "address"

    }

    override fun onCreate(p0: SQLiteDatabase?) {
        //create bookmark table
        val CREATE_TABLE_BOOKMARk = ("CREATE TABLE " + TABLE_NAME4 + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_title + " TEXT,"
                + COLUMN_address + " TEXT);")

        p0?.execSQL(CREATE_TABLE_BOOKMARk)


    }

    override fun onUpgrade(p0: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        p0?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME4")
        onCreate(p0)
    }

    //TODO: check bookmark exist
    fun checkBookmarkExist(title: String): Boolean {
        val db = this.writableDatabase

        //Get data from table
        val query = "select * from  " + TABLE_NAME4 + " where " +
                COLUMN_title + " = " + "'" + title + "'"
        val cursor = db.rawQuery(query, null)

        cursor.moveToFirst()
        if (cursor.count > 0) {

            return true
        }
        cursor.close()
        db.close()

        return false
    }

    //TODO: delete bookmark
    fun deletebookmark(id: String): Int? {
        val sqLiteDatabase = this.writableDatabase
        return sqLiteDatabase.delete(TABLE_NAME4, "id = ?", arrayOf(id))
    }

    //TODO: all bookmarks
    fun getAllBookmarks(): ArrayList<Bookmarks> {
        val db = this.readableDatabase
        val query = "select * from  $TABLE_NAME4"
        val cursor = db.rawQuery(query, arrayOf())
        val messages = ArrayList<Bookmarks>()
        if (cursor != null) {
            while (cursor.moveToNext()) {
                messages.add(
                    Bookmarks(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2)
                    )
                )
            }
            if (!cursor.isClosed) cursor.close()
        }
        db.close()
        return messages
    }

    //TODO: add bookmark table
    fun addbookmark(title: String, address: String): Boolean {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(COLUMN_title, title)
        contentValues.put(COLUMN_address, address)

        val result = db.insert(TABLE_NAME4, null, contentValues)

        db.close()
        return result > 0
    }

}