package com.bobbyloujo.bobengine.savedata.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;

/**
 * This class works like a HashMap but the contents can be saved to a SQLite database.
 *
 * Created by Benjamin on 4/15/2016.
 */
public class SaveTable extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "SaveData.db";            // The name of the database file.

    private String name;                        // The table name
    private HashMap<String, Object> values;     // HashMap to store the values in the table

    /**
     * Creates a container for the save table with the given name.
     * @param name The name of the save table to load.
     */
    public SaveTable(Context context, String name) {
        super(context, DATABASE_NAME, null, SaveDatabaseContract.SaveTableSchema.VERSION);
        this.name = name;
        values = new HashMap<String, Object>();
    }

    /**
     * Loads the contents of the save table from the database.
     */
    public void load() {
        SQLiteDatabase db = getReadableDatabase();
        String[] allColumns = {SaveDatabaseContract.SaveTableSchema.COLUMN_KEY, SaveDatabaseContract.SaveTableSchema.COLUMN_VALUE, SaveDatabaseContract.SaveTableSchema.COLUMN_SAVE_NAME};
        String whereSaveNameIs = SaveDatabaseContract.SaveTableSchema.COLUMN_SAVE_NAME + " = ?";
        String[] saveName = {name};
        Cursor c = null;

        try {                               // Try to do the query
            c = db.query(SaveDatabaseContract.SaveTableSchema.TABLE_NAME, allColumns, whereSaveNameIs, saveName, null, null, null);
        } catch (SQLiteException e) {       // Query failed, probably because the table didn't exist. There is probably a better way to determine if it exists.
            Log.i("BobEngine", "Attempt to load save file from table \"" + SaveDatabaseContract.SaveTableSchema.TABLE_NAME + "\" failed. Table may not exist. Did you save the file?");
        }

        if (c != null && c.moveToFirst()) {

            do {
                String key = c.getString(c.getColumnIndexOrThrow(SaveDatabaseContract.SaveTableSchema.COLUMN_KEY));
                int valCol = c.getColumnIndexOrThrow(SaveDatabaseContract.SaveTableSchema.COLUMN_VALUE);
                int type = DBType.getType(c, valCol);

                if (type == DBType.FIELD_TYPE_INTEGER) {
                    Integer value = c.getInt(valCol);
                    values.put(key, value);
                }
                else if (type == DBType.FIELD_TYPE_FLOAT) {
                    Float value = c.getFloat(valCol);
                    values.put(key, value);
                }
                else if (type == DBType.FIELD_TYPE_STRING) {
                    String value = c.getString(valCol);
                    values.put(key, value);
                }

            } while (c.moveToNext());

            c.close();
        }

        db.close();
    }

    /**
     * Saves the content of the table to the database given.
     */
    public void save() {
        SQLiteDatabase db = getWritableDatabase();

        /* Create the table if necessary */
        String create = "CREATE TABLE IF NOT EXISTS ";
        create = create.concat(SaveDatabaseContract.SaveTableSchema.TABLE_NAME + " (");
        create = create.concat(SaveDatabaseContract.SaveTableSchema.COLUMN_KEY + " STRING, ");
        create = create.concat(SaveDatabaseContract.SaveTableSchema.COLUMN_VALUE + " BLOB, ");
        create = create.concat(SaveDatabaseContract.SaveTableSchema.COLUMN_SAVE_NAME + " TEXT");
        create = create.concat(") ");
        db.execSQL(create);

        /* Save content to the table. */
        ContentValues content = new ContentValues();
        Iterator<String> i = values.keySet().iterator();

        while (i.hasNext()) {
            String key = i.next();
            Object value = values.get(key);

            content.put(SaveDatabaseContract.SaveTableSchema.COLUMN_KEY, key);
            content.put(SaveDatabaseContract.SaveTableSchema.COLUMN_SAVE_NAME, name);

            if (value instanceof Integer) {
                content.put(SaveDatabaseContract.SaveTableSchema.COLUMN_VALUE, (Integer) value);
            }
            else if (value instanceof Float) {
                content.put(SaveDatabaseContract.SaveTableSchema.COLUMN_VALUE, (Float) value);
            }
            else if (value instanceof String) {
                content.put(SaveDatabaseContract.SaveTableSchema.COLUMN_VALUE, (String) value);
            }

            try {
                db.insertWithOnConflict(SaveDatabaseContract.SaveTableSchema.TABLE_NAME, null, content, SQLiteDatabase.CONFLICT_REPLACE);
            } catch (SQLiteException e) {
                Log.e("BobEngine", "Could not save values to the table \"" + SaveDatabaseContract.SaveTableSchema.TABLE_NAME + "\". Did the schema change?");
                e.printStackTrace();
            }
        }

        db.close();
    }

    /**
     * Deletes all entries for this entire SaveTable from the database. Can't be undone!
     */
    public void deleteTable() {
        SQLiteDatabase db = getWritableDatabase();
        String delete = "DELETE FROM " + SaveDatabaseContract.SaveTableSchema.TABLE_NAME;
        delete = delete.concat("WHERE " + SaveDatabaseContract.SaveTableSchema.COLUMN_SAVE_NAME + " = " + name);
        db.execSQL(delete);
    }

    /**
     * Use this to determine if the table exists in the database.
     * @return True if a table with this table's name exists in db, false otherwise.
     */
    public boolean exists() {
        SQLiteDatabase db = getReadableDatabase();
        boolean exists = false;
        String[] inSaveNameColumn = {SaveDatabaseContract.SaveTableSchema.COLUMN_SAVE_NAME};
        String whereSaveNameIs = SaveDatabaseContract.SaveTableSchema.COLUMN_SAVE_NAME + " = ?";
        String[] saveName = {name};
        Cursor c = null;

        try {
            c = db.query(SaveDatabaseContract.SaveTableSchema.TABLE_NAME, inSaveNameColumn, whereSaveNameIs, saveName, null, null, null);
        } catch (SQLiteException e) {
            // Table probably doesn't exist.
        }

        if (c != null && c.moveToFirst()) {
            exists = true;
            c.close();
        }

        db.close();
        return exists;
    }

    /**
     * Put a boolean value into this SaveTable. This doesn't save the value until you call save().
     * @param key The key used to identify the value.
     * @param value The value to store.
     */
    public void putBoolean(String key, boolean value) {
        Integer intVal = value ? 1 : 0; // if value is true, intVal is 1, else intVal is 0
        values.put(key, intVal);
    }

    /**
     * Put an integer value into this SaveTable. This doesn't save the value until you call save().
     * @param key The key used to identify the value.
     * @param value The value to store.
     */
    public void putInt(String key, int value) {
        values.put(key, value);
    }

    /**
     * Put a float value into this SaveTable. This doesn't save the value until you call save().
     * @param key The key used to identify the value.
     * @param value The value to store.
     */
    public void putFloat(String key, float value) {
        values.put(key, value);
    }

    /**
     * Put a String value into this SaveTable. This doesn't save the value until you call save().
     * @param key The key used to identify the value.
     * @param value The value to store.
     */
    public void putString(String key, String value) {
        values.put(key, value);
    }

    /**
     * Get a boolean value from this table.
     * @param key The key to identify the value.
     * @param defValue The value to return if the key is not found or is not the correct type.
     * @return The value associated with the key if the key exists and is the correct data type,
     *         defValue otherwise.
     */
    public boolean getBoolean(String key, boolean defValue) {
        Object value = values.get(key);
        if (value != null && value instanceof Integer && ((Integer) value == 0 || (Integer) value == 1)) {
            return (Integer) value == 1;
        } else {
            return defValue;
        }
    }

    /**
     * Get an integer value from this table.
     * @param key The key to identify the value.
     * @param defValue The value to return if the key is not found or is not the correct type.
     * @return The value associated with the key if the key exists and is the correct data type,
     *         defValue otherwise.
     */
    public int getInt(String key, int defValue) {
        Object value = values.get(key);
        if (value != null && value instanceof Integer) {
            return (Integer) value;
        } else {
            return defValue;
        }
    }

    /**
     * Get a float value from this table.
     * @param key The key to identify the value.
     * @param defValue The value to return if the key is not found or is not the correct type.
     * @return The value associated with the key if the key exists and is the correct data type,
     *         defValue otherwise.
     */
    public float getFloat(String key, float defValue) {
        Object value = values.get(key);
        if (value != null && value instanceof Float) {
            return (Float) value;
        } else {
            return defValue;
        }
    }

    /**
     * Get a String value from this table.
     * @param key The key to identify the value.
     * @param defValue The value to return if the key is not found or is not the correct type.
     * @return The value associated with the key if the key exists and is the correct data type,
     *         defValue otherwise.
     */
    public String getString(String key, String defValue) {
        Object value = values.get(key);
        if (value != null && value instanceof String) {
            return (String) value;
        } else {
            return defValue;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // todo whatever goes here
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // todo schema changed
    }
}
