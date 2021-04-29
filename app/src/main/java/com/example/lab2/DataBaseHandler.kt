package com.example.lab2

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHandler (context: Context):SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION){

    companion object{
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "RegionsDataBase"
        val TABLE_CONTACTS = "RegionsTable"
        private val COL_ID = "id"
        private val COL_NAME = "Region"
        private val COL_POPULATION = "Popul"
        private val COL_SQUARE = "Square"
        private val COL_REGCENTER = "RegCenter"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val createTable = ("CREATE TABLE " + TABLE_CONTACTS + " (" +
                COL_ID +" INTEGER PRIMARY KEY," +
                COL_NAME + " TEXT," +
                COL_POPULATION + " INTEGER," +
                COL_SQUARE + " DOUBLE(13,2)," +
                COL_REGCENTER + " TEXT )")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        onCreate(db)
    }

    fun qwe(): Double {

        val regList: ArrayList<Region> = ArrayList<Region>()

        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS"
        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            //return Double.MAX_VALUE
        }

        //Toast.makeText(applicationContext,"NIT", android.widget.Toast.LENGTH_SHORT).show()
        var id: Int
        var name: String
        var population: Int
        var square: Double
        var regCenter: String
        var sum = 0.0
        var i =0

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    population = cursor.getString(cursor.getColumnIndex(COL_POPULATION)).toInt()

                    sum += population
                    i++

                } while (cursor!!.moveToNext())
            }
        }
        return sum/i
    }

    fun viewRegion(POPULATION:Int): ArrayList<Region> {

        val regList: ArrayList<Region> = ArrayList<Region>()

        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS WHERE $COL_POPULATION <= $POPULATION"
        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var name: String
        var population: Int
        var square: Double
        var regCenter: String

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(COL_ID))
                name = cursor.getString(cursor.getColumnIndex(COL_NAME))
                population = cursor.getString(cursor.getColumnIndex(COL_POPULATION)).toInt()
                square = cursor.getString(cursor.getColumnIndex(COL_SQUARE)).toDouble()
                regCenter = ""

                val reg = Region(id = id, name = name, population = population, square = square, regCenter = regCenter)
                regList.add(reg)

            } while (cursor.moveToNext())
        }
        return regList
    }

    fun addRegion(reg:Region):Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(COL_NAME, reg.name)
        contentValues.put(COL_POPULATION, reg.population)
        contentValues.put(COL_SQUARE, reg.square)
        contentValues.put(COL_REGCENTER, reg.regCenter)

        val success = db.insert(TABLE_CONTACTS, null, contentValues)
        db.close()
        return success
    }

    fun viewEmployee(): ArrayList<Region> {

        val regList: ArrayList<Region> = ArrayList<Region>()

        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS"
        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var name: String
        var population: Int
        var square: Double
        var regCenter: String

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(COL_ID))
                name = cursor.getString(cursor.getColumnIndex(COL_NAME))
                population = cursor.getString(cursor.getColumnIndex(COL_POPULATION)).toInt()
                square = cursor.getString(cursor.getColumnIndex(COL_SQUARE)).toDouble()
                regCenter = cursor.getString(cursor.getColumnIndex(COL_REGCENTER))

                val reg = Region(id = id, name = name, population = population, square = square, regCenter = regCenter)
                regList.add(reg)

            } while (cursor.moveToNext())
        }
        return regList
    }

    fun updateEmployee(reg: Region): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_NAME, reg.name)
        contentValues.put(COL_SQUARE, reg.square)
        contentValues.put(COL_POPULATION, reg.population)
        contentValues.put(COL_REGCENTER, reg.regCenter)

        val success = db.update(TABLE_CONTACTS, contentValues, COL_ID + "=" + reg.id, null)

        db.close()
        return success
    }

    fun deleteRegion(reg: Region): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_ID, reg.id)

        val success = db.delete(TABLE_CONTACTS, COL_ID + "=" + reg.id, null)
        db.close()
        return success
    }

}