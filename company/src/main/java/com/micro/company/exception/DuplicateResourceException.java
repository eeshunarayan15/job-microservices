package com.micro.company.exception;

import java.io.Serial;

public class DuplicateResourceException extends  RuntimeException {
    @Serial
    private  static  final long serialVersionUID =1L;
    public  DuplicateResourceException(String message){
        super(message);
    }

}
