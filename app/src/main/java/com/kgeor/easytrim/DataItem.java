package com.kgeor.easytrim;

/**
 * DataItem class to get data values for recycler view
 *
 * @author Keegan George
 * @version 1.0
 */
public class DataItem {
    // FIELDS //
    private final int id;
    private final String name;
    private final int image;


    // CONSTRUCTOR //
    public DataItem(int id, String name, int image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

} // Data Item class end
