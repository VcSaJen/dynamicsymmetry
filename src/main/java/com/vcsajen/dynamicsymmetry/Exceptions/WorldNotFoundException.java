package com.vcsajen.dynamicsymmetry.exceptions;

/**
 * Created by VcSaJen on 23.01.2016.
 */
public class WorldNotFoundException extends Exception {
    public WorldNotFoundException() {
        super("World not found!");
    }

    public WorldNotFoundException(String message) {
        super(message);
    }

    public WorldNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorldNotFoundException(Throwable cause) {
        super(cause);
    }
}
