package com.abhinav.instacropperpicker.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import com.abhinav.instacropperpicker.R;
import com.abhinav.instacropperpicker.adapter.GalleryAdapter;
import com.abhinav.instacropperpicker.bean.BucketBean;
import com.abhinav.instacropperpicker.bean.MediaBean;
import com.abhinav.instacropperpicker.repo.GalleryViewModel;
import com.yalantis.ucrop.UCropFragmentCallback;

import java.util.List;

public class GalleryFragment extends MediaHandlerFragment {

    private RecyclerView rvGallery;
    private GalleryAdapter adapter;
    private View currentMediaSelectedView;
    private FrameLayout flAspectRatio, flMultipleSelect;
    private AppCompatSpinner bucketSpinner;

    /**
     * callback to observe media list after retrieving it in background
     */
    private Observer<List<MediaBean>> mediaListObserver = new Observer<List<MediaBean>>() {
        @Override
        public void onChanged(@Nullable List<MediaBean> mediaBeans) {
//            assert mediaBeans != null;
//            if (mediaBeans.size() > 0) {
////                Bitmap bm = BitmapFactory.decodeFile(FileUtils.getPath(App.getContext(), mediaBeans.get(0).getUri()));
////                instacropper.setImageBitmap(bm);
//            }
            if (mediaBeans != null && mediaBeans.size() > 0) {
                adapter.submitList(mediaBeans);
                rvGallery.scrollToPosition(0);
                if (mediaBeans.get(0).getType() == 1)
                    updateImage(mediaBeans.get(0).getUri());
                else if (mediaBeans.get(0).getType() == 3)
                    updateVideo(mediaBeans.get(0).getUri());
            }
        }
    };
    private ArrayAdapter<BucketBean> bucketAdapter;
    /**
     * callback to observe bucket list containing images after retrieving it in backgound
     */
    private Observer<List<BucketBean>> bucketListObserver = new Observer<List<BucketBean>>() {
        @Override
        public void onChanged(@Nullable List<BucketBean> bucketList) {
//            bucketListAdapter.submitList(bucketList);
//            bucketListAdapter.notifyDataSetChanged();
            bucketAdapter.addAll(bucketList);

        }
    };
    private GalleryViewModel galleryViewModel;

    public static GalleryFragment getInstance(Bundle extras) {
        GalleryFragment fragment = new GalleryFragment();
        fragment.setArguments(extras);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            setCallback((UCropFragmentCallback) context);
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement UCropFragmentCallback");
        }
    }

    @Override
    public void setCallback(UCropFragmentCallback callback) {
        super.setCallback(callback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        setupViews(view);
        initGalleryViews(view);
        initClickWidgets(view);
        return view;
    }

    private void initClickWidgets(View view) {
        flAspectRatio = view.findViewById(R.id.fl_fit_size);
        flMultipleSelect = view.findViewById(R.id.fl_select_multiple);

        flAspectRatio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAspectRatio();
            }
        });


        flMultipleSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMultipleMediaSelected(!isMultipleMediaSelected());
                adapter.setInSelectedMode(isMultipleMediaSelected());
                if (isMultipleMediaSelected()) {
                    v.setAlpha(0.4f);
                } else {
                    v.setAlpha(1f);
                    removeMultiSelectedMedia();
                    updateAspectRatioToggleBehavior();
                }
            }
        });
    }

    private void initGalleryViews(View view) {

        bucketSpinner = view.findViewById(R.id.bucket_spinner);
        bucketAdapter = new ArrayAdapter<BucketBean>(getContext(), android.R.layout.simple_list_item_1);
        bucketSpinner.setAdapter(bucketAdapter);
        bucketSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int type = (position == 0) ? 2 : 1;
                galleryViewModel.getFilteredGalleryList((bucketAdapter.getItem(position)).getBucketId(), type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
                rvGallery.smoothScrollToPosition(position);
                resetOldView(currentMediaSelectedView);
                currentMediaSelectedView = view;
                if (!isMultipleMediaSelected()) {
                    if (object.getType() == 1) {
                        updateImage(object.getUri());
                    } else if (object.getType() == 3) {
                        updateVideo(object.getUri());
                    }
                    currentMediaSelectedView.setAlpha(0.4f);
                } else {
                    if (object.getType() == 1) {
                        int state = addImage(object.getUri());
                        updateViewState(view, state, position, object);
                    } else if (object.getType() == 3) {
                        int state = addVideo(object.getUri());
                        updateViewState(view, state, position, object);
                    }
                }
            }

            @Override
            public boolean onLongClick(View view, int position, MediaBean object, int requestCode) {
                return false;
            }
        });

        rvGallery.setAdapter(adapter);
        initViewModel();
    }

    private void resetOldView(View currentMediaSelectedView) {
        if (currentMediaSelectedView != null) {
            currentMediaSelectedView.setAlpha(1f);
        }
    }

    private void updateViewState(View view, int state, int position, MediaBean object) {
        switch (state) {
            case MediaState.MEDIA_ADDED:
            case MediaState.MEDIA_BROUGHT_TO_TOP:
                currentMediaSelectedView.findViewById(R.id.iv_select)
                        .setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));
                view.setAlpha(0.4f);
                break;
            case MediaState.MEDIA_REMOVED:
                currentMediaSelectedView.findViewById(R.id.iv_select)
                        .setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
                view.setAlpha(1f);
                break;
        }
    }

    private int addImage(Uri imageUri) {
        updateAspectRatioToggleBehavior();
        return imageSelectedToAdd(imageUri);
    }

    private int addVideo(Uri videoUri) {
        return videoSelectedToAdd(videoUri);
    }

    @Override
    protected void updateImage(Uri imageUri) {
        super.updateImage(imageUri);
        updateAspectRatioToggleBehavior();
    }

    private void updateAspectRatioToggleBehavior() {
        if (getIsAspectToggleEnable() && !isMultipleMediaSelected())
            flAspectRatio.setVisibility(View.VISIBLE);
        else flAspectRatio.setVisibility(View.GONE);
    }

    private void initViewModel() {
        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        galleryViewModel.getMediaList().observe(this, mediaListObserver);
        galleryViewModel.getAlbumsArray().observe(this, bucketListObserver);
    }

    @Override
    public void cropImage() {
        super.cropImage();
    }
}
