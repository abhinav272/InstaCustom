package com.abhinav.instacropperpicker.bean;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class MediaBean implements Parcelable {
    private Uri uri;
    private String path;
    private String videoPath;
    private boolean selected;
    private int position = -1;
    private int type;

    public MediaBean(){}

    protected MediaBean(Parcel in) {
        uri = in.readParcelable(Uri.class.getClassLoader());
        path = in.readString();
        videoPath = in.readString();
        selected = in.readByte() != 0;
        position = in.readInt();
        type = in.readInt();
    }

    public static final Creator<MediaBean> CREATOR = new Creator<MediaBean>() {
        @Override
        public MediaBean createFromParcel(Parcel in) {
            return new MediaBean(in);
        }

        @Override
        public MediaBean[] newArray(int size) {
            return new MediaBean[size];
        }
    };

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(uri, flags);
        dest.writeString(path);
        dest.writeString(videoPath);
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeInt(position);
        dest.writeInt(type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final MediaBean other = (MediaBean) obj;
        if (uri == null) {
            return other.uri == null;
        } else return uri.equals(other.uri);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }

}
