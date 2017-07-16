package org.hiddenfounders.pyoub.minifacebookphotosexporting;

import android.graphics.Bitmap;

public class ImageItem {
    private Bitmap image;
    private String title;
    private String id;
    public ImageItem(Bitmap image, String title ,String id) {
        super();
        this.image = image;
        this.title = title;
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
