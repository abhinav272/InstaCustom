package com.abhinav.instacropperpicker;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import com.fenchtose.nocropper.CropperView;

public class CropperNoCropperActivity extends Activity {

    private CropperView cropperView;
    private Button snap;
    private boolean isSnappedToCenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another);

        cropperView = findViewById(R.id.cropper_view);
        snap = findViewById(R.id.snap);

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.rrrr);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        cropperView.setImageBitmap(bitmap);

        snap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snapImage();
            }
        });

        cropperView.setGridCallback(new CropperView.GridCallback() {
            @Override
            public boolean onGestureStarted() {
                return true;
            }

            @Override
            public boolean onGestureCompleted() {
                return false;
            }
        });

    }

    private void snapImage() {
        if (isSnappedToCenter) {
            cropperView.cropToCenter();
        } else {
            cropperView.fitToCenter();
        }

        isSnappedToCenter = !isSnappedToCenter;
    }

}
