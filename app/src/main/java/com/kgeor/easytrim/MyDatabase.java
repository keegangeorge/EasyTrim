package com.kgeor.easytrim;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MyDatabase {
    private SQLiteDatabase db;
    private Context context;
    private final MyDatabaseHelper helper;

    public MyDatabase(Context c) {
        context = c;
        helper = new MyDatabaseHelper(context);
    }

    public long insertData(int speed, int pitch, int roll, int trim) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.BOAT_SPEED, speed);
        contentValues.put(DatabaseConstants.BOAT_PITCH, pitch);
        contentValues.put(DatabaseConstants.BOAT_ROLL, roll);
        contentValues.put(DatabaseConstants.BOAT_TRIM, trim);
        long id = db.insert(DatabaseConstants.TABLE_NAME, null, contentValues);
        return id;
    }

    public String getData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {DatabaseConstants.UID, DatabaseConstants.BOAT_SPEED,
                DatabaseConstants.BOAT_PITCH, DatabaseConstants.BOAT_ROLL,
                DatabaseConstants.BOAT_TRIM};
        Cursor cursor = db.query(DatabaseConstants.TABLE_NAME, columns, null, null, null, null, null);

        StringBuffer buffer = new StringBuffer();

        while (cursor.moveToNext()) {
            int index = cursor.getInt(0);
            int speed = cursor.getInt(1);
            int pitch = cursor.getInt(2);
            int roll = cursor.getInt(3);
            int trim = cursor.getInt(4);
            buffer.append("Database Index" + index + " Speed: " + speed + " Pitch: " + pitch + " Roll: " + roll + " Trim: " + trim + "\n");
        }
        return buffer.toString();
    }

    public String getSelectedData(String speed) {
        // find out the trim angle based on the desired speed
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {DatabaseConstants.UID, DatabaseConstants.BOAT_SPEED,
                DatabaseConstants.BOAT_PITCH, DatabaseConstants.BOAT_ROLL,
                DatabaseConstants.BOAT_TRIM};
        String selection = DatabaseConstants.BOAT_SPEED + "='" + speed + "'";  //Constants.TYPE = 'type'
        Cursor cursor = db.query(DatabaseConstants.TABLE_NAME, columns, selection, null, null, null, null);

        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {

            int index1 = cursor.getColumnIndex(DatabaseConstants.BOAT_SPEED);
            int index2 = cursor.getColumnIndex(DatabaseConstants.BOAT_TRIM);
            String boatSpeed = cursor.getString(index1);
            String boatTrim = cursor.getString(index2);
            buffer.append(boatSpeed + " " + boatTrim + "\n");
        }
        return buffer.toString();
    }

}
