package com.abhinav.instacropperpicker.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abhinav.instacropperpicker.R;
import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.SessionType;

public class PictureFragment extends CameraBaseFragment {


    private CameraView cameraView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture, container, false);
        cameraView = view.findViewById(R.id.cameraView1);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cameraView.setSessionType(SessionType.PICTURE);
    }

    @Override
    public void setCameraListener() {
        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onCameraError(@NonNull CameraException exception) {
                super.onCameraError(exception);
                Log.e("onCameraError: ", exception.getMessage(), exception);
            }

            @Override
            public void onPictureTaken(byte[] jpeg) {
                super.onPictureTaken(jpeg);
            }
        });
    }

    @Override
    public CameraView getCameraView() {
        return cameraView;
    }
}
