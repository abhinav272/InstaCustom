package com.abhinav.instacropperpicker;

import android.graphics.BitmapFactory;
import android.net.Uri;

import com.yalantis.ucrop.util.FileUtils;

public class SizeUtils {

    private Uri fileUri;
    private float height;
    private float width;

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
        width = options.outWidth;
        height = options.outHeight;
    }

    public float getAspectRatio() {
        if (height == 0 || width == 0)
            init();
        return width / height;
    }
}
