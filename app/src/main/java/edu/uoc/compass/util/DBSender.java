package edu.uoc.compass.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import edu.uoc.compass.R;

/**
 * Used to export data in CVS format.
 * Data is sent using external app (mail, dropbox, ...)
 * Data is exported in a zip file containing one CVS file for each database table
 * Zip file must be written in SD card before sending
 *
 * @author Antonio Ortega
 */
public class DBSender {
    /**
     * CVS file extension
     */
    private static final String CSV_EXTENSION = ".csv";
    /**
     * Temp directory where data is written
     */
    private static final String TEMP_DIRECTORY = "/CompassData";
    /**
     * Zip file name
     */
    private static final String ZIP_FILE = "compass_data.zip";
    /**
     * Data sending format
     */
    private static final String MAIL_TYPE = "text/html";
    /**
     * Buffer size used to export data temp files
     */
    private static final int BUFFER = 1024;

    /**
     * Sends zip file with exported data using ACTION_SEND Intent
     *
     * @param activity activity used to send data
     */
    public static void sendData(final Activity activity) {
        // Get database connection
        DBHelper dbHelper = new DBHelper(activity);
        // Access root directory
        File root   = Environment.getExternalStorageDirectory();
        // Create an array with CVS file names (one for each table)
        String filenames[] = new String[DBHelper.TABLES.length];

        // Check if write access granted
        if (!root.canWrite()) {
            showError(activity, activity.getString(R.string.error_saving_cvs));
            return;
        }

        // Create temp directory if doesn't exits
        String path = root.getAbsolutePath() + TEMP_DIRECTORY;
        File dir    =   new File (path);
        dir.mkdirs();

        // Create intent to send data
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getText(R.string.csv_send_subject));
        sendIntent.setType(MAIL_TYPE);

        // Create one CVS file to export each table data
        for (int i=0; i< DBHelper.TABLES.length; i++) {
            filenames[i] = DBHelper.TABLES[i] + CSV_EXTENSION;
            File file = createFile2Table(dbHelper, dir, filenames[i], DBHelper.TABLES[i]);
        }

        // Compress all CVS files in a zip file
        File zipFile = null;
        try {
            zipFile = zipFile(dir, filenames, ZIP_FILE);
        } catch (Exception e) {
            e.printStackTrace();
            showError(activity, activity.getString(R.string.error_saving_cvs));
            return;
        }

        // Adds zip file to intent data
        Uri uri = Uri.fromFile(zipFile);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);

        // At least one activity must be declared to send data
        PackageManager packageManager =  activity.getPackageManager();
        if (sendIntent.resolveActivity(packageManager) == null) {
            showError(activity, activity.getString(R.string.no_send_activity));
            return;
        } else {
            // Send data
            activity.startActivity(sendIntent);
        }
    }

    /**
     * Shows an error message
     * @param activity activity trying to send data
     * @param message error message
     */
    private static void showError(Activity activity, String message) {

        Util.showErrorMessage(
                activity,
                activity.getString(R.string.title_activity_save),
                message,
                null);
    }

    /**
     * Creates a zip file containing CVS files
     * @param dir directory where CVS files where created
     * @param fileNames array with CVS file names
     * @param zipFileName name of the zip file to create
     * @return File Object with created zip file
     * @throws IOException
     */
    private static File zipFile(File dir, String fileNames[], String zipFileName) throws IOException{
        // Create zip file
        File zipFile = new File(dir, zipFileName);
        // Input buffer to read CVS files
        BufferedInputStream origin;
        // Create output streams
        FileOutputStream dest = new FileOutputStream(zipFile);
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

        // All data is read in a buffer
        byte data[] = new byte[BUFFER];

        // Iterates CVS files
        for(int i=0; i < fileNames.length; i++) {
            // Obtain input file stream
            File OriginFile = new File(dir, fileNames[i]);
            FileInputStream fi = new FileInputStream(OriginFile);
            origin = new BufferedInputStream(fi, BUFFER);

            // Adds new entry to zip file
            ZipEntry entry = new ZipEntry(fileNames[i]);
            out.putNextEntry(entry);
            // Iterates input file reading data
            int count;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                // Writes data to zip entry
                out.write(data, 0, count);
            }
            origin.close();
        }

        out.close();
        return zipFile;
    }

    /**
     * Creates a CVS file with data from a database table
     * @param dbHelper database connection
     * @param dir directory to write CVS file
     * @param fileName name of CVS file
     * @param tableName name of database table to export
     * @return File object with exported CVS data
     */
    private static File createFile2Table(DBHelper dbHelper, File dir, String fileName, String tableName) {
        // Create output file and stream
        File file   =   new File(dir, fileName);
        FileOutputStream out   =   null;
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Export data
        try {
            dbHelper.tableToCSV(tableName, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }
}
