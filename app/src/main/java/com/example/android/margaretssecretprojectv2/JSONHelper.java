package com.example.android.margaretssecretprojectv2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class JSONHelper {
    public static final String TAG = "JSONHelper";
    private static final String FILE_NAME = "MargaretsSecretProjectv2/data.json";
    private static final int REQUEST_PERMISSION_WRITE = 1002;


    public static boolean exportToJSON(Activity activity, Context context, List<String> data) {
        Gson gson = new Gson();

        String jsonString = gson.toJson(data);
        Log.i(TAG, "export to Json" + jsonString);

        if (checkPermissions(activity, context)) {

        }
        FileOutputStream fileOutputStream = null;

        new File(Environment.getExternalStorageDirectory(), "MargaretsSecretProjectv2").mkdir();

        File file = new File(Environment.getExternalStorageDirectory(), FILE_NAME);
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(jsonString.getBytes());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public static List<String> importFromJSON(Activity activity, Context context) {
        FileReader fileReader = null;
        File file = new File(Environment.getExternalStorageDirectory(), FILE_NAME);

        try {
            fileReader = new FileReader(file);
            Gson gson = new Gson();
            List<String> datas = gson.fromJson(fileReader, ArrayList.class);
            return datas;

        } catch (FileNotFoundException e) {
            exportToJSON(activity, context, new ArrayList<String>());
            return importFromJSON(activity, context);

        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    public static void deleteJson() {
        File file = new File(Environment.getExternalStorageDirectory(), FILE_NAME);
        file.delete();
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    // Initiate request for permissions.
    private static boolean checkPermissions(Activity activity, Context context) {

        if (!isExternalStorageReadable() || !isExternalStorageWritable()) {
//            Toast.makeText(this, "This app only works on devices with usable external storage",
//                    Toast.LENGTH_SHORT).show();
            return false;
        }

        int permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_WRITE);
            return false;
        } else {
            return true;
        }
    }

    // Handle permissions result
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_PERMISSION_WRITE:
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    permissionGranted = true;
////                    Toast.makeText(context, "External storage permission granted",
////                            Toast.LENGTH_SHORT).show();
//                } else {
////                    Toast.makeText(context, "You must grant permission!", Toast.LENGTH_SHORT).show();
//                }
//                break;
//        }
//    }

}
