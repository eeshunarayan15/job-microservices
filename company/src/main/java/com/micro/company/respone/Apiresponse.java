package com.micro.company.respone;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class Apiresponse<T>{
    public  String status;
    public  String message;
    public T data;


}
