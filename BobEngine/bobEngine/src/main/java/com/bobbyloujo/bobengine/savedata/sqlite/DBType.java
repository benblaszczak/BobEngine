package com.bobbyloujo.bobengine.savedata.sqlite;

import android.database.Cursor;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteCursor;
import android.os.Build;

/**
 * Database type utility based on code by kassim (https://github.com/kassim).
 * Created by Benjamin on 4/15/2016.
 */
public class DBType {
    protected static final int FIELD_TYPE_BLOB = 4;
    protected static final int FIELD_TYPE_FLOAT = 2;
    protected static final int FIELD_TYPE_INTEGER = 1;
    protected static final int FIELD_TYPE_NULL = 0;
    protected static final int FIELD_TYPE_STRING = 3;

    public static int getType(Cursor cursor, int i) {
        SQLiteCursor sqLiteCursor = (SQLiteCursor) cursor;
        CursorWindow cursorWindow = sqLiteCursor.getWindow();
        int pos = cursor.getPosition();
        int type = -1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) { // Honeycomb or later.
            type = cursor.getType(i);

            if (type == Cursor.FIELD_TYPE_BLOB) {
                type = FIELD_TYPE_BLOB;
            } else if (type == Cursor.FIELD_TYPE_FLOAT) {
                type = FIELD_TYPE_FLOAT;
            } else if (type == Cursor.FIELD_TYPE_INTEGER) {
                type = FIELD_TYPE_INTEGER;
            } else if (type == Cursor.FIELD_TYPE_NULL) {
                type = FIELD_TYPE_NULL;
            } else if (type == Cursor.FIELD_TYPE_STRING) {
                type = FIELD_TYPE_STRING;
            }
        } else {                                           // Before Honeycomb
            if (cursorWindow.isNull(pos, i)) {
                type = FIELD_TYPE_NULL;
            } else if (cursorWindow.isLong(pos, i)) {
                type = FIELD_TYPE_INTEGER;
            } else if (cursorWindow.isFloat(pos, i)) {
                type = FIELD_TYPE_FLOAT;
            } else if (cursorWindow.isString(pos, i)) {
                type = FIELD_TYPE_STRING;
            } else if (cursorWindow.isBlob(pos, i)) {
                type = FIELD_TYPE_BLOB;
            }
        }

        return type;
    }
}
