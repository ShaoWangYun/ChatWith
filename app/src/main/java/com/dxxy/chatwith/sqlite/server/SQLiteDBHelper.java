package com.dxxy.chatwith.sqlite.server;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDBHelper extends SQLiteOpenHelper {

    //创建用户表
    public static final String CREATE_USER_DB = "create table User ("
            + "id integer primary key autoincrement, "
            + "username text, "
            + "password text)";

    //创建消息表
    public static final String CREATE_MESSAGE_DB = "create table Message ("
            + "id integer primary key autoincrement, "
            + "flag text,"
            + "source text, "
            + "destination text, "
            + "message text,"
            + "date text)";

    //创建好友表
    public static final String CREATE_FRIEND_DB = "create table Friend ("
            + "id integer primary key autoincrement, "
            + "myName text,"
            + "friendName text, "
            + "friendStatus integer)";

    public SQLiteDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_DB);
        db.execSQL(CREATE_MESSAGE_DB);
        db.execSQL(CREATE_FRIEND_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}