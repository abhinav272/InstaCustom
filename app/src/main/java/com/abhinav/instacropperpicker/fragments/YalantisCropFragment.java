package com.abhinav.instacropperpicker.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.abhinav.instacropperpicker.App;
import com.abhinav.instacropperpicker.R;
import com.abhinav.instacropperpicker.SizeUtils;
import com.abhinav.instacropperpicker.adapter.GalleryAdapter;
import com.abhinav.instacropperpicker.bean.BucketBean;
import com.abhinav.instacropperpicker.bean.MediaBean;
import com.abhinav.instacropperpicker.repo.GalleryViewModel;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropFragment;
import com.yalantis.ucrop.UCropFragmentCallback;
import com.yalantis.ucrop.callback.BitmapCropCallback;
import com.yalantis.ucrop.model.AspectRatio;
import com.yalantis.ucrop.util.FileUtils;
import com.yalantis.ucrop.view.CropImageView;
import com.yalantis.ucrop.view.GestureCropImageView;
import com.yalantis.ucrop.view.OverlayView;
import com.yalantis.ucrop.view.TransformImageView;
import com.yalantis.ucrop.view.UCropView;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class YalantisCropFragment extends UCropFragment {

    private static final int TABS_COUNT = 3;
    /**
     * callback to observe bucket list containing images after retrieving it in backgound
     */
    Observer<List<BucketBean>> bucketListObserver = new Observer<List<BucketBean>>() {
        @Override
        public void onChanged(@Nullable List<BucketBean> bucketList) {
//            bucketListAdapter.submitList(bucketList);
//            bucketListAdapter.notifyDataSetChanged();
        }
    };
    private UCropView mUCropView;
    private GestureCropImageView mGestureCropImageView;
    private OverlayView mOverlayView;
    private View mBlockingView;
    private UCropFragmentCallback callback;
    private ImageView aspectRatio;
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
    private int mCompressQuality;
    private Bitmap.CompressFormat mCompressFormat = DEFAULT_COMPRESS_FORMAT;
    private int[] mAllowedGestures = new int[]{SCALE, ROTATE, ALL};
    private Button btnCropSave;
    private RecyclerView rvGallery;
    private GalleryAdapter adapter;
    /**
     * callback to observe media list after retrieving it in background
     */
    Observer<List<MediaBean>> medialistObserver = new Observer<List<MediaBean>>() {
        @Override
        public void onChanged(@Nullable List<MediaBean> mediaBeans) {
            assert mediaBeans != null;
            if (mediaBeans.size() > 0) {
                Bitmap bm = BitmapFactory.decodeFile(FileUtils.getPath(App.getContext(), mediaBeans.get(0).getUri()));
//                instacropper.setImageBitmap(bm);
            }
            adapter.submitList(mediaBeans);
//            adapter.notifyDataSetChanged();
            rvGallery.scrollToPosition(0);
        }
    };
    private ArrayList<AspectRatio> aspectRatios;

    public static YalantisCropFragment newInstance(Bundle bundle) {
        YalantisCropFragment fragment = new YalantisCropFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (UCropFragmentCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement UCropFragmentCallback");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ucrop_custom, container, false);

        Bundle args = getArguments();
        setupViews(view, args);
        setImageData(args);
        setInitialState();
        addBlockingView(view);

        return view;
    }

//    private void updateImage(Uri newImageUri) {
//        Bundle bundle = getArguments();
//        float originalAspectRatio = getOriginalAspectRatio(newImageUri);
//        bundle.putFloat(UCrop.EXTRA_IMAGE_ORIGINAL_ASPECT_RATIO, originalAspectRatio);
//        bundle.putParcelable(UCrop.EXTRA_INPUT_URI, newImageUri);
//
//        resetCropImageView();
//
//        setImageData(bundle);
//
//    }

    public void resetCropImageView() {
        mUCropView.resetCropImageView();
        initCropViewChilds();
        setInitialState();
    }

//    public float getOriginalAspectRatio(Uri fileUri) {
//        SizeUtils sizeUtils = new SizeUtils(fileUri);
//        return sizeUtils.getAspectRatio();
//    }

    protected void setImageData(@NonNull Bundle bundle) {

//        setupAspectRatioWidget(bundle);

        Uri inputUri = bundle.getParcelable(UCrop.EXTRA_INPUT_URI);
        Uri outputUri = bundle.getParcelable(UCrop.EXTRA_OUTPUT_URI);
        processOptions(bundle);

        if (inputUri != null && outputUri != null) {
            try {
                mGestureCropImageView.setImageUri(inputUri, outputUri);
            } catch (Exception e) {
                callback.onCropFinish(getError(e));
            }
        } else {
            callback.onCropFinish(getError(new NullPointerException(getString(com.yalantis.ucrop.R.string.ucrop_error_input_data_is_absent))));
        }
    }

    @Override
    public void setupViews(View view, Bundle args) {

//        btnCropSave = view.findViewById(R.id.crop_save);
//        btnCropSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cropAndSaveImage();
//            }
//        });


//        initGalleryView(view);

        initiateRootViews(view);
    }

    /**
     * Moved
     */
    private void initGalleryView(View view) {

//        initViewModel();

        rvGallery = view.findViewById(R.id.rv_gallery);
        rvGallery.setLayoutManager(new GridLayoutManager(getContext(), 4));
        adapter = new GalleryAdapter(new DiffUtil.ItemCallback<MediaBean>() {
            @Override
            public boolean areItemsTheSame(MediaBean oldItem, MediaBean newItem) {
                return oldItem.getUri() != null && newItem.getUri() != null && oldItem.getUri().equals(newItem.getUri());
            }

            @Override
            public boolean areContentsTheSame(MediaBean oldItem, MediaBean newItem) {
                return oldItem.equals(newItem);
            }
        }, getContext());

        adapter.setRecyclerOnItemListener(new GalleryAdapter.RecyclerOnItemListener() {
            @Override
            public void onClick(View view, int position, MediaBean object) {
                Toast.makeText(getContext(), object.getPath() + " --- " + object.getUri(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onClick: " + object.getPath());
                Log.e(TAG, "onClick: " + object.getUri());

//                updateImage(object.getUri());
            }

            @Override
            public boolean onLongClick(View view, int position, MediaBean object, int requestCode) {
                return false;
            }
        });

        rvGallery.setAdapter(adapter);
    }

    /**
     * Moved
     */
    private void initViewModel() {
        GalleryViewModel galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        galleryViewModel.getMediaList().observe(this, medialistObserver);
        galleryViewModel.getAlbumsArray().observe(this, bucketListObserver);
    }

    private void setupAspectRatioWidget(Bundle args) {
        float sourceARatio = args.getFloat(UCrop.EXTRA_IMAGE_ORIGINAL_ASPECT_RATIO);
        final ArrayList<AspectRatio> aspectRatios = new ArrayList<>();
        aspectRatios.add(new AspectRatio("Square", 1, 1));
        if (sourceARatio > 1)
            aspectRatios.add(new AspectRatio("Landscape", 3, 2));
        else if (sourceARatio < 1) {
            aspectRatios.add(new AspectRatio("Portrait", 2, 3));
        } else return;


        aspectRatio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                mGestureCropImageView.setTargetAspectRatio(
                        toggleARatio.getAspectRatioX() / toggleARatio.getAspectRatioY());
                mGestureCropImageView.zoomOutImage(1f);
                mGestureCropImageView.setImageToWrapCropBounds();
                mGestureCropImageView.setTag(toggleARatio.getAspectRatioTitle());
            }
        });
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
        mGestureCropImageView.setTargetAspectRatio(
                toggleARatio.getAspectRatioX() / toggleARatio.getAspectRatioY());
        mGestureCropImageView.zoomOutImage(1f);
        mGestureCropImageView.setImageToWrapCropBounds();
        mGestureCropImageView.setTag(toggleARatio.getAspectRatioTitle());
    }

    private void initiateRootViews(View view) {
        aspectRatio = view.findViewById(R.id.iv_aspect_ratio);
        mUCropView = view.findViewById(R.id.ucrop);
        initCropViewChilds();
    }

    private void initCropViewChilds() {
        mGestureCropImageView = mUCropView.getCropImageView();
        mOverlayView = mUCropView.getOverlayView();

        mGestureCropImageView.setTransformImageListener(mImageListener);
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

        // Gestures options
        int[] allowedGestures = bundle.getIntArray(UCrop.Options.EXTRA_ALLOWED_GESTURES);
        if (allowedGestures != null && allowedGestures.length == TABS_COUNT) {
            mAllowedGestures = allowedGestures;
        }

        // Crop image view options
        mGestureCropImageView.setMaxBitmapSize(bundle.getInt(UCrop.Options.EXTRA_MAX_BITMAP_SIZE, CropImageView.DEFAULT_MAX_BITMAP_SIZE));
        mGestureCropImageView.setMaxScaleMultiplier(bundle.getFloat(UCrop.Options.EXTRA_MAX_SCALE_MULTIPLIER, CropImageView.DEFAULT_MAX_SCALE_MULTIPLIER));
        mGestureCropImageView.setImageToWrapCropBoundsAnimDuration(bundle.getInt(UCrop.Options.EXTRA_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION, CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION));

        // Overlay view options
        mOverlayView.setFreestyleCropEnabled(bundle.getBoolean(UCrop.Options.EXTRA_FREE_STYLE_CROP, OverlayView.DEFAULT_FREESTYLE_CROP_MODE != OverlayView.FREESTYLE_CROP_MODE_DISABLE));

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

        // Aspect ratio options
        float aspectRatioX = bundle.getFloat(UCrop.EXTRA_ASPECT_RATIO_X, 0);
        float aspectRatioY = bundle.getFloat(UCrop.EXTRA_ASPECT_RATIO_Y, 0);

        int aspectRationSelectedByDefault = bundle.getInt(UCrop.Options.EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT, 0);
        aspectRatios = bundle.getParcelableArrayList(UCrop.Options.EXTRA_ASPECT_RATIO_OPTIONS);

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

    /**
     * Adds view that covers everything below the Toolbar.
     * When it's clickable - user won't be able to click/touch anything below the Toolbar.
     * Need to block user input while loading and cropping an image.
     */
    private void addBlockingView(View view) {
        if (mBlockingView == null) {
            mBlockingView = new View(getContext());
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mBlockingView.setLayoutParams(lp);
            mBlockingView.setClickable(true);
        }

        ((RelativeLayout) view.findViewById(R.id.ucrop_photobox)).addView(mBlockingView);
    }

    public void cropAndSaveImage() {
        mBlockingView.setClickable(true);
        callback.loadingProgress(true);

        mGestureCropImageView.cropAndSaveImage(mCompressFormat, mCompressQuality, new BitmapCropCallback() {

            @Override
            public void onBitmapCropped(@NonNull Uri resultUri, int offsetX, int offsetY, int imageWidth, int imageHeight) {
                callback.onCropFinish(getResult(resultUri, mGestureCropImageView.getTargetAspectRatio(), offsetX, offsetY, imageWidth, imageHeight));
                callback.loadingProgress(false);
            }

            @Override
            public void onCropFailure(@NonNull Throwable t) {
                callback.onCropFinish(getError(t));
            }
        });
    }

    protected UCropResult getResult(Uri uri, float resultAspectRatio, int offsetX, int offsetY, int imageWidth, int imageHeight) {
        return new UCropResult(RESULT_OK, new Intent()
                .putExtra(UCrop.EXTRA_OUTPUT_URI, uri)
                .putExtra(UCrop.EXTRA_OUTPUT_CROP_ASPECT_RATIO, resultAspectRatio)
                .putExtra(UCrop.EXTRA_OUTPUT_IMAGE_WIDTH, imageWidth)
                .putExtra(UCrop.EXTRA_OUTPUT_IMAGE_HEIGHT, imageHeight)
                .putExtra(UCrop.EXTRA_OUTPUT_OFFSET_X, offsetX)
                .putExtra(UCrop.EXTRA_OUTPUT_OFFSET_Y, offsetY)
        );
    }

    protected UCropResult getError(Throwable throwable) {
        return new UCropResult(UCrop.RESULT_ERROR, new Intent().putExtra(UCrop.EXTRA_ERROR, throwable));
    }
}
