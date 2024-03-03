package com.bitconex.danalyst.funmodmap.boundary;

public class FuncModelBoundaryException extends RuntimeException{
    public FuncModelBoundaryException(String message) {
        super(message);
    }

    public FuncModelBoundaryException(String message, Exception cause) {
        super(message, cause);
    }

}
