package com.kgeor.easytrim;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Class containing methods relating to the database
 */
public class MyDatabase {
    private SQLiteDatabase db;
    private Context context;
    private final MyDatabaseHelper helper;

    public MyDatabase(Context c) {
        context = c;
        helper = new MyDatabaseHelper(context);
    }

    /**
     * Method responsible for inserting values into the columns of the database
     *
     * @param speed the speed integer value to be inserted
     * @param trim  the trim integer value to be inserted
     * @return returns the id of the inserted value
     */
    public long insertData(int speed, int trim) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.BOAT_SPEED, speed);
        contentValues.put(DatabaseConstants.BOAT_TRIM, trim);
        long id = db.insert(DatabaseConstants.TABLE_NAME, null, contentValues);
        return id;
    }


    /**
     * Fetches all the data that is contained in the database.
     * The method will query through each column and showcase those values in a String.
     *
     * @return a String buffer that contains the database values appended to it
     */
    public String getData() {
        // reference to the helper class
        SQLiteDatabase db = helper.getWritableDatabase();
        // check through all the columns of the database
        String[] columns = {DatabaseConstants.UID, DatabaseConstants.BOAT_SPEED,
                DatabaseConstants.BOAT_TRIM};
        // query the database using the cursor
        Cursor cursor = db.query(DatabaseConstants.TABLE_NAME, columns, null, null, null, null, null);
        // create a new StringBuffer to store the queried values
        StringBuffer buffer = new StringBuffer();
        // iterate through the database and append the queried values
        while (cursor.moveToNext()) {
            int index = cursor.getInt(0);
            int speed = cursor.getInt(1);
            int trim = cursor.getInt(2);
            buffer.append("Database Index: " + index + " Speed: " + speed + " Trim: " + trim + "\n");
        }
        // return the StringBuffer with the queried values
        return buffer.toString();
    }

    /**
     * Method responsible for fetching all the data that is contained in the database,
     * and then returning the values in a buffer string, but stylized by making the titles
     * bold and adding line breaks
     *
     * @param units the current measurement units for the speed value
     * @return the StringBuffer with all the queried values appended to it
     */
    public String getDataStylized(String units) {
        // reference to the helper class
        SQLiteDatabase db = helper.getWritableDatabase();
        // check through all the columns of the database
        String[] columns = {DatabaseConstants.UID, DatabaseConstants.BOAT_SPEED,
                DatabaseConstants.BOAT_TRIM};
        // query the database using the cursor
        Cursor cursor = db.query(DatabaseConstants.TABLE_NAME, columns, null, null, null, null, null);
        // create a new StringBuffer to store the queried values
        StringBuffer buffer = new StringBuffer();
        // iterate through the database and append the queried values
        while (cursor.moveToNext()) {
            int index = cursor.getInt(0);
            int speed = cursor.getInt(1);
            int trim = cursor.getInt(2);
            String info = "<b>Speed:</b> " + speed + " " + units + "<br><b>&ensp;&nbsp;Trim: </b>" + trim + "&#176;<br>";
            buffer.append(info + "<br>");
        }
        // return the StringBuffer with the queried values
        return buffer.toString();
    }


    /**
     * Method responsible for getting the speed-trim correlated values based on the current
     * speed of the device on the boat
     *
     * @param speed the current speed of the deviec on the boat
     * @return returns an int with the speed values
     */
    public int getSelectedData(int speed) {
        // reference to the helper class
        SQLiteDatabase db = helper.getWritableDatabase();
        // check through two of the columns of the database
        String[] columns = {DatabaseConstants.BOAT_SPEED, DatabaseConstants.BOAT_TRIM};
        // query the database using the cursor based on the selection
        String selection = DatabaseConstants.BOAT_SPEED + "='" + speed + "'";
        Cursor cursor = db.query(DatabaseConstants.TABLE_NAME, columns, selection, null, null, null, null);
        // create a new integer variable for store the values that are to be queried
        int bufferInt = 0;
        // iterate through the database and append the queried values
        while (cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(DatabaseConstants.BOAT_SPEED);
            int index2 = cursor.getColumnIndex(DatabaseConstants.BOAT_TRIM);
            int dataBoatSpeed = cursor.getInt(index1);
            int dataBoatTrim = cursor.getInt(index2);
            bufferInt = dataBoatSpeed;
        }
        // return the integer with the queried values
        return bufferInt;
    }

    /**
     * Method responsible for checking if the speed value is already in the database
     *
     * @param speed the speed value to be checkec if its in the database
     * @return returns true or false based on if the value is in the database or not
     */
    public boolean hasObject(String speed) {
        // reference to the helper class
        SQLiteDatabase db = helper.getWritableDatabase();
        // string representing the raw query to take place
        String selectString = "SELECT * FROM " + DatabaseConstants.TABLE_NAME + " WHERE " + DatabaseConstants.BOAT_SPEED + " =?";
        // iterate through the database with the cursor based on the specified raw query
        Cursor cursor = db.rawQuery(selectString, new String[]{speed});
        // local boolean variable indicating if the value is there or not
        boolean hasObject = false;
        if (cursor.moveToFirst()) {
            hasObject = true;
            int count = 0;
            while (cursor.moveToNext()) {
                count++;
            }
        }
        cursor.close();
        db.close();
        return hasObject;
    }

    /**
     * Method responsible for checking if the specified speed value is in the database, and if not
     * it will return the closest value to the specified speed value
     *
     * @param speed the current speed value of the boat that is to be compared with the database
     * @return the integer value closest/same as the specified speed value
     */
    public int getCloseToData(int speed) {
        // reference to the helper class
        SQLiteDatabase db = helper.getWritableDatabase();
        // check through two of the columns of the database
        String[] columns = {DatabaseConstants.BOAT_SPEED, DatabaseConstants.BOAT_TRIM};
        // query the database using the cursor based on the selection
        Cursor cursor = db.query(DatabaseConstants.TABLE_NAME, columns,
                null, null, null, null,
                "abs(" + DatabaseConstants.BOAT_SPEED + " - " + speed + ")", "1");
        int bufferInt = 0;
        while (cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(DatabaseConstants.BOAT_SPEED);
            int index2 = cursor.getColumnIndex(DatabaseConstants.BOAT_TRIM);
            int dataBoatSpeed = cursor.getInt(index1);
            int dataBoatTrim = cursor.getInt(index2);
            bufferInt = dataBoatSpeed;
        }
        return bufferInt;
    }


    /**
     * Method responsible for getting the trim value based on the current speed value
     *
     * @param speed the current speed value of the boat
     * @return an integer value of the trim value associated with the speicfied speed
     */
    public int getTrimData(int speed) {
        // reference to the helper class
        SQLiteDatabase db = helper.getWritableDatabase();
        // check through two of the columns of the database
        String[] columns = {DatabaseConstants.BOAT_SPEED, DatabaseConstants.BOAT_TRIM};
        // query the database using the cursor based on the selection
        String selection = DatabaseConstants.BOAT_SPEED + "='" + speed + "'";
        Cursor cursor = db.query(DatabaseConstants.TABLE_NAME, columns, selection, null, null, null, null);

        int bufferInt = 0;

        while (cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(DatabaseConstants.BOAT_SPEED);
            int index2 = cursor.getColumnIndex(DatabaseConstants.BOAT_TRIM);

            int dataBoatSpeed = cursor.getInt(index1);
            int dataBoatTrim = cursor.getInt(index2);
            bufferInt = dataBoatTrim;
        }
        return bufferInt;
    }
} // MyDatabase class end
