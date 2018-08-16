package com.abhinav.instacropperpicker.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.abhinav.instacropperpicker.CropContainerView;
import com.abhinav.instacropperpicker.R;
import com.abhinav.instacropperpicker.SizeUtils;
import com.yalantis.ucrop.MyVideoView;
import com.yalantis.ucrop.UCropFragment;
import com.yalantis.ucrop.UCropFragmentCallback;
import com.yalantis.ucrop.callback.BitmapCropCallback;
import com.yalantis.ucrop.model.AspectRatio;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MediaHandlerFragment extends CropFragment {

    private boolean isAspectToggleEnable;
    private boolean isMultipleMediaSelected;
    private FrameLayout flAllCropViewsContainer;
    private List<CropContainerView> allCropViews = new ArrayList<>();
    private CropContainerView cropView;
    private UCropFragmentCallback callback;
    private MyVideoView videoView;
    private List<Uri> selectedVideoUris = new ArrayList<>();
    private ArrayList<Uri> processedUris = new ArrayList<>();
    private int count;


    protected void setupViews(View view) {
        initiateRootViews(view);
        videoView = new MyVideoView(getContext());
        setVideoListener();
        flAllCropViewsContainer = view.findViewById(R.id.fl_crop_view_container);
        cropView = addCropContainerView();
        allCropViews.add(cropView);
    }

    private void setVideoListener() {
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                mp.setLooping(true);
                videoView.start();
            }
        });
    }

    @Override
    public void setCallback(UCropFragmentCallback callback) {
        super.setCallback(callback);
        this.callback = callback;
    }

    protected void updateImage(Uri imageUri) {
        processAspectRatios(imageUri);
        resetCropImageView();
        setImageData(imageUri);
        flAllCropViewsContainer.removeView(videoView);
    }

    private void processAspectRatios(Uri imageUri) {

        float sourceARatio = getOriginalAspectRatio(imageUri);
        ArrayList<AspectRatio> aspectRatios = new ArrayList<>();
        aspectRatios.add(new AspectRatio("Square", 1, 1));
        if (sourceARatio > 1) {
            aspectRatios.add(new AspectRatio("Landscape", 3, 2));
            isAspectToggleEnable = true;
        } else if (sourceARatio < 1) {
            aspectRatios.add(new AspectRatio("Portrait", 2, 3));
            isAspectToggleEnable = true;
        } else {
            isAspectToggleEnable = false;
        }

        setAspectRatios(aspectRatios);

    }

    private float getOriginalAspectRatio(Uri fileUri) {
        SizeUtils sizeUtils = new SizeUtils(fileUri);
        return sizeUtils.getAspectRatio();
    }

    protected void updateVideo(Uri videoUri) {
        showVideo(videoUri);
        selectedVideoUris.clear();
        selectedVideoUris.add(videoUri);
    }

    protected int imageSelectedToAdd(Uri imageUri) {
        if (alreadyContains(imageUri)) {
            return bringToFront(imageUri);
        } else
            addNewImage(imageUri);
        if (videoView.getParent() != null)
            flAllCropViewsContainer.removeView(videoView);
        return MediaState.MEDIA_ADDED;
    }

    protected int videoSelectedToAdd(Uri videoUri) {
        if (selectedVideoUris.contains(videoUri)) {
            return bringVideoToFront(videoUri);
        } else
            addNewVideo(videoUri);
        return MediaState.MEDIA_ADDED;
    }

    private int bringVideoToFront(Uri videoUri) {
        if (videoView.getParent() != null) {
            if (videoView.getVideoUri().equals(videoUri)) {
                Uri vidUri = getReplacementVideoUri(videoUri);
                if (vidUri != null)
                    showVideo(vidUri);
                else {
                    flAllCropViewsContainer.removeView(videoView);
                    selectedVideoUris.clear();
                }
                return MediaState.MEDIA_REMOVED;
            }
        }

        showVideo(videoUri);
        return MediaState.MEDIA_BROUGHT_TO_TOP;
    }

    private Uri getReplacementVideoUri(Uri videoUri) {
        selectedVideoUris.remove(videoUri);
        if (selectedVideoUris.size() == 0)
            return null;
        else return selectedVideoUris.get(selectedVideoUris.size() - 1);
    }

    private boolean alreadyContains(Uri imageUri) {

        for (CropContainerView view : allCropViews) {
            if (view.getInputUri().equals(imageUri))
                return true;
        }

        return false;
    }

    protected void addNewImage(Uri imageUri) {
        cropView = addCropContainerView();
        allCropViews.add(cropView);
        setupTargetAspectRatio();
        setImageData(imageUri);
        hideOtherViews();
    }

    protected void addNewVideo(Uri videoUri) {
        showVideo(videoUri);
        selectedVideoUris.add(videoUri);
    }

    private void showVideo(Uri videoUri) {
        if (videoView.getParent() == null)
            flAllCropViewsContainer.addView(videoView);
        videoView.setVideoURI(videoUri);
    }

    protected int bringToFront(Uri mediaUri) {
        if (removeIfSelectedTopMedia(mediaUri)) {
            if (videoView.getParent() != null) {
                flAllCropViewsContainer.removeView(videoView);
                return MediaState.MEDIA_BROUGHT_TO_TOP;
            }
            return MediaState.MEDIA_REMOVED;
        }

        for (CropContainerView view : allCropViews) {
            if (mediaUri.equals(view.getInputUri())) {
                view.setVisibility(View.VISIBLE);
                cropView = view;
                initCropViewChilds();
            } else view.setVisibility(View.GONE);
        }

        if (videoView.getParent() != null)
            flAllCropViewsContainer.removeView(videoView);

        return MediaState.MEDIA_BROUGHT_TO_TOP;
    }

    private boolean removeIfSelectedTopMedia(Uri mediaUri) {
        if (cropView.getInputUri().equals(mediaUri) && allCropViews.size() > 1) {
            allCropViews.remove(cropView);
            flAllCropViewsContainer.removeView(cropView);
            cropView = (CropContainerView) flAllCropViewsContainer
                    .getChildAt(flAllCropViewsContainer.getChildCount() - 1);

            cropView.setVisibility(View.VISIBLE);
            cropViewChanged(cropView);
            return true;
        }
        return false;
    }

    protected void resetToSingleMedia() {
        for (CropContainerView view : allCropViews) {
            if (view.getId() != cropView.getId()) {
                flAllCropViewsContainer.removeView(view);
            }
        }
        allCropViews.clear();
        allCropViews.add(cropView);
        cropViewChanged(cropView);
        processAspectRatios(cropView.getInputUri());
        setCurrentAspectRatio(null);

    }

    private void hideOtherViews() {
        for (CropContainerView view : allCropViews) {
            if (view.getId() != cropView.getId()) {
                view.setVisibility(View.GONE);
            }
        }
    }

    protected void removeMultiSelectedMedia() {
        isAspectToggleEnable = true;
        resetToSingleMedia();
    }

    protected boolean getIsAspectToggleEnable() {
        return isAspectToggleEnable;
    }

    protected boolean isMultipleMediaSelected() {
        return isMultipleMediaSelected;
    }

    protected void setMultipleMediaSelected(boolean multipleMediaSelected) {
        isMultipleMediaSelected = multipleMediaSelected;
    }

    protected void cropImage() {
        for (CropContainerView cropView : allCropViews) {

            cropView.getCropImageView().cropAndSaveImage(Bitmap.CompressFormat.PNG, 50, new BitmapCropCallback() {
                @Override
                public void onBitmapCropped(@NonNull Uri resultUri, int offsetX, int offsetY, int imageWidth, int imageHeight) {
                    Log.e(TAG, "onBitmapCropped: " + resultUri.toString());
                    count++;
                    processedUris.add(resultUri);
                    if (count == allCropViews.size()){
                        processedUris.addAll(selectedVideoUris);
                        callback.onCropFinish(new UCropFragment.UCropResult(RESULT_OK, new Intent()
                                .putParcelableArrayListExtra("ddd", processedUris)));
                    }
                }

                @Override
                public void onCropFailure(@NonNull Throwable t) {
                    Log.e(TAG, "onCropFailure: ", t);
                }
            });
        }
    }

    public static class MediaState {
        public static final int MEDIA_ADDED = 1;
        public static final int MEDIA_BROUGHT_TO_TOP = 2;
        public static final int MEDIA_REMOVED = 3;
    }
}
