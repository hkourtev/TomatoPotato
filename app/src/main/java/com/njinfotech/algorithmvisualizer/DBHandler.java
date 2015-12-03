package com.njinfotech.algorithmvisualizer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hkourtev on 11/30/15.
 */
public class DBHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "statistics";

    // Contacts table name
    private static final String TABLE_ERRORS = "errors";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_SESSION_ID = "session_id";
    private static final String KEY_STEP_CHOSEN = "step_chosen";
    private static final String KEY_STEP_CORRECT = "step_correct";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ERRORS_TABLE = "CREATE TABLE " + TABLE_ERRORS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_SESSION_ID + " INTEGER,"
                + KEY_STEP_CHOSEN + " TEXT,"
                + KEY_STEP_CORRECT + " TEXT" + ")";
        db.execSQL(CREATE_ERRORS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ERRORS);

        // Create tables again
        onCreate(db);
    }

    public void addError(Mistake newErr) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SESSION_ID, newErr.sessionId);       // session id
        values.put(KEY_STEP_CHOSEN, newErr.stepChosen);     // chosen step
        values.put(KEY_STEP_CORRECT, newErr.stepCorrect);   // correct step

        // Inserting Row
        db.insert(TABLE_ERRORS, null, values);
        db.close(); // Closing database connection
    }

    // get error by id
    public Mistake getError(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ERRORS, new String[] { KEY_ID, KEY_SESSION_ID,
                        KEY_STEP_CHOSEN, KEY_STEP_CORRECT }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Mistake err = new Mistake(Integer.parseInt(cursor.getString(0)),
                Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));

        // return contact
        return err;
    }

    // Getting All Errors
    public int getSessionID() {
        int sessID = 0;
        String query = "SELECT MAX(session_id) FROM " + TABLE_ERRORS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            sessID = cursor.getInt(0) + 1;
        }

        if (sessID < 1) sessID = 1;
        return sessID;
    }

    public int getTotalNumErrors() {
        int total = 0;
        String query = "SELECT MAX(id) FROM " + TABLE_ERRORS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }

        return total;
    }

    public int getNumSessions() {
        return getSessionID()-1;
    }

    public int getMaxNumErrPerSession() {
        List<Integer> errors = new ArrayList<>();
        int max = 0;

        String query = "SELECT id FROM " + TABLE_ERRORS + " GROUP BY session_id";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                if (errors.size() == 0) {
                    errors.add(cursor.getInt(0));
                } else {
                    errors.add(cursor.getInt(0) - errors.get(errors.size() - 1));
                }

                if (max < errors.get(errors.size()-1)) max = errors.get(errors.size()-1);
            } while (cursor.moveToNext());
        }

        return max;
    }

    public List<String[]> getErrorFrequency() {
        // return a list of string tuples - step and its frequency
        List<String[]> data = new ArrayList<>();
        String[] tmpData;

        String query = "SELECT count(*), step_chosen FROM " + TABLE_ERRORS +
                " GROUP BY step_chosen ORDER BY count(*) DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                tmpData = new String[2];
                tmpData[0] = Integer.toString(cursor.getInt(0));
                tmpData[1] = cursor.getString(1).substring(1);

                data.add(tmpData);
            } while (cursor.moveToNext());
        }

        return data;
    }

    public double getAvgNumErrors() {
        List<Integer> errors = new ArrayList<>();
        int totalNumErrors = 0;

        String query = "SELECT id FROM " + TABLE_ERRORS + " GROUP BY session_id";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                if (errors.size() == 0) {
                    errors.add(cursor.getInt(0));
                } else
                    errors.add(cursor.getInt(0) - errors.get(errors.size() - 1));
            } while (cursor.moveToNext());
        }

        for (int h=0; h<errors.size(); h++) {
            totalNumErrors = totalNumErrors + errors.get(h);
        }

        return ((double)totalNumErrors/(double)errors.size());
    }


}