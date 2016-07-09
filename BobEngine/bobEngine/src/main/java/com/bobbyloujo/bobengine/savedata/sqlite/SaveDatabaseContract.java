package com.bobbyloujo.bobengine.savedata.sqlite;

/**
 * Created by bobby on 4/14/2016.
 */
public class SaveDatabaseContract {
    private SaveDatabaseContract(){};

    public class SaveTableSchema {
        public final static int VERSION = 1;
        public final static String TABLE_NAME = "SaveTableEntries";
        public final static String COLUMN_KEY = "key";            // The key used to obtain a value
        public final static String COLUMN_VALUE = "value";        // The value
        public final static String COLUMN_SAVE_NAME = "saveName"; // The table the entry belongs to
    }
}
