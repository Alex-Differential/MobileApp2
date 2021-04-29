package com.example.lab2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class RegionDBHelper(context:Context) : SQLiteOpenHelper(context,"REGION",null,1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE REGION ( REG_ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, POPULATION INT, SQUARE DOUBLE, REGCENTER TEXT)")
        //db?.execSQL("INSERT INTO REGION (NAME,POPULATION) VALUES ('DFEFE','123.0')")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}