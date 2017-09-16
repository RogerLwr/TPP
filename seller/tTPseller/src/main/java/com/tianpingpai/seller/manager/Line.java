package com.tianpingpai.seller.manager;

import android.graphics.Bitmap;

public class Line {
    public static final int ALIGNMENT_LEFT = 0;
    public static final int ALIGNMENT_MIDDLE = 1;
    public static final int ALIGNMENT_RIGHT = 2;

    private static Line sLineFeed = new Line();
    static {
        sLineFeed.setText("\n");
    }

    public static Line LineFeed(){
        return sLineFeed;
    }

    public Line(String text){
        this.text = text;
    }

    public Line(){

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private String text;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private Bitmap bitmap;
    private int alignment = ALIGNMENT_LEFT;

    public int getAlignment() {
        return alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }
}
