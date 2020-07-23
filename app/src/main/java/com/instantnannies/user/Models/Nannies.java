package com.instantnannies.user.Models;

public class Nannies {
    public String getNanniesDesc() {
        return nanniesDesc;
    }

    public void setNanniesDesc(String nanniesDesc) {
        this.nanniesDesc = nanniesDesc;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }



    String nanniesDesc;
    String price;
    String childs_image;
    int currentPosition;
    String id;

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public String getChilds_image() {
        return childs_image;
    }

    public void setChilds_image(String childs_image) {
        this.childs_image = childs_image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Nannies(String nanniesDesc, String price, String childs_image, int currentPosition, String id) {
        this.nanniesDesc = nanniesDesc;
        this.price = price;
        this.childs_image = childs_image;
        this.currentPosition = currentPosition;
        this.id = id;
    }
}
