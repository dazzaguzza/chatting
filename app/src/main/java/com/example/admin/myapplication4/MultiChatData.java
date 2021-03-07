package com.example.admin.myapplication4;

public class MultiChatData {

    String image_multi,id_multi;
    boolean checked;

    public MultiChatData(String image_multi, String id_multi) {
        this.image_multi = image_multi;
        this.id_multi = id_multi;
    }

    public String getImage_multi() {
        return image_multi;
    }

    public void setImage_multi(String image_multi) {
        this.image_multi = image_multi;
    }

    public String getId_multi() {
        return id_multi;
    }

    public void setId_multi(String id_multi) {
        this.id_multi = id_multi;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
