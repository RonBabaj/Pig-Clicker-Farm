package com.example.hazirclicker2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HelperDB extends SQLiteOpenHelper {
    public static final String DB_NAME = "hazirClicker.db";
    public static final String OINKERS = "oinkers";
    public static final String OINKER_TABLE = "oinkcounter";

    public HelperDB(Context context) {
        super(context, DB_NAME, (SQLiteDatabase.CursorFactory) null, 1);
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE oinkcounter (oinkers INT);");
        sqLiteDatabase.execSQL("CREATE TABLE Skins (skindex TEXT ,price TEXT, Name TEXT, isEquipped TEXT, isBought TEXT);");
        sqLiteDatabase.execSQL("CREATE TABLE Upgrades (upgrade TEXT ,price INT, timesUpgraded INT);");
        sqLiteDatabase.execSQL("CREATE TABLE PiggyBank (oinkers INT,ticker INT);");
        sqLiteDatabase.execSQL("CREATE TABLE Handlers (handlerTimes INT);");

    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
