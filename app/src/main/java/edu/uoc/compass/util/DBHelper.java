package edu.uoc.compass.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.SensorEvent;
import android.text.TextUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import edu.uoc.compass.R;

/**
 * Created by aortega on 4/5/16.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE = "Compass.db";
    public static final String TABLE_PATH = "path";
    public static final String TABLE_PATH_DATA = "path_data";
    public static final String TABLE_GRID = "grid";
    public static final String TABLE_GRID_DATA = "grid_data";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_X = "x";
    public static final String COLUMN_Y = "y";
    public static final String COLUMN_Z = "z";
    public static final String COLUMN_SENSOR_TYPE = "sensor_type";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_ROWS = "rows";
    public static final String COLUMN_COLUMNS = "columns";
    public static final String COLUMN_ROW = "row";
    public static final String COLUMN_COLUMN = "column";
    public static final String COLUMN_SAMPLING_RATE = "sampling_rate";

    public static final String TABLES[] = {
            TABLE_PATH,
            TABLE_PATH_DATA,
            TABLE_GRID,
            TABLE_GRID_DATA
    };

    private static final String TYPE_INTEGER = "integer";
    private static final String TYPE_TEXT = "text";
    private static final String TYPE_FLOAT = "float";
    private static final String FLD_PRIMARY = "primary key";
    private static final String FLD_AUTO_INCREMENT = "autoincrement";

    public DBHelper(Context context) {
        super(context, DATABASE, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String pathFields[][] = {
                {COLUMN_ID, TYPE_INTEGER, FLD_PRIMARY, FLD_AUTO_INCREMENT},
                {COLUMN_NAME, TYPE_TEXT},
                {COLUMN_SAMPLING_RATE, TYPE_FLOAT},
        };
        String pathDataFields[][] = {
                {COLUMN_ID, TYPE_INTEGER},
                {COLUMN_SENSOR_TYPE, TYPE_INTEGER},
                {COLUMN_TIMESTAMP, TYPE_INTEGER},
                {COLUMN_X, TYPE_FLOAT},
                {COLUMN_Y, TYPE_FLOAT},
                {COLUMN_Z, TYPE_FLOAT},
        };
        String gridFields[][] = {
                {COLUMN_ID, TYPE_INTEGER, FLD_PRIMARY},
                {COLUMN_NAME, TYPE_TEXT},
                {COLUMN_ROWS, TYPE_INTEGER},
                {COLUMN_COLUMNS, TYPE_INTEGER},
                {COLUMN_SAMPLING_RATE, TYPE_FLOAT},
        };

        String gridDataFields[][] = {
                {COLUMN_ID, TYPE_INTEGER},
                {COLUMN_SENSOR_TYPE, TYPE_INTEGER},
                {COLUMN_TIMESTAMP, TYPE_INTEGER},
                {COLUMN_ROW, TYPE_INTEGER},
                {COLUMN_COLUMN, TYPE_INTEGER},
                {COLUMN_X, TYPE_FLOAT},
                {COLUMN_Y, TYPE_FLOAT},
                {COLUMN_Z, TYPE_FLOAT},
        };

        createTable(db, TABLE_PATH, pathFields);
        createTable(db, TABLE_PATH_DATA, pathDataFields);
        createTable(db, TABLE_GRID, gridFields);
        createTable(db, TABLE_GRID_DATA, gridDataFields);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTable(db, TABLE_PATH);
        dropTable(db, TABLE_PATH_DATA);
        dropTable(db, TABLE_GRID);
        dropTable(db, TABLE_GRID_DATA);

        onCreate(db);
    }

    public long insertPath(String name, float samplingRate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_SAMPLING_RATE, samplingRate);
        long ret = db.insert(TABLE_PATH, null, contentValues);
        return ret;
    }

    public long insertPathData(long id, int sensorType, float x, float y, float z, long timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, id);
        contentValues.put(COLUMN_SENSOR_TYPE, sensorType);
        contentValues.put(COLUMN_X, x);
        contentValues.put(COLUMN_Y, y);
        contentValues.put(COLUMN_Z, z);
        contentValues.put(COLUMN_TIMESTAMP, timestamp);
        return db.insert(TABLE_PATH_DATA, null, contentValues);
    }

    public long insertGrid(String name, int rows, int columns, float samplingRate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_COLUMNS, columns);
        contentValues.put(COLUMN_ROWS, rows);
        contentValues.put(COLUMN_SAMPLING_RATE, samplingRate);
        return db.insert(TABLE_GRID, null, contentValues);
    }

    public long insertGridData(long id, int sensorType, int row, int column, float x, float y, float z, long timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, id);
        contentValues.put(COLUMN_SENSOR_TYPE, sensorType);
        contentValues.put(COLUMN_X, x);
        contentValues.put(COLUMN_Y, y);
        contentValues.put(COLUMN_Z, z);
        contentValues.put(COLUMN_TIMESTAMP, timestamp);
        contentValues.put(COLUMN_ROW, row);
        contentValues.put(COLUMN_COLUMN, column);
        return db.insert(TABLE_GRID_DATA, null, contentValues);
    }

    public void deletePath(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String fields[] = new String[]{Long.toString(id)};
        db.delete(TABLE_PATH_DATA, COLUMN_ID + "=?", fields);
        db.delete(TABLE_PATH, COLUMN_ID + "=?", fields);
    }

    public void deleteGrid(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String fields[] = new String[]{Long.toString(id)};
        db.delete(TABLE_GRID_DATA, COLUMN_ID + "=?", fields);
        db.delete(TABLE_GRID, COLUMN_ID + "=?", fields);
    }

    public void deleteCell(long id, int row, int column) {
        SQLiteDatabase db = this.getWritableDatabase();
        String fields[] = new String[]{
                Long.toString(id),
                Integer.toString(row),
                Integer.toString(column)};
        db.delete(TABLE_GRID_DATA, COLUMN_ID + "=? AND " + COLUMN_ROW + "=? AND " + COLUMN_COLUMN + "=?", fields);
    }

    public int findPathByName(String name) {
        int ret = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(
                TABLE_PATH,
                new String[]{COLUMN_ID},
                COLUMN_NAME + "=?",
                new String[]{name},
                null,
                null,
                null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                ret = cursor.getInt(0);
            }
            cursor.close();
        }
        return ret;
    }

    public int findGridByName(String name) {
        int ret = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(
                TABLE_GRID,
                new String[]{COLUMN_ID},
                COLUMN_NAME + "=?",
                new String[]{name},
                null,
                null,
                null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                ret = cursor.getInt(0);
            }
            cursor.close();
        }
        return ret;
    }

    public ArrayList<Recording> getAllRecords() {
        ArrayList<Recording> ret = new ArrayList<Recording>();
        SQLiteDatabase db = this.getWritableDatabase();
        ret.addAll(getAllRecordings(db, TABLE_PATH, Recording.TYPE_PATH));
        ret.addAll(getAllRecordings(db, TABLE_GRID, Recording.TYPE_GRID));
        return ret;
    }

    private ArrayList<Recording> getAllRecordings(SQLiteDatabase db, String table, int type) {
        ArrayList<Recording> ret = new ArrayList<Recording>();
        Cursor cursor = db.query(
                table,
                new String[]{COLUMN_ID, COLUMN_NAME},
                null,
                null,
                null,
                null,
                COLUMN_ID);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(0);
                    String name = cursor.getString(1);
                    Recording recording = new Recording(type, id, name);
                    ret.add(recording);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return ret;
    }

    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PATH_DATA, null, null);
        db.delete(TABLE_PATH, null, null);
        db.delete(TABLE_GRID_DATA, null, null);
        db.delete(TABLE_GRID, null, null);
    }

    public void tableToCSV(String tableName, FileOutputStream out) throws IOException {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "select * from " + tableName;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            String columnNamesArray[] = cursor.getColumnNames();
            String columnNames = TextUtils.join(",", columnNamesArray) + "\n";
            out.write(columnNames.getBytes());
            if (cursor.moveToFirst()) {
                do {
                    String fieldsArray[] = new String[cursor.getColumnCount()];
                    for (int i = 0; i < fieldsArray.length; i++) {
                        fieldsArray[i] = cursor.getString(i);
                    }
                    String fields = TextUtils.join(",", fieldsArray) + "\n";
                    out.write(fields.getBytes());
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

    public void deleteRecording(Recording recording) {
        switch (recording.type) {
            case DBHelper.Recording.TYPE_PATH:
                deletePath(recording.id);
                break;
            case DBHelper.Recording.TYPE_GRID:
                deleteGrid(recording.id);
                break;
        }
    }

    private void createTable(SQLiteDatabase db, String tableName, String fields[][]) {
        String sql = "create table " + tableName + "(";
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                sql += ",";
            }
            for (int j = 0; j < fields[i].length; j++) {
                sql += fields[i][j] + " ";
            }
        }
        sql += ")";

        db.execSQL(sql);
    }

    private void dropTable(SQLiteDatabase db, String tableName) {
        String sql = "drop table if exists " + tableName;

        db.execSQL(sql);
    }

    public class Recording {
        public static final int TYPE_PATH = 1;
        public static final int TYPE_GRID = 2;
        public long id;
        public String name;
        public int type;

        public Recording(int type, long id, String name) {
            this.id = id;
            this.name = name;
            this.type = type;
        }

        public String getTypeName(Context context) {
            switch (type) {
                case TYPE_PATH:
                    return context.getString(R.string.path);
                case TYPE_GRID:
                    return context.getString(R.string.grid);
                default:
                    return "";
            }
        }
    }
}
