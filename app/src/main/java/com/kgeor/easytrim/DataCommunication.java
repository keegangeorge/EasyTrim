package com.kgeor.easytrim;

/**
 * An interface responsible for transmitting the current speed value
 * Used for comparing speed value with the value in the database
 */
public interface DataCommunication {
    void viewQueryResults(int speed); // get current speed value for COMPARING with database
}
