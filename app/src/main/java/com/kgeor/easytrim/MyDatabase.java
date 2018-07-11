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

    public long insertData(int speed, int trim) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.BOAT_SPEED, speed);
        contentValues.put(DatabaseConstants.BOAT_TRIM, trim);
        long id = db.insert(DatabaseConstants.TABLE_NAME, null, contentValues);
        return id;
    }


    public String getData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {DatabaseConstants.UID, DatabaseConstants.BOAT_SPEED,
                DatabaseConstants.BOAT_TRIM};
        Cursor cursor = db.query(DatabaseConstants.TABLE_NAME, columns, null, null, null, null, null);

        StringBuffer buffer = new StringBuffer();

        while (cursor.moveToNext()) {
            int index = cursor.getInt(0);
            int speed = cursor.getInt(1);
            int trim = cursor.getInt(2);
            buffer.append("Database Index: " + index + " Speed: " + speed + " Trim: " + trim + "\n");
        }
        return buffer.toString();
    }


    public String getSelectedData(String speed) {

        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {DatabaseConstants.BOAT_SPEED, DatabaseConstants.BOAT_TRIM};

        String selection = DatabaseConstants.BOAT_SPEED + "='" + speed + "'";
        Cursor cursor = db.query(DatabaseConstants.TABLE_NAME, columns, selection, null, null, null, null);

        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(DatabaseConstants.BOAT_SPEED);
            int index2 = cursor.getColumnIndex(DatabaseConstants.BOAT_TRIM);
            String dataBoatSpeed = cursor.getString(index1);
            String dataBoatTrim = cursor.getString(index2);
            buffer.append(dataBoatSpeed);
        }
        return buffer.toString();
    }

    public String getTrimData(String speed) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {DatabaseConstants.BOAT_SPEED, DatabaseConstants.BOAT_TRIM};

        String selection = DatabaseConstants.BOAT_SPEED + "='" + speed + "'";
        Cursor cursor = db.query(DatabaseConstants.TABLE_NAME, columns, selection, null, null, null, null);

        StringBuffer buffer = new StringBuffer();

        while (cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(DatabaseConstants.BOAT_SPEED);
            int index2 = cursor.getColumnIndex(DatabaseConstants.BOAT_TRIM);
            String dataBoatSpeed = cursor.getString(index1);
            String dataBoatTrim = cursor.getString(index2);
            buffer.append(dataBoatTrim);
        }
        return buffer.toString();
    }


}
