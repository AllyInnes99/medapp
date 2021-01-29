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

    // Const variables for the medication table and its columns
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

    // Const variables for the application table and its columns
    public static final String DOSE_TABLE = "DOSE_TABLE";
    public static final String COL_DOSE_ID = "DOSE_ID";
    public static final String COL_TIME_HOUR = "TIME_HOUR";
    public static final String COL_TIME_MINUTE = "TIME_MINUTE";
    public static final String COL_DAY = "DAY";
    public static final String COL_AMOUNT = "AMOUNT";
    public static final String COL_TAKEN = "IS_TAKEN";
    public static final String COL_CALENDAR_ID = "CALENDAR_ID";

    // Const variables for the medLog and its columns
    public static final String LOG_TABLE = "LOG_TABLE";
    public static final String COL_LOG_ID = "LOG_ID";
    public static final String COL_LOG_MSG = "MESSAGE";
    public static final String COL_LOG_TIME = "TIME";
    public static final String COL_ON_TIME = "ON_TIME";

    Calendar calendar = Calendar.getInstance();
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createMedTableStatement
                = onCreateHelper(MEDICATION_TABLE) + " ("
                + COL_MEDICATION_ID + " INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT, "
                + COL_MEDICATION_NAME + " TEXT, " + COL_QUANTITY + " INT, "
                + COL_DOSAGE + " REAL, "
                + COL_MEASUREMENT + " TEXT, "
                + COL_TYPE + " TEXT, " + COL_PROFILE + " TEXT, "
                + COL_DAYS_UNTIL_EMPTY + " INT,"
                + COL_AUTO_TAKE + " BOOL, "
                + COL_REFILL_REQUESTED + " BOOL,"
                + COL_CALENDAR_REFILL + " TEXT, "
                + COL_CALENDAR_EMPTY + " TEXT)";

        String createAppTableStatement =
                onCreateHelper(DOSE_TABLE) + " ("
                + COL_DOSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_MEDICATION_ID + " INTEGER, "
                + COL_TIME_HOUR + " INTEGER, "
                + COL_TIME_MINUTE + " INTEGER, "
                + COL_DAY + " TEXT, "
                + COL_AMOUNT + " INTEGER, "
                + COL_TAKEN + " BOOL, "
                + COL_CALENDAR_ID + " TEXT, "
                + "FOREIGN KEY (" + COL_MEDICATION_ID + ") REFERENCES " + MEDICATION_TABLE + " ( " + COL_MEDICATION_ID + ") ON DELETE CASCADE)"
                ;

        String createLogTableStatement =
                onCreateHelper(LOG_TABLE) + " ("
                + COL_LOG_ID + " INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT,"
                + COL_MEDICATION_ID + " INTEGER,"
                + COL_AMOUNT + " INTEGER, "
                + COL_LOG_MSG + " TEXT, "
                + COL_LOG_TIME + " INTEGER, "
                + COL_TAKEN + " BOOL, "
                + COL_ON_TIME + " BOOL, "
                + "FOREIGN KEY (" + COL_MEDICATION_ID + ") REFERENCES " + MEDICATION_TABLE + " ( " + COL_MEDICATION_ID + ") ON DELETE CASCADE)";

        db.execSQL(createMedTableStatement);
        db.execSQL(createAppTableStatement);
        db.execSQL(createLogTableStatement);

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


    public boolean addLog(MedicationLog log) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_LOG_ID, log.getLogId());
        cv.put(COL_MEDICATION_ID, log.getMedicationId());
        cv.put(COL_AMOUNT, log.getAmount());
        cv.put(COL_LOG_MSG, log.getMsg());
        cv.put(COL_LOG_TIME, log.getTime());
        cv.put(COL_TAKEN, log.isTaken());
        cv.put(COL_ON_TIME, log.isOnTime());
        long insert = db.insert(LOG_TABLE, null, cv);
        return isAdded(insert);
    }

    /**
     * Method that performs a "select all" query on the medication table
     * @return List of every medication entry in the table
     */
    public List<MedicationModel> selectAllMedication(){
        String rawQuery = "SELECT * FROM " + MEDICATION_TABLE + " ORDER BY " + COL_MEDICATION_ID + " DESC";
        return executeMedicationQuery(rawQuery);
    }

    /**
     * Method that selects a medication object from the DB via a provided ID
     * @param id the ID of the targeted medication
     * @return the medication object with matching ID
     */
    public MedicationModel selectMedicationFromID(int id) {
        String rawQuery = "SELECT * FROM " + MEDICATION_TABLE
                + " WHERE " + COL_MEDICATION_ID + " = " + id;
        return executeMedicationQuery(rawQuery).get(0);
    }

    /**
     * Method that selects the medication that the provided dose object is for
     * @param doseModel the dose that we want to find the medication for
     * @return the medication object that the dose is for
     */
    public MedicationModel selectMedicationFromDose(DoseModel doseModel) {
        String rawQuery = "SELECT * FROM " + MEDICATION_TABLE
                        + " WHERE " + COL_MEDICATION_ID + " = " + doseModel.getMedicationId();
        return executeMedicationQuery(rawQuery).get(0);
    }

    /**
     * Helper method used to obtain list of medication objects from db
     * @param rawQuery - the query to be executed on db
     * @return - list of med objs representing rows in db matching query
     */
    private List<MedicationModel> executeMedicationQuery(String rawQuery) {
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
        return executeDoseQuery(rawQuery);
    }

    /**
     * Method that selects all of the medications that are listed as autotaken
     * @return list of medication objects that are all set as autotaken
     */
    public List<MedicationModel> selectAutoTakenMeds() {
        String rawQuery = "SELECT * FROM " + MEDICATION_TABLE
                + " WHERE " + COL_AUTO_TAKE + " = 1";
        return executeMedicationQuery(rawQuery);
    }

    /*
     --- SELECT QUERIES FOR DOSE_TABLE ---
    */

    /**
     * Method that gets all of the doses set up in the database
     * @return a list of dose models
     */
    public List<DoseModel> selectAllDoses() {
        String rawQuery = "SELECT * FROM " + DOSE_TABLE;
        return executeDoseQuery(rawQuery);
    }

    /**
     * Method that gets all the doses that are to be taken on a specific day
     * @param day - String denoting the day of the week
     * @return a list of dose to be taken on the given day
     */
    public List<DoseModel> selectDoseFromDay(String day) {
        String rawQuery = "SELECT * FROM " + DOSE_TABLE + " WHERE " + COL_DAY + " = " + day;
        return executeDoseQuery(rawQuery);
    }

    /**
     * Method that selects a dose object via a provided ID
     * @param id the ID of the dose we are looking for
     * @return Dose object that has been found
     */
    public DoseModel selectDoseFromID(int id) {
        String rawQuery = "SELECT * FROM " + DOSE_TABLE
                + " WHERE " + COL_DOSE_ID + " = " + id;
        return executeDoseQuery(rawQuery).get(0);
    }

    /**
     * Method that obtains the doses of medication that are to be taken today
     * @return list of dose objects that are to be taken today
     */
    public List<DoseModel> selectTodaysDoseAndNotTaken() {

        // Get the day as a string
        String day = App.days.get(calendar.get(Calendar.DAY_OF_WEEK));

        // In query, we check taken == 0 as this is how false is represented in SQLite
        String rawQuery = "SELECT * FROM " + DOSE_TABLE +
                " WHERE (" + COL_DAY + " = '" + day +
                "' OR " + COL_DAY + " = 'Daily')"  +
                " AND (" + COL_TAKEN + " = 0 )";
        return executeDoseQuery(rawQuery);
    }

    /**
     * Helper method to obtain a list of dose models via provided query
     * @param rawQuery - SQL query to be made on db
     * @return list of dose models as a result of the query
     */
    private List<DoseModel> executeDoseQuery(String rawQuery) {
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

    /*
        SELECT QUERIES FOR LOG TABLE
    */

    private List<MedicationLog> selectAllLogs() {
        String rawQuery = "SELECT * FROM " + LOG_TABLE;
        return executeLogQuery(rawQuery);
    }

    private List<MedicationLog> executeLogQuery(String rawQuery) {
        List<MedicationLog> returnList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(rawQuery, null);

        if(cursor.moveToFirst()) {
            do {
                int i = 0;
                int logId = cursor.getInt(i++);
                int medId = cursor.getInt(i++);
                String msg = cursor.getString(i++);
                int amount = cursor.getInt(i++);
                long time = cursor.getLong(i++);
                boolean taken = SQLiteIntToBool(cursor.getInt(i++));
                boolean onTime = SQLiteIntToBool(cursor.getInt(i++));


                MedicationLog medLog = new MedicationLog(logId, medId, msg, amount, time, taken, onTime);
                returnList.add(medLog);

            } while(cursor.moveToNext());
        }
        cursor.close();
        return returnList;
    }


    /**
     * Method that finds the target medication model in database, and if found it is deleted.
     * Due to the foreign key constraint, the doses of the medication are also deleted
     * @param medModel - object that represents the medication that is to be removed.
     */
    public void deleteMedication(MedicationModel medModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MEDICATION_TABLE, COL_MEDICATION_ID + " = " + medModel.getMedicationId(), null);
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

    public void takeMedication(DoseModel doseModel, MedicationModel medModel, boolean onTime) {
        takeMedication(doseModel, medModel);
        // add log report signifying if the medication was taken on time or not

        String msg;
        if(onTime) {
            msg = medModel.getName() + " taken on time.";
        }
        else {

            String actualTime = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
            msg = medModel.getName() + " meant to be taken at " + doseModel.getTime()
                + ". Actually taken at " + actualTime;
        }
        MedicationLog log = new MedicationLog(medModel.getMedicationId(), msg, doseModel.getAmount(),
                                            calendar.getTimeInMillis(), true, onTime);
        addLog(log);

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

        // reduce qty of medication
        int amount = doseModel.getAmount();
        int newQuantity = medModel.getQuantity() - amount;
        cvMed.put(COL_QUANTITY, newQuantity);
        updateMedicationRow(medModel, cvMed);

        // register dose as taken
        cvAppl.put(COL_TAKEN, true);
        updateDose(doseModel, cvAppl);
    }

    /**
     * Method that is called to update the Google Calendar ID for the empty event
     * for a given medication
     * @param medModel the medication to update the event ID for
     * @param id the new value of the ID for the event
     */
    public void updateRefillID(MedicationModel medModel, String id) {
        ContentValues cv = new ContentValues(1);
        cv.put(COL_CALENDAR_REFILL, id);
        updateMedicationRow(medModel, cv);
    }

    /**
     * Method that is called to update the Google Calendar ID for the empty event
     * for a given medication
     * @param medModel the medication to update the event ID for
     * @param id the new value of the ID for the event
     */
    public void updateEmptyID(MedicationModel medModel, String id) {
        ContentValues cv = new ContentValues(1);
        cv.put(COL_CALENDAR_EMPTY, id);
        updateMedicationRow(medModel, cv);
    }

    /**
     * Method that is called to update the Google Calendar ID for a given dose
     * taking event for a medication
     * @param doseModel the dose to update the event ID for
     * @param id the new value of the ID for the event
     */
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

    /**
     * Method that is called daily to refresh the doses that are to be taken daily so that
     * they can be taken each day
     */
    public void refreshDailyDoses() {
        Toast.makeText(context, "here", Toast.LENGTH_SHORT).show();
        ContentValues cv = new ContentValues();
        cv.put(COL_TAKEN, false);
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(DOSE_TABLE, cv, COL_DAY + " = 'Daily'", null);
        db.close();
    }

    /**
     * Method that is called to update a medication in the database
     * @param med the medication to be updated
     */
    public void updateMedication(MedicationModel med) {
        ContentValues cv = new ContentValues();
        cv.put(COL_MEDICATION_NAME, med.getName());
        cv.put(COL_QUANTITY, med.getQuantity());
        cv.put(COL_DOSAGE, med.getDosage());
        cv.put(COL_MEASUREMENT, med.getMeasurement());
        cv.put(COL_TYPE, med.getType());
        cv.put(COL_PROFILE, med.getProfile());
        cv.put(COL_DAYS_UNTIL_EMPTY, med.getDaysUntilEmpty());
        cv.put(COL_AUTO_TAKE, med.isAutoTake());
        cv.put(COL_REFILL_REQUESTED, med.isRefillRequested());
        updateMedicationRow(med, cv);
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
     * @param doseModel - the application to be targeted
     * @param cv - the values to be updated
     */
    public void updateDose(DoseModel doseModel, ContentValues cv) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(DOSE_TABLE, cv, COL_DOSE_ID + "= "
                    + doseModel.getDoseId(), null);
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

    /**
     * Method counts the no. of doses from a given medication
     * @param medModel medication that we want to count the no. of doses from
     * @return int that is the no. of doses for the given medication
     */
    public int countApplicationFromMed(MedicationModel medModel){
        String queryString = "SELECT COUNT (*) FROM " + DOSE_TABLE
                                + "WHERE " + COL_MEDICATION_ID + " = " + medModel.getMedicationId();
        return countRowHelper(queryString);
    }

    /**
     * Helper method that is used to perform count operations on the DB
     * @param queryString the query to be executed
     * @return the resulting int of the COUNT query
     */
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
     * @return the no. of days until the med is empty
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

    /**
     * Method that is called to update the no. of days until a medication is empty
     * @param medModel the medication we want to update the given field
     */
    public void updateDaysUntilEmpty(MedicationModel medModel) {
        int days = daysUntilEmpty(medModel);
        ContentValues cv = new ContentValues();
        cv.put(COL_DAYS_UNTIL_EMPTY, days);
        updateMedicationRow(medModel, cv);
    }

    /**
     * Helper function that is used to avoid NULL pointer errors
     * @param takenMap HashMap that is used
     * @param target the key we want to look up
     * @return 0 if get results in null, otherwise the value.
     */
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
