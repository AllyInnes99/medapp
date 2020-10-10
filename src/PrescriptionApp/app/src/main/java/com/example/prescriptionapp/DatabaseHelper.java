package com.example.prescriptionapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    public static final String DATABASE_NAME = "MedicationApp.db";
    public static final int DATABASE_VERSION = 1;

    public static final String MEDICATION_TABLE = "MEDICATION_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_MEDICATION_NAME = "MEDICATION_NAME";
    public static final String COLUMN_QUANTITY = "QUANTITY";
    public static final String COLUMN_IS_TAKEN = "IS_TAKEN";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableStatement = "CREATE TABLE " + MEDICATION_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                                                                + COLUMN_MEDICATION_NAME + " TEXT, "
                                                                                + COLUMN_QUANTITY + " INT, "
                                                                                + COLUMN_IS_TAKEN + " BOOL)";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addMedication(MedicationModel medicationModel) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MEDICATION_NAME, medicationModel.getName());
        cv.put(COLUMN_QUANTITY, medicationModel.getQuantity());
        cv.put(COLUMN_IS_TAKEN, medicationModel.isTaken());

        long insert = db.insert(MEDICATION_TABLE, null, cv);
        return isMedicationAdded(insert);
    }

    private boolean isMedicationAdded(long insert) {
        return insert != -1;
    }


}
