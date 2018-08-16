package com.abhinav.instacropperpicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.abhinav.instacropperpicker.fragments.CameraFragment;
import com.abhinav.instacropperpicker.fragments.GalleryFragment;
import com.otaliastudios.cameraview.SessionType;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropFragment;
import com.yalantis.ucrop.UCropFragmentCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, UCropFragmentCallback {


    private ViewPager viewPager;
    private TextView tvGallery, tvPhoto, tvVideo;
    private GalleryFragment galleryFragment;
    private CameraFragment cameraFragment = new CameraFragment();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.view_pager);
        tvGallery = findViewById(R.id.tv_gallery);
        tvPhoto = findViewById(R.id.tv_picture);
        tvVideo = findViewById(R.id.tv_video);

        tvVideo.setOnClickListener(this);
        tvPhoto.setOnClickListener(this);
        tvGallery.setOnClickListener(this);


//        pickImage();

        startCrop();

    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*")
                .addCategory(Intent.CATEGORY_OPENABLE);

        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        startActivityForResult(Intent.createChooser(intent, ""), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
//                    startCrop(selectedUri);
                } else {
                    Toast.makeText(this, "Something wrong in picking up image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void startCrop() {
////        String destinationFileName = "sampleImage";
////        destinationFileName += ".png";
//        UCrop uCrop = UCrop.of(selectedUri, Uri.fromFile(FileUtils.getNextImageFile()));
////        UCrop uCrop = UCrop.of(selectedUri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
//        float originalAspectRatio = getOriginalAspectRatio(selectedUri);
//        uCrop = uCrop.withSourceImageAspectRatio(originalAspectRatio);
//        uCrop = basisConfig(uCrop);
//        uCrop = advancedConfig(uCrop);

        Bundle extras = UCrop.getBasic().getCropOptionsBundle();
        galleryFragment = GalleryFragment.getInstance(extras);

//        showFragment(uCrop);

        initiateViewPager();

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

    private void initiateViewPager() {
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {

                switch (position) {
                    case 0:
                        return galleryFragment;
                    case 1:
                        return cameraFragment;
                }
                return null;
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
    }

    @Override
    public void onClick(View v) {
        v.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        switch (v.getId()) {
            case R.id.tv_gallery:
                viewPager.setCurrentItem(0, true);
                break;
            case R.id.tv_picture:
                viewPager.setCurrentItem(1, true);
                cameraFragment.setSessionType(SessionType.PICTURE);
                cameraFragment.updateListenerForImage();
                break;
            case R.id.tv_video:
//                viewPager.setCurrentItem(1, true);
//                cameraFragment.setSessionType(SessionType.VIDEO);
//                cameraFragment.updateListenerForVideo();

                galleryFragment.cropImage();

                break;
        }
    }

    @Override
    public void loadingProgress(boolean showLoader) {

    }

    @Override
    public void onCropFinish(UCropFragment.UCropResult result) {

    }
}
