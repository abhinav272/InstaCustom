package com.abhinav.instacropperpicker;

import android.graphics.BitmapFactory;
import android.net.Uri;

import com.yalantis.ucrop.util.BitmapLoadUtils;
import com.yalantis.ucrop.util.FileUtils;

public class SizeUtils {

    private Uri fileUri;
    private float height;
    private float width;
    private int exifOrientation;

    public SizeUtils(Uri fileUri) {
        this.fileUri = fileUri;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    private void init() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(FileUtils.getPath(App.getContext(), fileUri), options);
        exifOrientation = BitmapLoadUtils.exifToDegrees(BitmapLoadUtils.getExifOrientation(App.getContext(), fileUri));
        width = options.outWidth;
        height = options.outHeight;
    }

    public float getAspectRatio() {
        float ratio = 1f;
        if (height == 0 || width == 0)
            init();
        switch (exifOrientation) {
            case 0:
            case 180:
                ratio = width / height;
                break;
            case 90:
            case 270:
                ratio = height / width;
                break;
        }
        return ratio;

    }
}
