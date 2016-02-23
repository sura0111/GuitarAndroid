package com.example.sura.guitar;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class GString {
    Bitmap image;
    int height, width;
    int x,y;
    int currentFrame=0;
    int sheight, swidth;
    Main.DisplayView dv;

    public GString(Main.DisplayView displayView,Bitmap image1){
        image = image1;
        height = image.getHeight();
        width = image.getWidth()/12;
        dv=displayView;
        x = 0; y = 0;
    }
    public void setWH(float w, float h){
        sheight= (int) h;
        swidth= (int) (w/12);
    }
    public void onDraw(Canvas canvas, int frame1, int frame2){
        currentFrame = (frame1*6 + frame2);
        int srcX = currentFrame*width;
        Rect src = new Rect(srcX, 0, srcX+width, height);
        Rect dst = new Rect(frame2*swidth*2, 0, 2*(frame2*swidth+swidth), sheight);
        canvas.drawBitmap(image, src, dst, null);
    }
}
