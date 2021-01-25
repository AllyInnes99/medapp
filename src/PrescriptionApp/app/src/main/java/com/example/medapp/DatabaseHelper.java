package com.example.medapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MedicationApp.db";
    public static final int DATABASE_VERSION = 1;

    // Const variables for the medication table
    public static final String MEDICATION_TABLE = "MEDICATION_TABLE";
    public static final String COL_MEDICATION_ID = "MEDICATION_ID";
    public static final String COL_MEDICATION_NAME = "MEDICATION_NAME";
    public static final String COL_QUANTITY = "QUANTITY";
    public static final String COL_DOSAGE = "DOSAGE";
    public static final String COL_MEASUREMENT = "MEASUREMENT";
    public static final String COL_TYPE = "TYPE";
    public static final String COL_PROFILE = "PROFILE";
    public static final String COL_DAYS_UNTIL_EMPTY = "DAYS_UNTIL_EMPTY";
    public static final String COL_AUTO_TAKE = "AUTO_TAKE";
    public static final String COL_REFILL_REQUESTED = "REFILL_REQUESTED";
    public static final String COL_CALENDAR_REFILL = "CALENDAR_REFILL";
    public static final String COL_CALENDAR_EMPTY = "CALENDAR_EMPTY";

    // Const variables for the application table
    public static final String DOSE_TABLE = "DOSE_TABLE";
    public static final String COL_DOSE_ID = "DOSE_ID";
    public static final String COL_TIME_HOUR = "TIME_HOUR";
    public static final String COL_TIME_MINUTE = "TIME_MINUTE";
    public static final String COL_DAY = "DAY";
    public static final String COL_AMOUNT = "AMOUNT";
    public static final String COL_TAKEN = "IS_TAKEN";
    public static final String COL_CALENDAR_ID = "CALENDAR_ID";

    Calendar calendar = Calendar.getInstance();
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createMedTableStatement = onCreateHelper(MEDICATION_TABLE) + " ("
                                        + COL_MEDICATION_ID + " INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT, "
                                        + COL_MEDICATION_NAME + " TEXT, " + COL_QUANTITY + " INT, "
                                        + COL_DOSAGE + " REAL, "
                                        + COL_MEASUREMENT + " TEXT, "
                                        + COL_TYPE + " TEXT, " + COL_PROFILE + " TEXT, "
                                        + COL_DAYS_UNTIL_EMPTY + " INT,"
                                        + COL_AUTO_TAKE + " BOOL, "
                                        + COL_REFILL_REQUESTED + " BOOL,"
                                        + COL_CALENDAR_REFILL + " STRING, "
                                        + COL_CALENDAR_EMPTY + " STRING)";

        String createAppTableStatement = onCreateHelper(DOSE_TABLE) + " ("
                                        + COL_DOSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + COL_MEDICATION_ID + " INT, "
                                        + COL_TIME_HOUR + " INT, "
                                        + COL_TIME_MINUTE + " INT, "
                                        + COL_DAY + " TEXT, "
                                        + COL_AMOUNT + " INT, "
                                        + COL_TAKEN + " BOOL, "
                                        + COL_CALENDAR_ID + " STRING, "
                                        + "FOREIGN KEY (" + COL_MEDICATION_ID + ") REFERENCES " + MEDICATION_TABLE + " ( " + COL_MEDICATION_ID + ") ON DELETE CASCADE)"
                                        ;

        db.execSQL(createMedTableStatement);
        db.execSQL(createAppTableStatement);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }


    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
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
     * @param medicationModel model to be added to the database
     * @return true if added successfully, false otherwise
     */
    public boolean addMedication(MedicationModel medicationModel) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MEDICATION_NAME, medicationModel.getName());
        cv.put(COL_QUANTITY, medicationModel.getQuantity());
        cv.put(COL_DOSAGE, medicationModel.getDosage());
        cv.put(COL_MEASUREMENT, medicationModel.getMeasurement());
        cv.put(COL_TYPE, medicationModel.getType());
        cv.put(COL_PROFILE, medicationModel.getProfile());
        cv.put(COL_DAYS_UNTIL_EMPTY, medicationModel.getDaysUntilEmpty());
        cv.put(COL_AUTO_TAKE, medicationModel.isAutoTake());
        cv.put(COL_REFILL_REQUESTED, medicationModel.isRefillRequested());
        cv.put(COL_CALENDAR_REFILL, medicationModel.getCalendarRefill());
        cv.put(COL_CALENDAR_EMPTY, medicationModel.getCalendarEmpty());
        long insert = db.insert(MEDICATION_TABLE, null, cv);
        return isAdded(insert);
    }

    /**
     * Method that adds a row to the application table from an application object
     * @param doseModel - object to be added as a row in db
     * @return true if added successfully, false otherwise
     */
    public boolean addDose(DoseModel doseModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        //Split time String into int hour and int min
        String[] time = doseModel.getTime().split(":");

        cv.put(COL_MEDICATION_ID, doseModel.getMedicationId());
        cv.put(COL_TIME_HOUR, Integer.parseInt(time[0]));
        cv.put(COL_TIME_MINUTE, Integer.parseInt(time[1]));
        cv.put(COL_DAY, doseModel.getDay());
        cv.put(COL_AMOUNT, doseModel.getAmount());
        cv.put(COL_TAKEN, doseModel.isTaken());
        cv.put(COL_CALENDAR_ID, doseModel.getCalendarID());
        long insert = db.insert(DOSE_TABLE, null, cv);
        return isAdded(insert);
    }

    /**
     * Method that performs a "select all" query on the medication table
     * @return List of every medication entry in the table
     */
    public List<MedicationModel> selectAllMedication(){
        String rawQuery = "SELECT * FROM " + MEDICATION_TABLE + " ORDER BY " + COL_MEDICATION_ID + " DESC";
        return selectMedicationHelper(rawQuery);
    }

    public MedicationModel selectMedicationFromID(int id) {
        String rawQuery = "SELECT * FROM " + MEDICATION_TABLE
                + " WHERE " + COL_MEDICATION_ID + " = " + id;
        return selectMedicationHelper(rawQuery).get(0);
    }

    public MedicationModel selectMedicationFromDose(DoseModel model) {
        String rawQuery = "SELECT * FROM " + MEDICATION_TABLE
                        + " WHERE " + COL_MEDICATION_ID + " = " + model.getMedicationId();
        return selectMedicationHelper(rawQuery).get(0);
    }

    /**
     * Helper method used to obtain list of medication objects from db
     * @param rawQuery - the query to be executed on db
     * @return - list of med objs representing rows in db matching query
     */
    private List<MedicationModel> selectMedicationHelper(String rawQuery) {
        List<MedicationModel> returnList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(rawQuery, null);

        if (cursor.moveToFirst()){
            do {
                int i = 0;
                int id = cursor.getInt(i++);
                String name = cursor.getString(i++);
                int qty = cursor.getInt(i++);
                double dosage = cursor.getDouble(i++);
                String measurement = cursor.getString(i++);
                String type = cursor.getString(i++);
                String profile = cursor.getString(i++);
                int daysUntilEmpty = cursor.getInt(i++);
                boolean autoTake = SQLiteIntToBool(cursor.getInt(i++));
                boolean refillRequested = SQLiteIntToBool(cursor.getInt(i++));
                String calendarRefill = cursor.getString(i++);
                String calendarEmpty = cursor.getString(i++);
                MedicationModel model = new MedicationModel(id, name, qty, daysUntilEmpty, type, dosage, measurement,
                                                            profile, autoTake, refillRequested, calendarRefill, calendarEmpty);
                returnList.add(model);

            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return returnList;
    }

    /**
     * Method that gets all of the doses that belong to a specific medication, by use of
     * the MedicationID foreign key
     * @param model - the medication model that we are trying to get the doses for
     * @return a list of doses models
     */
    public List<DoseModel> selectDoseFromMedication(MedicationModel model) {
        int medID = model.getMedicationId();
        String rawQuery = "SELECT * FROM " + DOSE_TABLE + " WHERE " + COL_MEDICATION_ID  + " = " + medID;
        return selectDoseHelper(rawQuery);
    }

    public List<MedicationModel> selectAutoTakenMeds() {
        String rawQuery = "SELECT * FROM " + MEDICATION_TABLE
                + " WHERE " + COL_AUTO_TAKE + " = 1";
        return selectMedicationHelper(rawQuery);
    }

    /*
     --- SELECT QUERIES FOR DOSE_TABLE ---
    */

    /**
     * Method that gets all of the applications set up in the database
     * @return a list of application models
     */
    public List<DoseModel> selectAllDoses() {
        String rawQuery = "SELECT * FROM " + DOSE_TABLE;
        return selectDoseHelper(rawQuery);
    }


    /**
     * Method that gets all the applications that are to be taken on a specific day
     * @param day - String denoting the day of the week
     * @return a list of applications to be taken on the given day
     */
    public List<DoseModel> selectDoseFromDay(String day) {
        String rawQuery = "SELECT * FROM " + DOSE_TABLE + " WHERE " + COL_DAY + " = " + day;
        return selectDoseHelper(rawQuery);
    }

    public List<DoseModel> selectDoseFromMedicationAndDay(MedicationModel model) {
        int medID = model.getMedicationId();

        String rawQuery = "SELECT * FROM " + DOSE_TABLE + " WHERE (" + COL_DAY + " = 'Monday'"
                            + " AND " + COL_MEDICATION_ID + " = " + medID + ")";
        return selectDoseHelper(rawQuery);
    }

    public DoseModel selectDoseFromID(int id) {
        String rawQuery = "SELECT * FROM " + DOSE_TABLE
                + " WHERE " + COL_DOSE_ID + " = " + id;
        return selectDoseHelper(rawQuery).get(0);
    }


    public List<DoseModel> selectTodaysDoseAndNotTaken() {

        // Get the day as a string
        String day = App.days.get(calendar.get(Calendar.DAY_OF_WEEK));

        // In query, we check taken == 0 as this is how false is represented in SQLite
        String rawQuery = "SELECT * FROM " + DOSE_TABLE +
                " WHERE (" + COL_DAY + " = '" + day +
                "' OR " + COL_DAY + " = 'Daily')"  +
                " AND (" + COL_TAKEN + " = 0 )";
        return selectDoseHelper(rawQuery);
    }

    /**
     * Helper method to obtain a list of application models via provided query
     * @param rawQuery - SQL query to be made on db
     * @return list of application models as a result of the query
     */
    private List<DoseModel> selectDoseHelper(String rawQuery) {
        List<DoseModel> returnList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(rawQuery, null);

        if(cursor.moveToFirst()) {
            do {
                int i = 0;
                int doseID = cursor.getInt(i++);
                int medID = cursor.getInt(i++);
                int timeHour = cursor.getInt(i++);
                int timeMinute = cursor.getInt(i++);

                String day = cursor.getString(i++);
                int amount = cursor.getInt(i++);
                boolean isTaken = SQLiteIntToBool(cursor.getInt(i++));
                String calID = cursor.getString(i++);

                DoseModel m = new DoseModel(doseID, medID, timeMinute, timeHour,
                                            day, amount, isTaken, calID);
                returnList.add(m);

            } while(cursor.moveToNext());
        }
        cursor.close();
        return returnList;
    }

    /**
     * Method that finds the target medication model in database, and if found it is deleted.
     * @param model - object that represents the medication that is to be removed.
     */
    public void deleteMedication(MedicationModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        //List<DoseModel> doseModels = selectDoseFromMedication(model);
        /*
        for(DoseModel appl: doseModels){
            deleteDose(appl);
        }
        */
        db.delete(MEDICATION_TABLE, COL_MEDICATION_ID + " = " + model.getMedicationId(), null);


    }
    /**
     * Method that finds the target application model in database, and if found it is deleted.
     * @param model - object that represents the application that is to be removed.
     * @return true if model is found and deleted successfully, false if not
     */
    public void deleteDose(DoseModel model){
        Toast.makeText(context, "Del", Toast.LENGTH_SHORT).show();
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DOSE_TABLE, COL_DOSE_ID + " = " + model.getDoseId(), null);
    }

    /**
     * Method that is called for when the medication is to be taken, updating the total quantity of
     * the medication according to the amount to be taken, and setting the application to be taken
     * @param doseModel - the application model mirroring the row to have isTaken set to true
     * @param medModel - the medication model mirroring the row to have quantity updated
     */
    public void takeMedication(DoseModel doseModel, MedicationModel medModel) {
        ContentValues cvMed = new ContentValues(1);
        ContentValues cvAppl = new ContentValues(1);

        int newQuantity = medModel.getQuantity() - doseModel.getAmount();
        cvMed.put(COL_QUANTITY, newQuantity);
        updateMedicationRow(medModel, cvMed);

        cvAppl.put(COL_TAKEN, true);
        updateDose(doseModel, cvAppl);
    }

    public void updateRefillID(MedicationModel medicationModel, String id) {
        ContentValues cv = new ContentValues(1);
        cv.put(COL_CALENDAR_REFILL, id);
        updateMedicationRow(medicationModel, cv);
    }

    public void updateEmptyID(MedicationModel medicationModel, String id) {
        ContentValues cv = new ContentValues(1);
        cv.put(COL_CALENDAR_EMPTY, id);
        updateMedicationRow(medicationModel, cv);
    }

    public void updateDoseCalendarID(DoseModel doseModel, String id) {
        ContentValues cv = new ContentValues();
        cv.put(COL_CALENDAR_ID, id);
        updateDose(doseModel, cv);
    }

    /**
     * Method that is called weekly that refreshes the every application in the db so that its
     * IS_TAKEN column is reset to false
     */
    public void refreshDoses(){
        ContentValues cv = new ContentValues();
        cv.put(COL_TAKEN, false);
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(DOSE_TABLE, cv, null, null);
        db.close();
    }

    public void refreshDailyDoses() {
        ContentValues cv = new ContentValues();
        cv.put(COL_TAKEN, false);
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(DOSE_TABLE, cv, COL_DAY + " = 'Daily'", null);
        db.close();
    }


    public void updateMedication(MedicationModel model) {
        ContentValues cv = new ContentValues();

        cv.put(COL_MEDICATION_NAME, model.getName());
        cv.put(COL_QUANTITY, model.getQuantity());
        cv.put(COL_DOSAGE, model.getDosage());
        cv.put(COL_MEASUREMENT, model.getMeasurement());
        cv.put(COL_TYPE, model.getType());
        cv.put(COL_PROFILE, model.getProfile());
        cv.put(COL_DAYS_UNTIL_EMPTY, model.getDaysUntilEmpty());
        updateMedicationRow(model, cv);
    }

    /**
     * Function that takes values to be updated in a row in MEDICATION_TABLE and makes the changes
     * @param medModel - the application to be targeted
     * @param cv - the values to be updated
     */
    public void updateMedicationRow(MedicationModel medModel, ContentValues cv) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(MEDICATION_TABLE, cv, COL_MEDICATION_ID + "= "
                + medModel.getMedicationId(), null);
        db.close();
    }

    /**
     * Function that takes values to be updated in a row in APPLICATION_TABLE and makes the changes
     * @param applModel - the application to be targeted
     * @param cv - the values to be updated
     */
    public void updateDose(DoseModel applModel, ContentValues cv) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(DOSE_TABLE, cv, COL_DOSE_ID + "= "
                    + applModel.getDoseId(), null);
        db.close();
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
        String queryString = "SELECT COUNT (*) FROM " + DOSE_TABLE
                                + "WHERE " + COL_MEDICATION_ID + " = " + model.getMedicationId();
        return countRowHelper(queryString);
    }

    private int countRowHelper(String queryString){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    /**
     * Function that calcs. the no. of days until a medication runs out of supply
     * @param model - the med to find out
     * @return
     */
    public int daysUntilEmpty(MedicationModel model) {
        Calendar c = Calendar.getInstance();
        List<DoseModel> doses = selectDoseFromMedication(model);
        Map<String, Integer> takenPerDay = new HashMap<>();
        takenPerDay.put("Monday", 0);
        takenPerDay.put("Tuesday", 0);
        takenPerDay.put("Wednesday", 0);
        takenPerDay.put("Thursday", 0);
        takenPerDay.put("Friday", 0);
        takenPerDay.put("Saturday", 0);
        takenPerDay.put("Sunday", 0);

        for(DoseModel doseModel: doses) {
            String d = doseModel.getDay();
            int count = doseModel.getAmount();

            if(d.equals("Daily")){
                for(String key: takenPerDay.keySet()){
                    int original = takenPerDay.get(key);
                    takenPerDay.put(key, original + count);
                }
            }
            else {
                int original = takenPerDay.get(d);
                takenPerDay.put(d, original + count);
            }
        }

        Map<Integer, Integer> m = new HashMap<>();
        m.put(Calendar.SUNDAY, mapFiller(takenPerDay, "Sunday"));
        m.put(Calendar.MONDAY, mapFiller(takenPerDay, "Monday"));
        m.put(Calendar.TUESDAY, mapFiller(takenPerDay, "Tuesday"));
        m.put(Calendar.WEDNESDAY, mapFiller(takenPerDay, "Wednesday"));
        m.put(Calendar.THURSDAY, mapFiller(takenPerDay, "Thursday"));
        m.put(Calendar.FRIDAY, mapFiller(takenPerDay, "Friday"));
        m.put(Calendar.SATURDAY, mapFiller(takenPerDay, "Saturday"));
        takenPerDay.clear();

        int current = model.getQuantity();
        int dayCount = 0;

        // from today's date, continuously subtract from the current qty for each day and then
        while(current > 0) {
            current -= m.get(c.get(Calendar.DAY_OF_WEEK));
            c.add(Calendar.DATE, 1);
            dayCount++;
        }
        return dayCount;
    }

    public void updateDaysUntilEmpty(MedicationModel medModel) {
        int days = daysUntilEmpty(medModel);
        ContentValues cv = new ContentValues();
        cv.put(COL_DAYS_UNTIL_EMPTY, days);
        updateMedicationRow(medModel, cv);
    }

    private int mapFiller(Map<String, Integer> takenMap, String target){
        if(takenMap.get(target) == null){
            return 0;
        }
        return takenMap.get(target);
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
