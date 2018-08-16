package com.abhinav.instacropperpicker;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;

import com.yalantis.ucrop.view.UCropView;

public class CropContainerView extends UCropView {

    private Uri inputUri, outputUri;

    public CropContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CropContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Uri getInputUri() {
        return inputUri;
    }

    public void setInputImageUri(Uri inputUri) {
        this.inputUri = inputUri;
    }

    public Uri getOutputUri() {
        return outputUri;
    }

    public void setOutputUri(Uri outputUri) {
        this.outputUri = outputUri;
    }

    @Override
    public void resetCropImageView() {
        super.resetCropImageView();
        inputUri = null;
        outputUri = null;
    }
}
