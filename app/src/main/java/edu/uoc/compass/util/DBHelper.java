package edu.uoc.compass.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import edu.uoc.compass.R;

/**
 * Used to manage operations with application database
 *
 * Database name: Compass.db
 * Tables: path, path_data, grid, grid_data
 *
 * Table path
 *	    INTEGER id (primary key, auto increment)
 *  	STRING name
 *  	FLOAT sampling_rate
 *
 * Table grid
 *  	INTEGER id (primary key, auto increment)
 *  	STRING name
 *  	INTEGER rows
 *  	INTEGER columns
 *  	FLOAT sampling_rate
 *
 * Table path_data
 *  	INTEGER id (primary key, auto increment)
 *  	INTEGER sensor_type
 *  	INTEGER timestamp
 *  	FLOAT x
 *  	FLOAT y
 *  	FLOAT z
 *
 * Table grid_data
 *  	INTEGER id (primary key, auto increment)
 *  	INTEGER sensor_type
 *  	INTEGER timestamp
 *  	INTEGER row
 *  	INTEGER column
 *  	FLOAT x
 *  	FLOAT y
 *  	FLOAT z
 *
 * @author Antonio Ortega
 */
public class DBHelper extends SQLiteOpenHelper {
    // Database name
    public static final String DATABASE = "Compass.db";
    // Table names
    public static final String TABLE_PATH = "path";
    public static final String TABLE_PATH_DATA = "path_data";
    public static final String TABLE_GRID = "grid";
    public static final String TABLE_GRID_DATA = "grid_data";
    // Table column names
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

    // Array of tables
    public static final String TABLES[] = {
            TABLE_PATH,
            TABLE_PATH_DATA,
            TABLE_GRID,
            TABLE_GRID_DATA
    };

    // Column data types and restrictions
    private static final String TYPE_INTEGER = "integer";
    private static final String TYPE_TEXT = "text";
    private static final String TYPE_FLOAT = "float";
    private static final String FLD_PRIMARY = "primary key";
    private static final String FLD_AUTO_INCREMENT = "autoincrement";

    /**
     * Constructor
     * @param context Activity context
     */
    public DBHelper(Context context) {
        super(context, DATABASE, null, 2);
    }

    @Override

    public void onCreate(SQLiteDatabase db) {
        // Define arrays for each table containing field name, field type and extra information
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

        // Create each table
        createTable(db, TABLE_PATH, pathFields);
        createTable(db, TABLE_PATH_DATA, pathDataFields);
        createTable(db, TABLE_GRID, gridFields);
        createTable(db, TABLE_GRID_DATA, gridDataFields);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop all tables
        dropTable(db, TABLE_PATH);
        dropTable(db, TABLE_PATH_DATA);
        dropTable(db, TABLE_GRID);
        dropTable(db, TABLE_GRID_DATA);

        // Create tables with new version
        onCreate(db);
    }

    /**
     * Inserts a new record into path table.
     * Id value is not needed because it is an autoincrement field
     *
     * @param name  name of path
     * @param samplingRate  sampling rate used to record path
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long insertPath(String name, float samplingRate) {
        // Get database connection
        SQLiteDatabase db = this.getWritableDatabase();

        // Set field values
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_SAMPLING_RATE, samplingRate);

        // Insert record
        return db.insert(TABLE_PATH, null, contentValues);
    }

    /**
     * Inserts a new record into path_data table
     *
     * @param id foreign key in path table
     * @param sensorType sensor used to sample vector
     * @param x x component of vector
     * @param y y component of vector
     * @param z z component of vector
     * @param timestamp time in milliseconds when data was sampled
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long insertPathData(long id, int sensorType, float x, float y, float z, long timestamp) {
        // Get database connection
        SQLiteDatabase db = this.getWritableDatabase();

        // Set field values
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, id);
        contentValues.put(COLUMN_SENSOR_TYPE, sensorType);
        contentValues.put(COLUMN_X, x);
        contentValues.put(COLUMN_Y, y);
        contentValues.put(COLUMN_Z, z);
        contentValues.put(COLUMN_TIMESTAMP, timestamp);

        // Insert record
        return db.insert(TABLE_PATH_DATA, null, contentValues);
    }

    /**
     * Inserts a new record into grid table
     * Id value is not needed because it is an autoincrement field
     *
     * @param name name of grid
     * @param rows number of rows in the grid
     * @param columns number of columns in the grid
     * @param samplingRate  sampling rate used to record each cell
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long insertGrid(String name, int rows, int columns, float samplingRate) {
        // Get database connection
        SQLiteDatabase db = this.getWritableDatabase();

        // Set field values
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_COLUMNS, columns);
        contentValues.put(COLUMN_ROWS, rows);
        contentValues.put(COLUMN_SAMPLING_RATE, samplingRate);

        // Insert row
        return db.insert(TABLE_GRID, null, contentValues);
    }

    /**
     * Inserts a new record into grid_data table
     *
     * @param id foreign key in grid table
     * @param sensorType sensor used to sample vector
     * @param row row of grid
     * @param column column of grid
     * @param x x component of vector
     * @param y y component of vector
     * @param z z component of vector
     * @param timestamp time in milliseconds when data was sampled
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long insertGridData(long id, int sensorType, int row, int column, float x, float y, float z, long timestamp) {
        // Get database connection
        SQLiteDatabase db = this.getWritableDatabase();

        // Set field values
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, id);
        contentValues.put(COLUMN_SENSOR_TYPE, sensorType);
        contentValues.put(COLUMN_X, x);
        contentValues.put(COLUMN_Y, y);
        contentValues.put(COLUMN_Z, z);
        contentValues.put(COLUMN_TIMESTAMP, timestamp);
        contentValues.put(COLUMN_ROW, row);
        contentValues.put(COLUMN_COLUMN, column);

        // Insert row
        return db.insert(TABLE_GRID_DATA, null, contentValues);
    }

    /**
     * Delete a record from path table. Records from path_data are also deleted using foreign key
     *
     * @param id primary key in path table
     */
    public void deletePath(long id) {
        // Get database connection
        SQLiteDatabase db = this.getWritableDatabase();
        String fields[] = new String[]{Long.toString(id)};

        // Delete from path_data table
        db.delete(TABLE_PATH_DATA, COLUMN_ID + "=?", fields);
        // Delete from path table
        db.delete(TABLE_PATH, COLUMN_ID + "=?", fields);
    }

    /**
     * Delete a record from grid table. Records from grid_data are also deleted using foreign key
     *
     * @param id primary key in grid table
     */
    public void deleteGrid(long id) {
        // Get database connection
        SQLiteDatabase db = this.getWritableDatabase();

        // Define array with fields of filter
        String fields[] = new String[]{Long.toString(id)};

        // Delete from grid_data table
        db.delete(TABLE_GRID_DATA, COLUMN_ID + "=?", fields);
        // Delete from grid table
        db.delete(TABLE_GRID, COLUMN_ID + "=?", fields);
    }

    /**
     * Delete all sampled data in a cell of a grid
     * @param id grid id
     * @param row row number
     * @param column column number
     */
    public void deleteCell(long id, int row, int column) {
        // Get database connection
        SQLiteDatabase db = this.getWritableDatabase();
        // Define array with fields of filter
        String fields[] = new String[]{
                Long.toString(id),
                Integer.toString(row),
                Integer.toString(column)};
        // Delete from grid_data table
        db.delete(TABLE_GRID_DATA, COLUMN_ID + "=? AND " + COLUMN_ROW + "=? AND " + COLUMN_COLUMN + "=?", fields);
    }

    /**
     * Finds the path with de specified name
     * @param name path name
     * @return path id or -1 if not found
     */
    public int findPathByName(String name) {
        // Default return value
        int ret = -1;
        // Get database connection
        SQLiteDatabase db = this.getWritableDatabase();
        // Constructs cursor
        Cursor cursor = db.query(
                TABLE_PATH,
                new String[]{COLUMN_ID},
                COLUMN_NAME + "=?",
                new String[]{name},
                null,
                null,
                null);
        // Access first record in cursor
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // Get path id
                ret = cursor.getInt(0);
            }
            cursor.close();
        }
        return ret;
    }

    /**
     * Finds the grid with de specified name
     * @param name grid name
     * @return grid id or -1 if not found
     */
    public int findGridByName(String name) {
        // Default return value
        int ret = -1;
        // Get database connection
        SQLiteDatabase db = this.getWritableDatabase();
        // Constructs cursor
        Cursor cursor = db.query(
                TABLE_GRID,
                new String[]{COLUMN_ID},
                COLUMN_NAME + "=?",
                new String[]{name},
                null,
                null,
                null);
        // Access first record in cursor
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // Get grid id
                ret = cursor.getInt(0);
            }
            cursor.close();
        }
        return ret;
    }

    /**
     * Get a list of all paths and grids from the database
     *
     * @return An ArrayList with a Recordings in the database
     */
    public ArrayList<Recording> getAllRecords() {
        // Create empty ArrayList
        ArrayList<Recording> ret = new ArrayList<Recording>();
        // Get database connection
        SQLiteDatabase db = this.getWritableDatabase();
        // Insert all Paths into ArrayList
        ret.addAll(getAllRecordings(db, TABLE_PATH, Recording.TYPE_PATH));
        // Insert all Grids into ArrayList
        ret.addAll(getAllRecordings(db, TABLE_GRID, Recording.TYPE_GRID));
        return ret;
    }

    /**
     * Get a list of all paths or grids from the database
     *
     * @param db database connection
     * @param table name of table (path or grid)
     * @param type type of recording (@see Recording class)
     * @return An ArrayList with a Recording objects of selected type
     */
    private ArrayList<Recording> getAllRecordings(SQLiteDatabase db, String table, int type) {
        // Get database connection
        ArrayList<Recording> ret = new ArrayList<Recording>();
        // Construct cursor
        Cursor cursor = db.query(
                table,
                new String[]{COLUMN_ID, COLUMN_NAME},
                null,
                null,
                null,
                null,
                COLUMN_ID);

        // Iterate cursor to obtain records
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

    /**
     * Delete all records from all tables
     */
    public void deleteAllData() {
        // Get database connection
        SQLiteDatabase db = this.getWritableDatabase();

        // Execute delete from each table
        db.delete(TABLE_PATH_DATA, null, null);
        db.delete(TABLE_PATH, null, null);
        db.delete(TABLE_GRID_DATA, null, null);
        db.delete(TABLE_GRID, null, null);
    }

    /**
     * Sends table data in CVS format to a FileOutputStream
     *
     * @param tableName Name of the table to fetch data
     * @param out FileOutputStream where data is written
     * @throws IOException
     */
    public void tableToCSV(String tableName, FileOutputStream out) throws IOException {
        // Get database connection
        SQLiteDatabase db = this.getWritableDatabase();

        // Construct cursor
        String sql = "select * from " + tableName;
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor != null) {
            // Write header with column names
            String columnNamesArray[] = cursor.getColumnNames();
            String columnNames = TextUtils.join(",", columnNamesArray) + "\n";
            out.write(columnNames.getBytes());

            // Iterate cursor to obtain each table record
            if (cursor.moveToFirst()) {
                do {
                    // Iterate columns to fetch each value
                    String fieldsArray[] = new String[cursor.getColumnCount()];
                    for (int i = 0; i < fieldsArray.length; i++) {
                        fieldsArray[i] = cursor.getString(i);
                    }
                    // Write values in CVS format
                    String fields = TextUtils.join(",", fieldsArray) + "\n";
                    out.write(fields.getBytes());
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

    /**
     * Delete a path or grid form database
     *
     * @param recording Recording object containing Recording type (path or grid) and record id
     */
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

    /**
     * Creates a database table
     *
     * @param db database connection
     * @param tableName name of table to create
     * @param fields array with fields (type, name, extra info)
     */
    private void createTable(SQLiteDatabase db, String tableName, String fields[][]) {
        // Construct SQL query
        String sql = "create table " + tableName + "(";

        // Iterate fields array to construct query
        for (int i = 0; i < fields.length; i++) {
            // First field doesn't need a preceding comma
            if (i > 0) {
                sql += ",";
            }
            // Iterates each field data
            // 0 => field type
            // 1 => field name
            // 2 => extra data (e.g. primary key)
            for (int j = 0; j < fields[i].length; j++) {
                sql += fields[i][j] + " ";
            }
        }
        sql += ")";

        // Execute sql sentence
        db.execSQL(sql);
    }

    /**
     * Deletes a table from database
     *
     * @param db database connection
     * @param tableName table name
     */
    private void dropTable(SQLiteDatabase db, String tableName) {
        String sql = "drop table if exists " + tableName;
        db.execSQL(sql);
    }

    /**
     * Internal class to store information of recording. A recording can be a path or a record
     */
    public class Recording {
        //  Record type constants
        public static final int TYPE_PATH = 1;
        public static final int TYPE_GRID = 2;

        /**
         * Path or grid primary key
         */
        public long id;

        /**
         * Path or grid name
         */
        public String name;

        /**
         * Specifies if recording is a path or a grid
         */
        public int type;

        /**
         * Constructor
         *
         * @param type 1 => path, 2 => grid
         * @param id path or grid ID
         * @param name path or grid ID
         */
        public Recording(int type, long id, String name) {
            this.id = id;
            this.name = name;
            this.type = type;
        }

        /**
         * Gets a textual description of record type
         *
         * @param context Activity context used to access String resources
         * @return String with record type description or empty string if error
         */
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
