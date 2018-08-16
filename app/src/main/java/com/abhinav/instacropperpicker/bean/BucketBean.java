package com.abhinav.instacropperpicker.bean;

public class BucketBean {

    private String bucketId;
    private String bucketName;
    private int filesCount;

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public int getFilesCount() {
        return filesCount;
    }

    public void setFilesCount(int filesCount) {
        this.filesCount = filesCount;
    }

    @Override
    public String toString() {
        return bucketName;
    }
}
