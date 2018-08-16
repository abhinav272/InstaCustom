package com.abhinav.instacropperpicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.abhinav.instacropperpicker.fragments.YalantisCropFragment;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropFragment;
import com.yalantis.ucrop.UCropFragmentCallback;

public class SampleUCropActivity extends AppCompatActivity implements UCropFragmentCallback {

    private YalantisCropFragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_crop);


        pickImage();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    startCrop(selectedUri);
                } else {
                    Toast.makeText(this, "Something wrong in picking up image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*")
                .addCategory(Intent.CATEGORY_OPENABLE);

        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        startActivityForResult(Intent.createChooser(intent, ""), 1);
    }

    private void startCrop(Uri selectedUri) {
//        String destinationFileName = "sampleImage";
//        destinationFileName += ".png";
        UCrop uCrop = UCrop.of(selectedUri, Uri.fromFile(FileUtils.getNextImageFile()));
//        UCrop uCrop = UCrop.of(selectedUri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        float originalAspectRatio = getOriginalAspectRatio(selectedUri);
        uCrop = uCrop.withSourceImageAspectRatio(originalAspectRatio);
        uCrop = basisConfig(uCrop);
        uCrop = advancedConfig(uCrop);

        showFragment(uCrop);
    }

    private float getOriginalAspectRatio(Uri fileUri) {
        SizeUtils sizeUtils = new SizeUtils(fileUri);
        return sizeUtils.getAspectRatio();
    }

    private UCrop advancedConfig(UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);

        options.setCompressionQuality(50);
        options.setHideBottomControls(true);
        options.setFreeStyleCropEnabled(false);


        return uCrop.withOptions(options);
    }

    private UCrop basisConfig(UCrop uCrop) {
        uCrop = uCrop.withAspectRatio(1, 1);

        return uCrop;
    }

    private void showFragment(UCrop uCrop) {
        Bundle extras = uCrop.getIntent(this).getExtras();
        fragment = YalantisCropFragment.newInstance(extras);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_container, fragment, UCropFragment.TAG)
                .commitAllowingStateLoss();
    }

    @Override
    public void loadingProgress(boolean showLoader) {
        Log.e("loadingProgress: ", "" + showLoader);
    }

    @Override
    public void onCropFinish(UCropFragment.UCropResult result) {
        Log.e("onCropFinish: ", result.mResultData.getData() + "");
    }
}
