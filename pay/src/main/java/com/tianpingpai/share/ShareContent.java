package com.tianpingpai.share;

import android.graphics.Bitmap;

public class ShareContent {
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    private String link;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;

    public Bitmap getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(Bitmap thumbImage) {
        this.thumbImage = thumbImage;
    }

    private Bitmap thumbImage;
}
