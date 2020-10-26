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
    public static final String COL_MEDICATION_ID = "MEDICATION_ID";
    public static final String COL_MEDICATION_NAME = "MEDICATION_NAME";
    public static final String COL_QUANTITY = "QUANTITY";
    public static final String COL_FREQUENCY = "FREQUENCY";
    public static final String COL_MEASUREMENT = "MEASUREMENT";
    public static final String COL_TYPE = "TYPE";
    public static final String COL_REFILL = "REFILL_AT";


    public static final String APPLICATION_TABLE = "APPLICATION_TABLE";
    public static final String COLUMN_APPLICATION_ID = "APPLICATION_ID";
    public static final String COLUMN_TIME_HOUR = "TIME_HOUR";
    public static final String COLUMN_TIME_MINUTE = "TIME_MINUTE";
    public static final String COLUMN_DOSAGE = "DOSAGE";
    public static final String COLUMN_DAY = "DAY";
    public static final String COLUMN_AMOUNT = "AMOUNT";
    public static final String COLUMN_TAKEN = "IS_TAKEN";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createMedTableStatement = onCreateHelper(MEDICATION_TABLE) + " ("
                                        + COL_MEDICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + COL_MEDICATION_NAME + " TEXT, "
                                        + COL_QUANTITY + " INT, "
                                        + COL_FREQUENCY + " TEXT,"
                                        + COL_MEASUREMENT + " TEXT,"
                                        + COL_TYPE + " TEXT,"
                                        + COL_REFILL + " INT)";

        String createAppTableStatement = onCreateHelper(APPLICATION_TABLE) + " ("
                                        + COLUMN_APPLICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + COL_MEDICATION_ID + " INT, "
                                        + COLUMN_TIME_HOUR + " INT, "
                                        + COLUMN_TIME_MINUTE + " INT, "
                                        + COLUMN_DOSAGE + " REAL, "
                                        + COLUMN_DAY + " TEXT, "
                                        + COLUMN_AMOUNT + " INT, "
                                        + COLUMN_TAKEN + " BOOL,"
                                        + "FOREIGN KEY (" + COL_MEDICATION_ID
                                        + ") REFERENCES " + MEDICATION_TABLE
                                        + "(" + COL_MEDICATION_ID + "))";

        db.execSQL(createMedTableStatement);
        db.execSQL(createAppTableStatement);
    }

    private String onCreateHelper(String tableName) {
        return "CREATE TABLE " + tableName;
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
        String queryString = "DELETE FROM " + MEDICATION_TABLE + "WHERE " + COL_MEDICATION_ID + " = " + model.getMedicationId();
        Cursor cursor = db.rawQuery(queryString, null);
        return cursor.moveToFirst();
    }


    /**
     * Method that adds a row to the medication table from medication object
     * @param medicationModel
     * @return true if added successfully, false otherwise
     */
    public boolean addMedication(MedicationModel medicationModel) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MEDICATION_NAME, medicationModel.getName());
        cv.put(COL_QUANTITY, medicationModel.getQuantity());
        cv.put(COL_FREQUENCY, medicationModel.getDayFrequency());
        cv.put(COL_MEASUREMENT, medicationModel.getMeasurement());
        cv.put(COL_TYPE, medicationModel.getType());
        cv.put(COL_REFILL, medicationModel.getRefillAt());

        long insert = db.insert(MEDICATION_TABLE, null, cv);
        return isMedicationAdded(insert);
    }

    /**
     * Method that performs a "select all" query on the medication table
     * @return List of every medication entry in the table
     */
    public List<MedicationModel> selectAllMedication(){
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
                //MedicationModel model = new MedicationModel(medicationID, medicationName, medicationQuantity, isTaken);
                //returnList.add(model);

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
