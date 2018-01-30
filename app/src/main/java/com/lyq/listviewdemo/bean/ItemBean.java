package com.lyq.listviewdemo.bean;

public class ItemBean {

    private String text;
    private int imageId;

    public ItemBean(String text, int inmageId) {
        this.text = text;
        this.imageId = inmageId;
    }

    public String getText() {
        return text;
    }

    public int getImageId() {
        return imageId;
    }
}
