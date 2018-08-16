package com.abhinav.instacropperpicker.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abhinav.instacropperpicker.R;
import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.SessionType;

import java.io.File;

public class VideoFragment extends CameraBaseFragment {

    private CameraView cameraView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        cameraView = view.findViewById(R.id.cameraView2);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cameraView.setSessionType(SessionType.VIDEO);
    }

    @Override
    public CameraView getCameraView() {
        return cameraView;
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
            public void onVideoTaken(File video) {
                super.onVideoTaken(video);
            }
        });
    }
}
