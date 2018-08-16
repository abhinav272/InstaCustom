package com.abhinav.instacropperpicker.repo;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

import com.abhinav.instacropperpicker.R;
import com.abhinav.instacropperpicker.bean.BucketBean;
import com.abhinav.instacropperpicker.bean.MediaBean;
import com.yalantis.ucrop.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class GalleryRepository {

    private MutableLiveData<List<MediaBean>> medialist;
    private MutableLiveData<List<BucketBean>> albumsArray;
    private Handler handler;
    private ContentResolver contentResolver;
    private Application application;

    private String[] projection = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.TITLE
    };


    private Uri queryUri = MediaStore.Files.getContentUri("external");

    private String selection = MediaStore.Images.Media.BUCKET_ID + " =? OR " + MediaStore.Video.Media.BUCKET_ID + " =? ";

    public static GalleryRepository getInstance(Application application) {
        return new GalleryRepository(application);
    }

    private GalleryRepository(Application application) {
        medialist = new MutableLiveData<>();
        albumsArray = new MutableLiveData<>();
        handler = new Handler();
        this.application = application;
        contentResolver = application.getContentResolver();
        getMedialist(application);
        getAlbumsList(application);
    }

    private void getMedialist(final Context context) {
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        medialist.postValue(getGalleryItems(context));
                    }
                });
                Looper.loop();

            }
        }.start();
    }

    private void getAlbumsList(final Context context){
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getBucketsList(context);
                    }
                });
                Looper.loop();

            }
        }.start();
    }

    /**
     * method to get filtered albums list
     */
    public void getFilteredAlbumsList(final String bucketId, final int type){

        if (type == 1) {

            new Thread(){
                @Override
                public void run() {
                    Looper.prepare();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            List<MediaBean> mediaBeanList = new ArrayList<>();

                            String[] selectionArgs = {bucketId};

                            Cursor cursor = contentResolver.query(queryUri,
                                    projection,
                                    selection,
                                    selectionArgs,
                                    null);

                            assert cursor != null;
                            int count = cursor.getCount();

                            for (int i = 0; i < count; i++) {
                                cursor.moveToPosition(i);
                                int type = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE));
                                MediaBean multimediaItem = new MediaBean();

                                if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                                    multimediaItem.setType(type);
                                    multimediaItem.setUri(ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))));

                                } else if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                                    multimediaItem.setType(type);
                                    multimediaItem.setUri(ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                            cursor.getInt(cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID))));
                                }

//                                multimediaItem.setPath(FileUtils.getPath(application,multimediaItem.getUri()));
                                multimediaItem.setPath(com.abhinav.instacropperpicker.FileUtils.getRealPathFromURI(application, multimediaItem.getUri()));

                                mediaBeanList.add(multimediaItem);
                            }

                            cursor.close();

                            medialist.postValue(mediaBeanList);
                        }
                    });
                    Looper.loop();
                }
            }.start();

        }else {
            getMedialist(application);
        }

    }

    /**
     * method to get album names
     */
    private void getBucketsList(Context context){

        List<BucketBean> bucketList = new ArrayList<>();
        BucketBean bucketBean = new BucketBean();
        bucketBean.setBucketId("0");
        bucketBean.setBucketName("Gallery");
        bucketList.add(bucketBean);

        // which image properties are we querying
        String[] PROJECTION_BUCKET = {
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.DATA};
        // We want to order the albums by reverse chronological order. We abuse the
        // "WHERE" parameter to insert a "GROUP BY" clause into the SQL statement.
        // The template for "WHERE" parameter is like:
        //    SELECT ... FROM ... WHERE (%s)
        // and we make it look like:
        //    SELECT ... FROM ... WHERE (1) GROUP BY 1,(2)
        // The "(1)" means true. The "1,(2)" means the first two columns specified
        // after SELECT. Note that because there is a ")" in the template, we use
        // "(2" to match it.
        String BUCKET_GROUP_BY =
                "1) GROUP BY 1,(2";
        String BUCKET_ORDER_BY = "MAX(datetaken) DESC";

        // Get the base URI for the People table in the Contacts content provider.
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        Cursor cur = context.getContentResolver().query(
                images, PROJECTION_BUCKET, BUCKET_GROUP_BY, null, BUCKET_ORDER_BY);


        if (cur!=null && cur.moveToFirst()) {

            int bucketColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int bucketIdColumn = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);

            do {

                BucketBean bucket = new BucketBean();

                bucket.setBucketName(cur.getString(bucketColumn));
                bucket.setBucketId(cur.getString(bucketIdColumn));

                bucketList.add(bucket);

            } while (cur.moveToNext());
        }

        assert cur != null;
        cur.close();

        albumsArray.postValue(bucketList);
    }

    /**
     * method to get all the items from a gallery
     * @param context
     * @return
     */
    private List<MediaBean> getGalleryItems(Context context) {

        List<MediaBean> mediaBeanList = new ArrayList<>();

        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE
        };

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        Uri queryUri = MediaStore.Files.getContentUri("external");

        CursorLoader cursorLoader = new CursorLoader(context, queryUri, projection, selection, null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
        );

        Cursor cursor = cursorLoader.loadInBackground();
        int count = cursor.getCount();
        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int type = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE));
            MediaBean multimediaItem = new MediaBean();
            if(type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)));
                multimediaItem.setUri(uri);
                //     multimediaItem.setPath(AppUtils.getRealPathFromURI(context,uri));

            }else {
                Uri uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        cursor.getInt(cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID)));
                multimediaItem.setUri(uri);
                //     multimediaItem.setPath(AppUtils.getRealPathFromURI(context,uri));

            }


            multimediaItem.setType(type);


            mediaBeanList.add(multimediaItem);
        }

        cursor.close();
        medialist.postValue(mediaBeanList);
        return mediaBeanList;

    }

    public MutableLiveData<List<MediaBean>> getMedialist() {
        return medialist;
    }

    public MutableLiveData<List<BucketBean>> getAlbumsArray() {
        return albumsArray;
    }

}

