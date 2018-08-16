package com.abhinav.instacropperpicker.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.abhinav.instacropperpicker.FileUtils;
import com.abhinav.instacropperpicker.R;
import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.SessionType;

import java.io.File;

public class CameraFragment extends Fragment {

    private CameraView cameraView;
    private SessionType currentSessionType;
    private Button record;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        cameraView = view.findViewById(R.id.cameraView);
        record = view.findViewById(R.id.btn_record);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onCameraError(@NonNull CameraException exception) {
                super.onCameraError(exception);
                Log.e("onCameraError: ", exception.getMessage(), exception);
            }

            @Override
            public void onPictureTaken(byte[] jpeg) {
                super.onPictureTaken(jpeg);
                CameraUtils.decodeBitmap(jpeg, new CameraUtils.BitmapCallback() {
                    @Override
                    public void onBitmapReady(Bitmap bitmap) {
                        Log.e("onBitmapReady: ", " ");
                    }
                });
            }

            @Override
            public void onVideoTaken(File video) {
                super.onVideoTaken(video);
            }
        });
    }

    /**
     * SessionType.PICTURE
     * SessionType.VIDEO
     */
    public void setSessionType(SessionType sessionType) {
        currentSessionType = sessionType;
        cameraView.setSessionType(sessionType);
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
    }

    public void updateListenerForImage() {
        record.setOnTouchListener(null);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.capturePicture();
            }
        });
    }

    public void updateListenerForVideo() {
        record.setOnClickListener(null);
        record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        cameraView.startCapturingVideo(FileUtils.getNextVideoFile());
                        break;
                    case MotionEvent.ACTION_UP:
                        cameraView.stopCapturingVideo();
                        break;
                }

                return false;
            }
        });
    }
}
