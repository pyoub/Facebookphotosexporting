package org.hiddenfounders.pyoub.minifacebookphotosexporting;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.CheckBox;

import java.net.URL;

/**
 * Created by Ayoub on 10/07/2017.
 */

public class image_adapter {
    CheckBox checkBox ;
    Bitmap bitmap;
    String id;
    public image_adapter(Bitmap bp,CheckBox checkBox,String id){
        bitmap = bp;
        this.checkBox=checkBox;
        this.id=id;
    }



    public boolean getcheck() {
        return checkBox.isChecked();
    }

    public void setcheck(Boolean chechbox) {
        this.checkBox.setChecked(chechbox);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getId(){return id;}

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}