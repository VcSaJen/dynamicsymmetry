package com.vcsajen.dynamicsymmetry.exceptions;

/**
 * Created by VcSaJen on 23.01.2016.
 */
public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException() {
        super("Player not found!");
    }

    public PlayerNotFoundException(String message) {
        super(message);
    }

    public PlayerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerNotFoundException(Throwable cause) {
        super(cause);
    }
}
