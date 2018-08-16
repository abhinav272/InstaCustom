package com.abhinav.instacropperpicker;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    private static final String PREFIX = "dogood";
    private static final String STORAGE_PARENT_DIRECTORY = "/storage/emulated/0/DOgood/";
    private static final String VIDEO_EXT = "dgVideo";
    private static final String IMAGE_EXT = "dgImage";
    private static FileUtils fileUtils;

    private FileUtils() {
    }

    public static synchronized void init() {
        if (fileUtils == null) {
            fileUtils = new FileUtils();
        }
    }

    public static FileUtils getInstance() {
        if (fileUtils == null)
            throw new IllegalStateException("must call init() before calling getInstance()");
        return fileUtils;
    }

    public static File getNextVideoFile() {
        File videoDir = getVideoDir();
        String fileExt = ".mp4";
        String fileName = String.valueOf(System.currentTimeMillis());
        return new File(videoDir, fileName + fileExt);
    }

    public static File getNextImageFile() {
        File videoDir = getImageDir();
        String fileExt = ".png";
        String fileName = String.valueOf(System.currentTimeMillis());
        return new File(videoDir, fileName + fileExt);
    }

    public static File getVideoDir() {
        File filesDir = new File(STORAGE_PARENT_DIRECTORY);
        File audioDir = new File(filesDir, VIDEO_EXT);
        if (!audioDir.exists()) {
            audioDir.mkdirs();
        }
        return audioDir;
    }

    public static File getImageDir() {
//        File filesDir = context.getFilesDir();
        File filesDir = new File(STORAGE_PARENT_DIRECTORY);
        File audioDir = new File(filesDir, IMAGE_EXT);
        if (!audioDir.exists()) {
            audioDir.mkdirs();
        }
        return audioDir;
    }

//    public static String getRealPathFromURI(Uri contentUri) {
//        String[] proj = {MediaStore.Images.Media.DATA};
//        Cursor cursor = App.getContext().getContentResolver().query(contentUri, proj,
//                null, null, null);
//        int column_index = cursor
//                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }

    private static boolean isNewGooglePhotosUri(Uri uri) {
        return uri.getAuthority().startsWith("com.google.android.apps");
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor;
        String filePath = "";
        if (contentUri == null)
            return filePath;
        if (contentUri.getAuthority() != null) {
            if (isNewGooglePhotosUri(contentUri)) {
                return contentUri.getLastPathSegment();
            }
            if (!TextUtils.isEmpty(filePath))
                return filePath;
        }
        File file = new File(contentUri.getPath());
        if (file.exists())
            filePath = file.getPath();
        if (!TextUtils.isEmpty(filePath))
            return filePath;
        String[] proj = {MediaStore.Images.Media.DATA};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                String wholeID = DocumentsContract.getDocumentId(contentUri);
                // Split at colon, use second item in the array
//                String[] split = wholeID.split(":");
                String id;
                if (wholeID.contains(":"))
                    id = wholeID.split(":")[1];

                else id = wholeID;
//                if (split.length > 1)
//                    id = split[1];
//                else id = wholeID;
                // where id is equal to
                cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, MediaStore.Images.Media._ID + "='" + id + "'", null, null);
                if (cursor != null) {
                    int columnIndex = cursor.getColumnIndex(proj[0]);
                    if (cursor.moveToFirst())
                        filePath = cursor.getString(columnIndex);
                    if (!TextUtils.isEmpty(filePath))
                        contentUri = Uri.parse(filePath);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(filePath))
            return filePath;
        try {
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor == null)
                return contentUri.getPath();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst())
                filePath = cursor.getString(column_index);
            if (!cursor.isClosed())
                cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            filePath = contentUri.getPath();
        }
        if (filePath == null)
            filePath = "";
        return filePath;
    }

    public static String getRealPathFromVideoURI(Context context, Uri contentUri) {
        Cursor cursor;
        String filePath = "";
        String[] proj = {MediaStore.Video.Media.DATA};
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            try {
                String wholeID = DocumentsContract.getDocumentId(contentUri);
                String id;
                // Split at colon, use second item in the array
                if (wholeID.contains(":"))
                    id = wholeID.split(":")[1];

                else id = wholeID;
                // where id is equal to
                cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, MediaStore.Video.Media._ID + "='" + id + "'", null, null);
                int columnIndex = cursor.getColumnIndex(proj[0]);
                if (cursor.moveToFirst())
                    filePath = cursor.getString(columnIndex);
                if (!TextUtils.isEmpty(filePath))
                    contentUri = Uri.parse(filePath);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        try {
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor == null)
                return contentUri.getPath();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            if (cursor.moveToFirst())
                filePath = cursor.getString(column_index);
            if (!cursor.isClosed())
                cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            filePath = contentUri.getPath();
        }
        if (filePath == null)
            filePath = "";
        return filePath;
    }
}
