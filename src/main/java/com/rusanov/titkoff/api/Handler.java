package com.rusanov.titkoff.api;

/**
 * Handler interface to handle object and receive according object
 * @author Magnusyatina
 * @param <T> - generic type of processed object
 * @param <R> - generic type of received object
 */
public interface Handler <T, R>{

    /*
            * Method to handle object with generic type {@link T} and receive object with generic type {@link R}
     *
             * @param objects - handled param
     * @return object with generic type {@link R}
     */
    R handle(T ...objects);
}