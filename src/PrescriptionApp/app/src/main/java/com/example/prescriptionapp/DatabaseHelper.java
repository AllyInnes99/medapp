package com.example.prescriptionapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    public static final String DATABASE_NAME = "MedicationApp.db";
    public static final int DATABASE_VERSION = 1;

    // Const variables for the medication table
    public static final String MEDICATION_TABLE = "MEDICATION_TABLE";
    public static final String COL_MEDICATION_ID = "MEDICATION_ID";
    public static final String COL_MEDICATION_NAME = "MEDICATION_NAME";
    public static final String COL_QUANTITY = "QUANTITY";
    public static final String COL_FREQUENCY = "FREQUENCY";
    public static final String COL_MEASUREMENT = "MEASUREMENT";
    public static final String COL_TYPE = "TYPE";
    public static final String COL_PROFILE = "PROFILE";
    public static final String COL_REFILL = "REFILL_AT";

    // Const variables for the application table
    public static final String APPLICATION_TABLE = "APPLICATION_TABLE";
    public static final String COL_APPLICATION_ID = "APPLICATION_ID";
    public static final String COL_TIME_HOUR = "TIME_HOUR";
    public static final String COL_TIME_MINUTE = "TIME_MINUTE";
    public static final String COL_DOSAGE = "DOSAGE";
    public static final String COL_DAY = "DAY";
    public static final String COL_AMOUNT = "AMOUNT";
    public static final String COL_TAKEN = "IS_TAKEN";

    Calendar calendar = Calendar.getInstance();
    List<String> days = Arrays.asList(
            new String[] {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"});

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createMedTableStatement = onCreateHelper(MEDICATION_TABLE) + " ("
                                        + COL_MEDICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + COL_MEDICATION_NAME + " TEXT, " + COL_QUANTITY + " INT, "
                                        + COL_FREQUENCY + " TEXT," + COL_MEASUREMENT + " TEXT, "
                                        + COL_TYPE + " TEXT, " + COL_PROFILE + " TEXT, "
                                        + COL_REFILL + " INT)";

        String createAppTableStatement = onCreateHelper(APPLICATION_TABLE) + " ("
                                        + COL_APPLICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + COL_MEDICATION_ID + " INT, " + COL_TIME_HOUR + " INT, "
                                        + COL_TIME_MINUTE + " INT, " + COL_DOSAGE + " REAL, "
                                        + COL_DAY + " TEXT, " + COL_AMOUNT + " INT, " + COL_TAKEN + " BOOL,"
                                        + "FOREIGN KEY (" + COL_MEDICATION_ID + ") REFERENCES " + MEDICATION_TABLE
                                        + "(" + COL_MEDICATION_ID + "))";

        db.execSQL(createMedTableStatement);
        db.execSQL(createAppTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }


    /**
     * Helper function that prepends "CREATE TABLE" to a table name to be used when creating tables
     * @param tableName - the table to be created
     * @return String with the create table prepending the table name
     */
    private String onCreateHelper(String tableName) {
        return "CREATE TABLE " + tableName;
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
        cv.put(COL_PROFILE, medicationModel.getProfile());
        cv.put(COL_REFILL, medicationModel.getRefillAt());
        long insert = db.insert(MEDICATION_TABLE, null, cv);
        return isAdded(insert);
    }

    /**
     * Method that adds a row to the application table from an application object
     * @param applModel - object to be added as a row in db
     * @return true if added successfully, false otherwise
     */
    public boolean addApplication(ApplicationModel applModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        //Split time String into int hour and int min
        String[] time = applModel.getTime().split(":");

        cv.put(COL_MEDICATION_ID, applModel.getMedicationId());
        cv.put(COL_TIME_HOUR, Integer.parseInt(time[0]));
        cv.put(COL_TIME_MINUTE, Integer.parseInt(time[1]));
        cv.put(COL_DOSAGE, applModel.getDosage());
        cv.put(COL_DAY, applModel.getDay());
        cv.put(COL_AMOUNT, applModel.getAmount());
        cv.put(COL_TAKEN, applModel.isTaken());
        long insert = db.insert(APPLICATION_TABLE, null, cv);
        return isAdded(insert);
    }

    /**
     * Method that performs a "select all" query on the medication table
     * @return List of every medication entry in the table
     */
    public List<MedicationModel> selectAllMedication(){
        String rawQuery = "SELECT * FROM " + MEDICATION_TABLE;
        return selectMedicationHelper(rawQuery);
    }

    private List<MedicationModel> selectMedicationHelper(String rawQuery) {
        List<MedicationModel> returnList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(rawQuery, null);

        if (cursor.moveToFirst()){
            do {
                int medicationID = cursor.getInt(0);
                String medicationName = cursor.getString(1);
                int medicationQuantity = cursor.getInt(2);
                String freq = cursor.getString(3);
                String measurement = cursor.getString(4);
                String type = cursor.getString(5);
                String profile = cursor.getString(6);
                int refill = cursor.getInt(7);
                MedicationModel model = new MedicationModel(medicationID, medicationName, medicationQuantity, refill,
                        freq,  measurement, type, profile);
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
     * Method that gets all of the applications that belong to a specific medication, by use of
     * the MedicationID foreign key
     * @param model - the medication model that we are trying to get the applications for
     * @return a list of application models
     */
    public List<ApplicationModel> selectApplicationFromMedication(MedicationModel model) {
        int medID = model.getMedicationId();
        String rawQuery = "SELECT * FROM " + APPLICATION_TABLE + " WHERE " + COL_MEDICATION_ID  + " = " + medID;
        return selectApplicationHelper(rawQuery);
    }

    /**
     * Method that gets all the applications that are to be taken on a specific day
     * @param day - String denoting the day of the week
     * @return a list of applications to be taken on the given day
     */
    public List<ApplicationModel> selectApplicationFromDay(String day) {
        String rawQuery = "SELECT * FROM " + APPLICATION_TABLE + " WHERE " + COL_DAY + " = " + day;
        return selectApplicationHelper(rawQuery);
    }

    public List<ApplicationModel> selectApplFromMedicationAndDay(MedicationModel model) {
        int medID = model.getMedicationId();

        String rawQuery = "SELECT * FROM " + APPLICATION_TABLE + " WHERE (" + COL_DAY + " = 'Monday'"
                            + " AND " + COL_MEDICATION_ID + " = " + medID + ")";
        return selectApplicationHelper(rawQuery);
    }

    public List<ApplicationModel> selectTodaysApplAndNotTaken() {

        // Get the day as a string
        String day = days.get(calendar.get(Calendar.DAY_OF_WEEK) + 1);

        // In query, we check taken == 0 as this is how false is represented in SQLite
        String rawQuery = "SELECT * FROM " + APPLICATION_TABLE + " WHERE (" + COL_DAY + " = '" + day +
                "' AND " + COL_TAKEN + " = 0 )";
        return selectApplicationHelper(rawQuery);
    }

    /**
     * Helper method to obtain a list of application models via provided query
     * @param rawQuery - SQL query to be made on db
     * @return list of application models as a result of the query
     */
    private List<ApplicationModel> selectApplicationHelper(String rawQuery) {
        List<ApplicationModel> returnList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(rawQuery, null);

        if(cursor.moveToFirst()) {
            do {
                int applicationID = cursor.getInt(0);
                int medID = cursor.getInt(1);
                int timeHour = cursor.getInt(2);
                int timeMinute = cursor.getInt(3);
                double dosage = cursor.getDouble(4);
                String day = cursor.getString(5);
                int amount = cursor.getInt(6);
                boolean isTaken = SQLiteIntToBool(cursor.getInt(7));

                ApplicationModel m = new ApplicationModel(applicationID, medID, timeMinute,
                        timeHour, dosage, day, amount, isTaken);
                returnList.add(m);

            } while(cursor.moveToNext());
        }
        return returnList;
    }

    /**
     * Method that finds the target medication model in database, and if found it is deleted.
     * @param model - object that represents the medication that is to be removed.
     * @return true if model is found and deleted successfully, false if not
     */
    public boolean deleteMedication(MedicationModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + MEDICATION_TABLE + "WHERE " + COL_MEDICATION_ID + " = " + model.getMedicationId();
        List<ApplicationModel> applicationModels = selectApplicationFromMedication(model);
        for(ApplicationModel appl: applicationModels){
            deleteApplication(appl);
        }
        Cursor cursor = db.rawQuery(queryString, null);
        return cursor.moveToFirst();
    }
    /**
     * Method that finds the target application model in database, and if found it is deleted.
     * @param model - object that represents the application that is to be removed.
     * @return true if model is found and deleted successfully, false if not
     */
    public boolean deleteApplication(ApplicationModel model){
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + APPLICATION_TABLE + "WHERE " + COL_APPLICATION_ID + " = " + model.getApplicationId();
        Cursor cursor = db.rawQuery(queryString, null);
        return cursor.moveToFirst();
    }

    /**
     * Method that returns the number of medications in the medication table
     * @return int that is the no. of rows in MEDICATION_TABLE
     */
    public int countMedication(){
        String queryString = "SELECT COUNT (*) FROM " + MEDICATION_TABLE;
        return countRowHelper(queryString);
    }

    public int countApplicationFromMed(MedicationModel model){
        String queryString = "SELECT COUNT (*) FROM " + APPLICATION_TABLE
                                + "WHERE " + COL_MEDICATION_ID + " = " + model.getMedicationId();
        return countRowHelper(queryString);
    }

    private int countRowHelper(String queryString){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }


    /**
     * SQLite databases represent boolean variable as integers, with 0 for false and 1 for true.
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
    private boolean isAdded(long insert) {
        return insert >= 0;
    }

}
