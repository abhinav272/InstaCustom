package com.abhinav.instacropperpicker.repo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.abhinav.instacropperpicker.App;
import com.abhinav.instacropperpicker.bean.BucketBean;
import com.abhinav.instacropperpicker.bean.MediaBean;

import java.util.List;

public class GalleryViewModel extends ViewModel {

    private GalleryRepository galleryRepository;
    private LiveData<List<MediaBean>> mediaList;
    private LiveData<List<BucketBean>> albumsArray;

    public GalleryViewModel() {
        this.galleryRepository = GalleryRepository.getInstance(App.getInstance());
        mediaList = galleryRepository.getMedialist();
        albumsArray = galleryRepository.getAlbumsArray();
    }

    public LiveData<List<MediaBean>> getMediaList() {
        return mediaList;
    }

    public LiveData<List<BucketBean>> getAlbumsArray(){
        return albumsArray;
    }

    public void getFilteredGalleryList(String bucketId, int type){
        galleryRepository.getFilteredAlbumsList(bucketId,type);
    }

}
