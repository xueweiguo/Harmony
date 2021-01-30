package com.example.helloharmony;

public class SampleItem {
    private String name;
    private int layout;
    private int item;
    public SampleItem(String name, int layout, int item) {
        this.name = name;
        this.layout = layout;
        this.item = item;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getLayout(){
        return layout;
    }

    public int getItem(){
        return item;
    }
}