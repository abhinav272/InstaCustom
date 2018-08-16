package com.abhinav.instacropperpicker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yashoid.instacropper.InstaCropperView;

public class InstaCropperActivity extends Activity {

    InstaCropperView cropperView;

    /*1734826307, 1580780387*/

    Uri uri1 = Uri.parse("content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F79495/ORIGINAL/NONE/469292260");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insta_cropper);

        cropperView = findViewById(R.id.instacropper);
        cropperView.setRatios(1, 1, 4);

        pickPhoto();

    }


    public void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, 1);

//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getFile()));
//        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            cropperView.setImageUri(data.getData());
            Log.e("onActivityResult: ", data.getData() + "");
        }
    }
}
