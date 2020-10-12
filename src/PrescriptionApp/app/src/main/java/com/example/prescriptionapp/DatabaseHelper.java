package com.example.prescriptionapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

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

    public List<MedicationModel> selectAll(){
        List<MedicationModel> returnList = new ArrayList<>();
        String queryString = "SELECT * FROM " + MEDICATION_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()){
            do {
                int medicationID = cursor.getInt(0);
                String medicationName = cursor.getString(1);
                int medicationQuantity = cursor.getInt(2);
                boolean isTaken = cursor.getInt(3) == 1;

                MedicationModel model = new MedicationModel(medicationID, medicationName, medicationQuantity, isTaken);


            } while(cursor.moveToFirst());
        }
        cursor.close();
        db.close();
        return returnList;
    }

    private boolean isMedicationAdded(long insert) {
        return insert >= 0;
    }


}
