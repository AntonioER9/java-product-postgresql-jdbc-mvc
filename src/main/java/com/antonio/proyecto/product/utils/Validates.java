package com.antonio.proyecto.product.utils;

import com.antonio.proyecto.product.exception.InvalidProductDataException;

public class Validates {
    public static <T extends Number> void validate( T value, String message ) throws InvalidProductDataException {
        if(value==null){
            throw new InvalidProductDataException(message);
        }
    }
    public static <T> void validateObject( T obj, String message ) throws InvalidProductDataException {
        if(obj==null){
            throw new InvalidProductDataException(message);
        }
    }

    public static void validateText( String text, String message ) throws InvalidProductDataException {
        if(text==null || text.isEmpty()){
            throw new InvalidProductDataException(message);
        }
    }
}
