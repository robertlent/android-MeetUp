package com.lentcoding.meetup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.sql.SQLException;

public class DBAdapter {
    static final String TAG = "DBAdapter";
    static final String DATABASE_NAME = "meetupDB.sqlite";
    static final int DATABASE_VERSION = 1;
    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context c) {
        this.context = c;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteAssetHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS Friends");
            onCreate(db);
        }
    }

    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DBHelper.close();
    }

    public long insertFriend(String name, String email, String phone) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("name", name);
        initialValues.put("email", email);
        initialValues.put("phone", phone);
        return db.insert("Friends", null, initialValues);
    }

    public boolean deleteFriend(int rowId) {
        return db.delete("Friends", "id" + "=" + rowId, null) > 0;
    }

    public Cursor getAllFriends() {
        return db.query("Friends", new String[]{"id", "name", "email", "phone"}, null, null, null, null, "name COLLATE NOCASE");
    }

    public Cursor getFriend(int rowId) throws SQLException {
        Cursor mCursor = db.query(true, "Friends", new String[]{"id", "name", "email", "phone"}, "id" + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean updateFriend(int id, String name, String email, String phone) {
        ContentValues args = new ContentValues();
        args.put("name", name);
        args.put("email", email);
        args.put("phone", phone);
        return db.update("Friends", args, "id" + "=" + id, null) > 0;
    }

    public long insertPlace(String name, String address, String phone, String placeType) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("name", name);
        initialValues.put("address", address);
        initialValues.put("phone", phone);
        initialValues.put("placeType", placeType);
        return db.insert("Places", null, initialValues);
    }

    public boolean deletePlace(int rowId) {
        return db.delete("Places", "id" + "=" + rowId, null) > 0;
    }

    public Cursor getAllPlaces() {
        return db.query("Places", new String[]{"id", "name", "address", "phone", "placeType"}, null, null, null, null, "name COLLATE NOCASE");
    }

    public Cursor getPlace(int rowId) throws SQLException {
        Cursor mCursor = db.query(true, "Places", new String[]{"id", "name", "address", "phone", "placeType"}, "id" + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean updatePlace(int id, String name, String address, String phone, String placeType) {
        ContentValues args = new ContentValues();
        args.put("name", name);
        args.put("address", address);
        args.put("phone", phone);
        args.put("placeType", placeType);

        return db.update("Places", args, "id" + "=" + id, null) > 0;
    }

    public long insertMeetUp(String name, String desc, String friends, String place, String date, String time) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("name", name);
        initialValues.put("desc", desc);
        initialValues.put("friends", friends);
        initialValues.put("place", place);
        initialValues.put("date", date);
        initialValues.put("time", time);
        return db.insert("MeetUps", null, initialValues);
    }

    public boolean deleteMeetUp(int rowId) {
        return db.delete("MeetUps", "id" + "=" + rowId, null) > 0;
    }

    public Cursor getAllMeetUps() {
        return db.query("MeetUps", new String[]{"id", "name", "desc", "friends", "place", "date", "time"}, null, null, null, null, "date, time");
    }

    public Cursor getMeetUp(int rowId) throws SQLException {
        Cursor mCursor =
                db.query(true, "MeetUps", new String[]{"id", "name", "desc", "friends", "place", "date", "time"}, "id" + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean updateMeetUp(int id, String name, String desc, String friends, String place, String date, String time) {
        ContentValues args = new ContentValues();
        args.put("name", name);
        args.put("desc", desc);
        args.put("friends", friends);
        args.put("place", place);
        args.put("date", date);
        args.put("time", time);

        return db.update("MeetUps", args, "id" + "=" + id, null) > 0;
    }

}
