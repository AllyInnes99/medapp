package com.example.prescriptionapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
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

    /**
     * Method that finds the target medication model in database, and if found it is deleted.
     * @param model - object that represents the medication that is to be removed.
     * @return true if model is found and deleted successfully, false if not
     */
    public boolean deleteMedication(MedicationModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + MEDICATION_TABLE + "WHERE " + COLUMN_ID + " = " + model.getId();
        Cursor cursor = db.rawQuery(queryString, null);
        return cursor.moveToFirst();
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
        String rawQuery = "SELECT * FROM " + MEDICATION_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(rawQuery, null);

        if (cursor.moveToFirst()){
            do {
                int medicationID = cursor.getInt(0);
                String medicationName = cursor.getString(1);
                int medicationQuantity = cursor.getInt(2);
                boolean isTaken = SQLiteIntToBool(cursor.getInt(3));
                MedicationModel model = new MedicationModel(medicationID, medicationName, medicationQuantity, isTaken);
                returnList.add(model);

            } while(cursor.moveToNext());
        }
        else {
        }
        cursor.close();
        db.close();
        return returnList;
    }

    /**
     * SQLite databases represent boolean variable as integers, with 0 for false and 1 for true
     * To convert back into boolean for the model, check for equality with 1
     * @param b - the integer representing the boolean
     * @return true or false, depending on if b is 1 or 0
     */
    private boolean SQLiteIntToBool(int b) {
        return b == 1;
    }

    /**
     * When using the insert method on a SQLiteDatabase instance, a long value is returned that
     * denotes if the data was added to the db successfully. If the value returned is negative,
     * that therefore means that the data has not been inserted properly
     * @param insert - long value that represents the success
     * @return false if negative, true otherwise
     */
    private boolean isMedicationAdded(long insert) {
        return insert >= 0;
    }


}
