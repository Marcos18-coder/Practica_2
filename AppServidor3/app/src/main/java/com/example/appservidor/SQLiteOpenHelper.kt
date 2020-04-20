package com.example.appservidor

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context):SQLiteOpenHelper(context, DATABASE_NAME,null,1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("CREATE TABLE $TABLE_NAME (ID INTEGER PRIMARY KEY AUTOINCREMENT, NOMBRE TEXT, APELLIDO TEXT,EDAD INT,RADIO BOOLEANO)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun insertData(nombre: String,apellido: String,edad: Int, radio: Boolean) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_2, nombre)
        contentValues.put(COL_3, apellido)
        contentValues.put(COL_4, edad)
        contentValues.put(COL_5, radio)
        var bol=  db.insert(TABLE_NAME, null, contentValues)
    }
    val allDate: Cursor
        get() {
        val db = this.writableDatabase
        val res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null)
        return res
    }

    companion object{
        val DATABASE_NAME = "versionTres.db"
        val TABLE_NAME = "table_Datos"
        // val COL_1 = "ID"
        val COL_2 = "NOMBRE"
        val COL_3 = "APELLIDO"
        val COL_4 = "EDAD"
        val COL_5 = "RADIO"
    }
}