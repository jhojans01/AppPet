package com.example.apppet.laboratorio;

public class Animal {
    private String name;
    private int imageResourceId;
    private String description;

    public Animal(String name, int imageResourceId, String description) {
        this.name = name;
        this.imageResourceId = imageResourceId;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public String getDescription() {
        return description;
    }

    // Este método se usa para mostrar el nombre en el ListView
    @Override
    public String toString() {
        return name;
    }
}


