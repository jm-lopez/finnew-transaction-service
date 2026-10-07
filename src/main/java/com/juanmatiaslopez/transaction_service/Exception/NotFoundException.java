package com.juanmatiaslopez.transaction_service.Exception;

public class NotFoundException extends RuntimeException{

    public NotFoundException(String msg) {
        super(msg);
    }
}
