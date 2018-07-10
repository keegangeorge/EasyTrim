package com.kgeor.easytrim;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;

    private static final String CREATE_TABLE =
            "CREATE TABLE " +
                    DatabaseConstants.TABLE_NAME + " (" +
                    DatabaseConstants.UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DatabaseConstants.BOAT_SPEED + " INTEGER, " +
                    DatabaseConstants.BOAT_TRIM + " INTEGER);";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + DatabaseConstants.TABLE_NAME;

    public MyDatabaseHelper(Context context) {
        super(context, DatabaseConstants.DATABASE_NAME, null, DatabaseConstants.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE);
            Toast.makeText(context, "Database onCreate() called", Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            Toast.makeText(context, "Database exception onCreate() db", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(DROP_TABLE);
            onCreate(db);
            Toast.makeText(context, "Database onUpgrade called", Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            Toast.makeText(context, "Database exception onUpgrade() db", Toast.LENGTH_LONG).show();
        }
    }
}
