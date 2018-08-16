package com.abhinav.instacropperpicker.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;

public abstract class CameraBaseFragment extends Fragment {

    @NonNull
    private CameraView cameraView;
    private Facing currentFacing;


    public abstract void setCameraListener();

    public abstract CameraView getCameraView();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cameraView = getCameraView();
        setCameraListener();
    }

    public void flipCamera() {
        currentFacing = cameraView.getFacing();

        if (currentFacing == Facing.BACK) {
            cameraView.setFacing(Facing.FRONT);
            currentFacing = Facing.FRONT;
        } else {
            cameraView.setFacing(Facing.BACK);
            currentFacing = Facing.BACK;
        }
    }



}
