package com.example.ossp.prototype;

/**
 * Prototype interface for cloneable objects.
 * Implements the Prototype design pattern to allow object cloning.
 * 
 * @param <T> The type of object that can be cloned
 */
public interface Prototype<T> {
    /**
     * Creates and returns a deep copy of this object.
     * 
     * @return A new instance that is a clone of this object
     */
    T clone();
}
