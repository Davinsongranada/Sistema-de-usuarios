package com.example.appuserssqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class clsDB extends SQLiteOpenHelper {
    // Definir las vbles de cada una de las tablas (create)
    String tblUser = "Create Table user(username text, fullname text, password text, rol integer)";
    String tblProduct = "create table product(ref text, descrip text, unitprice integer, stock integer)";
    public clsDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tblUser);
        db.execSQL(tblProduct);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop Table User");
        db.execSQL(tblUser);
        db.execSQL("Drop Table Product");
        db.execSQL(tblProduct);
    }
}
