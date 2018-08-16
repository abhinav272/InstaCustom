package com.abhinav.instacropperpicker.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.abhinav.instacropperpicker.CropContainerView;
import com.abhinav.instacropperpicker.FileUtils;
import com.abhinav.instacropperpicker.R;
import com.universalvideoview.UniversalVideoView;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropFragment;
import com.yalantis.ucrop.UCropFragmentCallback;
import com.yalantis.ucrop.model.AspectRatio;
import com.yalantis.ucrop.view.CropImageView;
import com.yalantis.ucrop.view.GestureCropImageView;
import com.yalantis.ucrop.view.OverlayView;
import com.yalantis.ucrop.view.TransformImageView;

import java.util.ArrayList;

public class CropFragment extends UCropFragment {

    public static final int DEFAULT_COMPRESS_QUALITY = 50;
    private ArrayList<AspectRatio> aspectRatios;
    private int mCompressQuality;
    private Bitmap.CompressFormat mCompressFormat = DEFAULT_COMPRESS_FORMAT;
    private CropContainerView mUCropView;
    private GestureCropImageView mGestureCropImageView;
    private OverlayView mOverlayView;
    private View mBlockingView;
    private int[] mAllowedGestures = new int[]{SCALE, ROTATE, ALL};
    private UCropFragmentCallback callback;
    private FrameLayout flCorpContainerViewParent;
    private AspectRatio currentAspectRatio;
    private VideoView videoView;
    private TransformImageView.TransformImageListener mImageListener = new TransformImageView.TransformImageListener() {
        @Override
        public void onRotate(float currentAngle) {
            Log.e(TAG, "onRotate: " + currentAngle);
        }

        @Override
        public void onScale(float currentScale) {
            Log.e(TAG, "onScale: " + currentScale);
        }

        @Override
        public void onLoadComplete() {
            mUCropView.animate().alpha(1).setDuration(300).setInterpolator(new AccelerateInterpolator());
            mBlockingView.setClickable(false);
            callback.loadingProgress(false);
        }

        @Override
        public void onLoadFailure(@NonNull Exception e) {
            callback.onCropFinish(getError(e));
        }

    };

    public void setCallback(UCropFragmentCallback callback) {
        this.callback = callback;
    }

    public void setAspectRatios(ArrayList<AspectRatio> aspectRatios) {
        this.aspectRatios = aspectRatios;
    }

//    protected void setupViews(View view) {
//        initiateRootViews(view);
//    }


    public AspectRatio getCurrentAspectRatio() {
        return currentAspectRatio;
    }

    public void setCurrentAspectRatio(AspectRatio currentAspectRatio) {
        this.currentAspectRatio = currentAspectRatio;
    }

    protected void resetCropImageView() {
        mUCropView.resetCropImageView();
        initCropViewChilds();
    }

    protected void initiateRootViews(View view) {
        flCorpContainerViewParent = view.findViewById(R.id.fl_crop_view_container);
//        mUCropView = addCropContainerView();
//        allCropViews.add(mUCropView);
        addBlockingView(view);
    }

    protected void setupTargetAspectRatio() {
        if (currentAspectRatio != null) {
            mGestureCropImageView.setTargetAspectRatio(
                    currentAspectRatio.getAspectRatioX() / currentAspectRatio.getAspectRatioY());
        }
    }

    protected CropContainerView addCropContainerView() {
        View inflate = LayoutInflater.from(getContext())
                .inflate(R.layout.layout_custom_crop_view, flCorpContainerViewParent, true);
        int id = View.generateViewId();
        CropContainerView view = (CropContainerView) ((FrameLayout) inflate).getChildAt(((FrameLayout) inflate).getChildCount() - 1);
        view.setId(id);
        mUCropView = view;
        initCropViewChilds();
        return view;
    }

    protected void cropViewChanged(CropContainerView cropView) {
        mUCropView = cropView;
        initCropViewChilds();
    }

    protected void initCropViewChilds() {
        mGestureCropImageView = mUCropView.getCropImageView();
        mOverlayView = mUCropView.getOverlayView();

        mGestureCropImageView.setTransformImageListener(mImageListener);
        setInitialState();
    }

    protected void setImageData(Uri imageUri) {
//        Uri inputUri = bundle.getParcelable(UCrop.EXTRA_INPUT_URI);
//        Uri outputUri = bundle.getParcelable(UCrop.EXTRA_OUTPUT_URI);

        Uri outputUri = Uri.fromFile(FileUtils.getNextImageFile());

        mUCropView.setInputImageUri(imageUri);
        mUCropView.setOutputUri(outputUri);

        processOptions(getArguments());

        if (imageUri != null && outputUri != null) {
            try {
                mGestureCropImageView.setImageUri(imageUri, outputUri);
            } catch (Exception e) {
                callback.onCropFinish(getError(e));
            }
        } else {
            callback.onCropFinish(getError(new NullPointerException(getString(com.yalantis.ucrop.R.string.ucrop_error_input_data_is_absent))));
        }
    }

    private void processOptions(Bundle bundle) {
        // Bitmap compression options
        String compressionFormatName = bundle.getString(UCrop.Options.EXTRA_COMPRESSION_FORMAT_NAME);
        Bitmap.CompressFormat compressFormat = null;
        if (!TextUtils.isEmpty(compressionFormatName)) {
            compressFormat = Bitmap.CompressFormat.valueOf(compressionFormatName);
        }
        mCompressFormat = (compressFormat == null) ? DEFAULT_COMPRESS_FORMAT : compressFormat;

        mCompressQuality = DEFAULT_COMPRESS_QUALITY;


        // Crop image view options
        mGestureCropImageView.setMaxBitmapSize(bundle.getInt(UCrop.Options.EXTRA_MAX_BITMAP_SIZE, CropImageView.DEFAULT_MAX_BITMAP_SIZE));
        mGestureCropImageView.setMaxScaleMultiplier(bundle.getFloat(UCrop.Options.EXTRA_MAX_SCALE_MULTIPLIER, CropImageView.DEFAULT_MAX_SCALE_MULTIPLIER));
        mGestureCropImageView.setImageToWrapCropBoundsAnimDuration(bundle.getInt(UCrop.Options.EXTRA_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION, CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION));

        // Overlay view options
        mOverlayView.setFreestyleCropEnabled(false);

        mOverlayView.setDimmedColor(bundle.getInt(UCrop.Options.EXTRA_DIMMED_LAYER_COLOR, getResources().getColor(android.R.color.transparent)));
        mOverlayView.setCircleDimmedLayer(bundle.getBoolean(UCrop.Options.EXTRA_CIRCLE_DIMMED_LAYER, OverlayView.DEFAULT_CIRCLE_DIMMED_LAYER));

        mOverlayView.setShowCropFrame(bundle.getBoolean(UCrop.Options.EXTRA_SHOW_CROP_FRAME, OverlayView.DEFAULT_SHOW_CROP_FRAME));
        mOverlayView.setCropFrameColor(bundle.getInt(UCrop.Options.EXTRA_CROP_FRAME_COLOR, getResources().getColor(R.color.ucrop_color_default_crop_frame)));
        mOverlayView.setCropFrameStrokeWidth(bundle.getInt(UCrop.Options.EXTRA_CROP_FRAME_STROKE_WIDTH, getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_frame_stoke_width)));

        mOverlayView.setShowCropGrid(bundle.getBoolean(UCrop.Options.EXTRA_SHOW_CROP_GRID, OverlayView.DEFAULT_SHOW_CROP_GRID));
        mOverlayView.setCropGridRowCount(bundle.getInt(UCrop.Options.EXTRA_CROP_GRID_ROW_COUNT, OverlayView.DEFAULT_CROP_GRID_ROW_COUNT));
        mOverlayView.setCropGridColumnCount(bundle.getInt(UCrop.Options.EXTRA_CROP_GRID_COLUMN_COUNT, OverlayView.DEFAULT_CROP_GRID_COLUMN_COUNT));
        mOverlayView.setCropGridColor(bundle.getInt(UCrop.Options.EXTRA_CROP_GRID_COLOR, getResources().getColor(R.color.ucrop_color_default_crop_grid)));
        mOverlayView.setCropGridStrokeWidth(bundle.getInt(UCrop.Options.EXTRA_CROP_GRID_STROKE_WIDTH, getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_grid_stoke_width)));

        // Aspect ratio options -- default to SQUARE
        float aspectRatioX = bundle.getFloat(UCrop.EXTRA_ASPECT_RATIO_X, 1);
        float aspectRatioY = bundle.getFloat(UCrop.EXTRA_ASPECT_RATIO_Y, 1);
        if (currentAspectRatio != null) {
            aspectRatioX = currentAspectRatio.getAspectRatioX();
            aspectRatioY = currentAspectRatio.getAspectRatioY();
        }

        int aspectRationSelectedByDefault = bundle.getInt(UCrop.Options.EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT, 0);

        if (aspectRatioX > 0 && aspectRatioY > 0) {
//            if (mWrapperStateAspectRatio != null) {
//                mWrapperStateAspectRatio.setVisibility(View.GONE);
//            }
            mGestureCropImageView.setTargetAspectRatio(aspectRatioX / aspectRatioY);
        } else if (aspectRatios != null && aspectRationSelectedByDefault < aspectRatios.size()) {
            mGestureCropImageView.setTargetAspectRatio(aspectRatios.get(aspectRationSelectedByDefault).getAspectRatioX() /
                    aspectRatios.get(aspectRationSelectedByDefault).getAspectRatioY());
        } else {
            mGestureCropImageView.setTargetAspectRatio(CropImageView.SOURCE_IMAGE_ASPECT_RATIO);
        }

        // Result bitmap max size options
        int maxSizeX = bundle.getInt(UCrop.EXTRA_MAX_SIZE_X, 0);
        int maxSizeY = bundle.getInt(UCrop.EXTRA_MAX_SIZE_Y, 0);

        if (maxSizeX > 0 && maxSizeY > 0) {
            mGestureCropImageView.setMaxResultImageSizeX(maxSizeX);
            mGestureCropImageView.setMaxResultImageSizeY(maxSizeY);
        }
    }

    private void setInitialState() {
        setAllowedGestures(0);
    }

    private void setAllowedGestures(int tab) {
        mGestureCropImageView.setScaleEnabled(mAllowedGestures[tab] == ALL || mAllowedGestures[tab] == SCALE);
        mGestureCropImageView.setRotateEnabled(mAllowedGestures[tab] == ALL || mAllowedGestures[tab] == ROTATE);
    }

    protected void toggleAspectRatio() {
        String tag = (String) mGestureCropImageView.getTag();
        if (tag == null)
            tag = "Square";
        AspectRatio toggleARatio;
        switch (tag) {
            case "Landscape":
            case "Portrait":
                toggleARatio = aspectRatios.get(0);
                break;
            default:
                toggleARatio = aspectRatios.get(1);
                break;
        }

        currentAspectRatio = toggleARatio;

        mGestureCropImageView.setTargetAspectRatio(
                toggleARatio.getAspectRatioX() / toggleARatio.getAspectRatioY());
        mGestureCropImageView.zoomOutImage(1f);
        mGestureCropImageView.setImageToWrapCropBounds();
        mGestureCropImageView.setTag(toggleARatio.getAspectRatioTitle());
    }

    private void addBlockingView(View view) {
        if (mBlockingView == null) {
            mBlockingView = new View(getContext());
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mBlockingView.setLayoutParams(lp);
            mBlockingView.setClickable(true);
        }

        ((RelativeLayout) view.findViewById(R.id.ucrop_photobox)).addView(mBlockingView);
    }

    public static class UCropResult {

        public int mResultCode;
        public Intent mResultData;

        public UCropResult(int resultCode, Intent data) {
            mResultCode = resultCode;
            mResultData = data;
        }

    }
}
