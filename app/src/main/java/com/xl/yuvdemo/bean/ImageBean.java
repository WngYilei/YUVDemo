package com.xl.yuvdemo.bean;

/**
 * @Author : wyl
 * @Date : 2022/4/28
 * Desc :
 */
public class ImageBean {

    int height;
    int wight;
    byte[] data;

    public ImageBean(int height, int wight, byte[] data) {
        this.height = height;
        this.wight = wight;
        this.data = data;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWight() {
        return wight;
    }

    public void setWight(int wight) {
        this.wight = wight;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
