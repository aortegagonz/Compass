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
 * Created by aortega on 5/5/16.
 */
public class DBSender {
    private static final String CSV_EXTENSION = ".csv";
    private static final String TEMP_DIRECTORY = "/CompassData";
    private static final String ZIP_FILE = "compass_data.zip";
    private static final String MAIL_TYPE = "text/html";
    private static final int BUFFER = 1024;

    public static void sendData(final Activity activity) {
        final ProgressDialog progressDialog = ProgressDialog.show(
                activity,
                activity.getString(R.string.title_activity_save),
                activity.getString(R.string.wait_saving_data)
        );

        Thread thread = new Thread() {
            @Override
            public void run() {
                File root   = Environment.getExternalStorageDirectory();
                DBHelper dbHelper = new DBHelper(activity);
                String filenames[] = new String[DBHelper.TABLES.length];

                if (!root.canWrite()) {
                    progressDialog.dismiss();
                    showErrorFromThread(activity, activity.getString(R.string.error_saving_cvs));
                }
                String path = root.getAbsolutePath() + TEMP_DIRECTORY;
                File dir    =   new File (path);
                dir.mkdirs();

                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getText(R.string.csv_send_subject));
                sendIntent.setType(MAIL_TYPE);

                for (int i=0; i< DBHelper.TABLES.length; i++) {
                    filenames[i] = DBHelper.TABLES[i] + CSV_EXTENSION;
                    File file = createFile2Table(dbHelper, dir, filenames[i], DBHelper.TABLES[i]);
                }

                File zipFile = null;
                try {
                    zipFile = zipFile(dir, filenames, ZIP_FILE);
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    showErrorFromThread(activity, activity.getString(R.string.error_saving_cvs));

                }

                Uri uri = Uri.fromFile(zipFile);
                sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                PackageManager packageManager =  activity.getPackageManager();
                if (sendIntent.resolveActivity(packageManager) == null) {
                    progressDialog.dismiss();
                    showErrorFromThread(activity, activity.getString(R.string.no_send_activity));
                } else {
                    progressDialog.dismiss();
                    activity.startActivity(sendIntent);
                }
            }
        };
        thread.start();
    }

    private static void showErrorFromThread(final Activity activity, final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Util.showErrorMessage(
                        activity,
                        activity.getString(R.string.title_activity_save),
                        message,
                        null);
            }
        });
    }

    private static File zipFile(File dir, String fileNames[], String zipFileName) throws IOException{
        File zipFile = new File(dir, zipFileName);
        BufferedInputStream origin;
        FileOutputStream dest = new FileOutputStream(zipFile);
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

        byte data[] = new byte[BUFFER];

        for(int i=0; i < fileNames.length; i++) {
            File OriginFile = new File(dir, fileNames[i]);
            FileInputStream fi = new FileInputStream(OriginFile);
            origin = new BufferedInputStream(fi, BUFFER);
            ZipEntry entry = new ZipEntry(fileNames[i]);
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();
        }

        out.close();
        return zipFile;
    }

    private static File createFile2Table(DBHelper dbHelper, File dir, String fileName, String tableName) {
        File file   =   new File(dir, fileName);
        FileOutputStream out   =   null;
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
